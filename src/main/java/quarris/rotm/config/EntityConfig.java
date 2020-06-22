package quarris.rotm.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import quarris.rotm.ROTM;
import quarris.rotm.config.utils.StringConfig;
import quarris.rotm.config.utils.StringConfigException;
import quarris.rotm.utils.Settable;
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
            "Format: <modid:master>;<modid:summon>;<healthPercentage>;<spawnRange>;<cooldownRange>;<bypass>;<despawnOnDeath>;<maxCap>;<disableXP>;<disableLoot>;<sound>;?<id>;?<nbt>",
            "Where: <modid:master> and <modid:summon> are the entities for the master and the mob to summon respectively.",
            "<healthPercentage> is a value between 0 and 100 (inclusive) which determines the health that the master entity has to be at to spawn the summon",
            "<spawnRange> is the min-max range of summons that can spawn in one cycle. Additionally higher value determines the maximum amount of this summon entity that can exist by the master entity.",
            "<cooldownRange> is the min-max range of seconds to wait between each cycle.",
            "<bypass> is a true/false value. If set to true it will ignore the <maxSpawn> restriction and allow more summons to spawn.",
            "<despawnOnDeath> will cause all summons of this type to die when the master entity dies.",
            "<maxCap> is the maximum amount of this summon type that can ever be spawned by this entity. Set this to 0 or less to disable",
            "<disableXP> and <disableLoot> are true/false values and will make it so that the summoned entities do not drop XP or Loot respectively",
            "<sound> is the sound that will be played when the summon happens",
            "?<nbt> optional NBT to apply to the summon on spawn.",
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
    }

    private void updatePotionsConfig() {
        this.potionsToCancel.clear();
        for (String s : this.rawCancelPotions) {
            List<ResourceLocation> potions = new ArrayList<>();
            Settable<ResourceLocation> entity = Settable.create();
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(EntityConfig::isEntityValid).accept(entity::set)
                        .rest().parseAs(ResourceLocation::new).validate(ForgeRegistries.POTIONS::containsKey).<ResourceLocation>accept(potions::add);
            } catch (StringConfigException exception) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, exception.getLocalizedMessage());
            }

            this.potionsToCancel.putAll(entity.get(), potions);
        }
    }

    public void updateDamageSourceConfigs() {
        this.damagesToCancel.clear();
        for (String s : this.rawCancelDamage) {
            List<String> damages = new ArrayList<>();
            Settable<ResourceLocation> entity = Settable.create();
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(EntityConfig::isEntityValid).accept(entity::set)
                        .rest().<String>accept(damages::add);
            } catch (StringConfigException exception) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, exception.getLocalizedMessage());
            }

            this.damagesToCancel.putAll(entity.get(), damages);
        }
    }

    public void updateSummonSpawnConfigs() {
        this.summonSpawns.clear();
        for (String s : this.rawSummonSpawns) {
            SummonType.Builder builder = SummonType.builder();
            Settable<ResourceLocation> masterSetter = Settable.create();

            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(EntityConfig::isEntityValid).accept(masterSetter::set)
                        .next().parseAs(ResourceLocation::new).validate(EntityConfig::isEntityValid).accept(builder::summon)
                        .next().parseAs(Float::parseFloat).<Float>validate(i -> (i >= 0.0 && i <= 100.0)).accept(builder::health)
                        .next().parseAs(Integer::parseInt).<Integer>validateRange((min, max) -> min <= max).acceptRange(builder::minSpawn, builder::maxSpawn)
                        .next().parseAs(Integer::parseInt).<Integer>validateRange((min, max) -> min <= max).acceptRange(builder::minCooldown, builder::maxCooldown)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::bypassMaxSpawns)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::despawnOnDeath)
                        .next().parseAs(Integer::parseInt).accept(builder::cap)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::disableXP)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::disableLoot)
                        .next().parseAs(ResourceLocation::new).validate(ForgeRegistries.SOUND_EVENTS::containsKey).accept(builder::sound)
                        .next().optional(0).parseAs(Integer::parseInt)
                        .<Integer>validate((id) -> {
                            if (this.summonSpawns.containsKey(masterSetter.get())) {
                                return this.summonSpawns.get(masterSetter.get()).stream().anyMatch(entry -> entry.summon.equals(builder.summon) != (id.equals(entry.id)));
                            }
                            return true;
                        }).accept(builder::id)
                        .next().optional(new NBTTagCompound())
                        .parseAs(str -> {
                            try {
                                return JsonToNBT.getTagFromJson(str);
                            } catch (NBTException e) {
                                e.printStackTrace();
                            }
                            return new NBTTagCompound();
                        }).accept(builder::nbt);
            } catch (StringConfigException exception) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, exception.getLocalizedMessage());
            }

            this.summonSpawns.put(masterSetter.get(), builder.build());
        }
    }

    private static boolean isEntityValid(ResourceLocation entity) {
        if (!Utils.doesEntityExist(entity)) {
            return false;
        }
        return true;
    }
}
