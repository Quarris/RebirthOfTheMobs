package quarris.rotm.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import quarris.rotm.ROTM;
import quarris.rotm.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityConfig implements ISubConfig {
    @Config.Name("Cancel Potion Effects")
    @Config.Comment({
            "Cancels a potion effect being able to be applied to a specified entity.",
            "Format: \"<modid:entity>;<effect1>;<effect2>;...<effectN>\"",
            "Where: <modid:entity> is the entity from the 'modid' mod for which to cancel the <effect#> potion effect.",
            "Example:",
            "S:\"Cancel Potion Effects\" <",
            "   minecraft:player;minecraft:strength;minecraft:regeneration;minecraft:weakness",
            "   minecraft:zombie;minecraft:invisibility;minecraft:speed",
            ">"
    })
    public String[] rawCancelPotions = new String[]{};
    @Config.Ignore
    public final Multimap<ResourceLocation, ResourceLocation> potionsToCancel = HashMultimap.create();

    @Config.Name("Cancel Damage Sources")
    @Config.Comment({
            "Cancels a damage source affecting a specified entity.",
            "Format: \"<modid:entity>;<sourceName1>;<sourceName2>;...<sourceNameN>\"",
            "Where: <modid:entity> is the entity from the 'modid' mod for which to cancel the <sourceName#> potion effect.",
            "Example: ",
            "S:\"Cancel Damage Sources\" <",
            "   minecraft:player;indirectMagic",
            "   minecraft:zombie;onFire;indirectMagic",
            ">"

    })
    public String[] rawCancelDamage = new String[]{};
    @Config.Ignore
    public final Multimap<ResourceLocation, String> damagesToCancel = HashMultimap.create();

    @Config.Name("Summon Spawns")
    @Config.Comment({
            "Summon Spawns allows mobs to be summoned when an entity has a target and reaches a certain health.",
            "Format: <modid:master>;<modid:summon>;<healthPercentage>;<spawnRange>;<cooldownRange>;<bypass>;<despawnOnDeath>;<maxCap>;<disableXP>;<disableLoot>;<sound>;?<id>",
            "Where: <modid:master> and <modid:summon> are the entities for the master and the mob to summon respectively.",
            "<healthPercentage> is a value between 0 and 100 (inclusive) which determines the health that the master entity has to be at to spawn the summon",
            "<spawnRange> is the min-max range of summons that can spawn in one cycle. Additionally higher value determines the maximum amount of this summon entity that can exist by the master entity.",
            "<cooldownRange> is the min-max range of seconds to wait between each cycle.",
            "<bypass> is a true/false value. If set to true it will ignore the <maxSpawn> restriction and allow more summons to spawn.",
            "<despawnOnDeath> will cause all summons of this type to die when the master entity dies.",
            "<maxCap> is the maximum amount of this summon type that can ever be spawned by this entity. Set this to 0 or less to disable",
            "<disableXP> and <disableLoot> are true/false values and will make it so that the summoned entities do not drop XP or Loot respectively",
            "<sound> is the sound that will be played when the summon happens",
            "?<id> optional number if you want to have a master:summon combo more than once. This has to be unique."
    })
    public String[] rawSummonSpawns = new String[]{};
    @Config.Ignore
    public final Multimap<ResourceLocation, SummonType> summonSpawns = HashMultimap.create();


    @Override
    public void onConfigChanged() {
        this.updatePotionsConfig();
        this.updateDamageSourceConfigs();
        this.updateSummonSpawnConfigs();
        System.out.println(this.summonSpawns);
    }

    private void updatePotionsConfig() {
        this.potionsToCancel.clear();
        for (String s : this.rawCancelPotions) {
            String[] split = s.split(";");
            if (split.length < 2) {
                ROTM.logger.warn("Invalid format for CancelPotionEffect config; skipping {}", s);
                continue;
            }

            ResourceLocation entity = new ResourceLocation(split[0]);
            if (!isEntityValid(entity, "CancelPotionEffect")) {
                continue;
            }

            List<ResourceLocation> potions = new ArrayList<>();
            for (int i = 1; i < split.length; i++) {
                ResourceLocation potion = new ResourceLocation(split[i]);
                if (!ForgeRegistries.POTIONS.containsKey(potion)) {
                    ROTM.logger.warn("Potion does not exist for CancelPotionEffect config; skipping {}", potion.toString());
                    continue;
                }
                potions.add(potion);
            }

            this.potionsToCancel.putAll(entity, potions);
        }
    }

    public void updateDamageSourceConfigs() {
        this.damagesToCancel.clear();
        for (String s : this.rawCancelDamage) {
            String[] split = s.split(";");
            if (split.length < 2) {
                ROTM.logger.warn("Invalid format for CancelDamageSource config; skipping {}", s);
                continue;
            }

            ResourceLocation entity = new ResourceLocation(split[0]);
            if (!isEntityValid(entity, "CancelDamageSource")) {
                continue;
            }

            List<String> sources = new ArrayList<>();
            sources.addAll(Arrays.asList(split));
            sources.remove(0);
            this.damagesToCancel.putAll(entity, sources);
        }
    }

    public void updateSummonSpawnConfigs() {
        this.summonSpawns.clear();
        for (String s : this.rawSummonSpawns) {
            try {
                String[] split = s.split(";");
                if (split.length < 11) {
                    ROTM.logger.warn("Invalid format for Summon Spawn config; skipping {}", s);
                    continue;
                }

                int id = split.length == 12 ? Integer.parseInt(split[11]) : 0;

                ResourceLocation master = new ResourceLocation(split[0]);
                if (!isEntityValid(master, "Summon Spawn")) {
                    continue;
                }

                ResourceLocation summon = new ResourceLocation(split[1]);
                if (this.summonSpawns.containsKey(master)) {
                    if (this.summonSpawns.get(master).stream().anyMatch(entry -> entry.summon.equals(summon) && entry.id == id)) {
                        ROTM.logger.warn("Entity {} with summon {} and id {} already exist for Summon Spawns. Add a unique id number at the end; skipping {}", master, summon, id, s);
                        continue;
                    }
                }
                if (!isEntityValid(summon, "Summon Spawn")) {
                    continue;
                }

                float healthPerc = Float.parseFloat(split[2]) / 100;

                int minSpawn;
                int maxSpawn;
                String[] spawnRange = split[3].split("-");
                int minSRange = Integer.parseInt(spawnRange[0]);
                if (spawnRange.length == 1) {
                    minSpawn = minSRange;
                    maxSpawn = minSRange;
                } else {
                    minSpawn = minSRange;
                    maxSpawn = Integer.parseInt(spawnRange[1]);
                }

                int minCooldown;
                int maxCooldown;
                String[] cdRange = split[4].split("-");
                int minCRange = Integer.parseInt(cdRange[0]);
                if (cdRange.length == 1) {
                    minCooldown = minCRange;
                    maxCooldown = minCRange;
                } else {
                    minCooldown = minCRange;
                    maxCooldown = Integer.parseInt(cdRange[1]);
                }

                boolean bypassMax = Boolean.parseBoolean(split[5]);
                boolean despawnOnDeath = Boolean.parseBoolean(split[6]);

                int maxCap = Integer.parseInt(split[7]);

                boolean disableXP = Boolean.parseBoolean(split[8]);
                boolean disableLoot = Boolean.parseBoolean(split[9]);

                ResourceLocation sound = new ResourceLocation(split[10]);
                if (!ForgeRegistries.SOUND_EVENTS.containsKey(sound)) {
                    ROTM.logger.warn("Sound does not exist for Summon Spawn config, skipping; {}", sound.toString());
                    continue;
                }

                SummonType summonType = new SummonType(summon, id, healthPerc, minSpawn, maxSpawn, minCooldown, maxCooldown, bypassMax, despawnOnDeath, maxCap, disableXP, disableLoot, sound);

                summonSpawns.put(master, summonType);
            } catch (Exception e) {
                ROTM.logger.warn("Error parsing Summon Spawn config, skipping; {}", s);
            }
        }
    }


    private static boolean isEntityValid(ResourceLocation entity, String configType) {
        if (!Utils.doesEntityExist(entity)) {
            ROTM.logger.warn("Entity does not exist for {} config, skipping; {}", configType, entity.toString());
            return false;
        }
        return true;
    }
}
