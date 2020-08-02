package quarris.rotm.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import quarris.rotm.ROTM;
import quarris.rotm.config.types.*;
import quarris.rotm.config.utils.StringConfig;
import quarris.rotm.config.utils.StringConfigException;
import quarris.rotm.utils.Settable;
import quarris.rotm.utils.Utils;

import java.util.ArrayList;
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
    public final SetMultimap<ResourceLocation, ResourceLocation> potionsToCancel = HashMultimap.create();

    @Config.Name("Cancel Damage Sources")
    @Config.Comment({
            "Cancels a damage source affecting a specified entity.",
            "Format: \"<modid:entity>;<damageSource>;?<modid:source>\"",
            "Where: <modid:entity> is the entity.",
            "<damageSource> is the name of the damage dealt.",
            "?<modid:source> is the optional entity that needs to deal the specified damage",
            "Example: ",
            "S:\"Cancel Damage Sources\" <",
            "   minecraft:player;indirectMagic",
            "   minecraft:zombie;onFire",
            "   minecraft:zombie;indirectMagic",
            "   minecraft:player;arrow;minecraft:stray",
            ">"
    })
    public String[] rawCancelDamage = new String[]{};
    @Config.Ignore
    public final SetMultimap<Pair<ResourceLocation, ResourceLocation>, String> damagesToCancel = HashMultimap.create();

    @Config.Name("Summon Spawns")
    @Config.Comment({
            "Summon Spawns allows mobs to be summoned when an entity has a target and reaches a certain health.",
            "Format: <modid:master>;<modid:summon>;<healthPercentage>;<spawnRange>;<cooldownRange>;<bypass>;<despawnOnDeath>;<maxCap>;<disableXP>;<disableLoot>;<?sound>;?<id>;?<nbt>",
            "Where: <modid:master> and <modid:summon> are the entities for the master and the mob to summon respectively.",
            "<healthPercentage> is a value between 0 and 100 (inclusive) which determines the health that the master entity has to be at to spawn the summon",
            "<spawnRange> is a '-' separated min-max range of summons that can spawn in one cycle. Additionally higher value determines the maximum amount of this summon entity that can exist by the master entity.",
            "<cooldownRange> is a '-' separated range of seconds to wait between each cycle.",
            "<bypass> is a true/false value. If set to true it will ignore the <maxSpawn> restriction and allow more summons to spawn.",
            "<despawnOnDeath> is a true/false value and will cause all summons of this type to die when the master entity dies.",
            "<maxCap> is the maximum amount of this summon type that can ever be spawned by this entity. Set this to 0 or less to disable",
            "<disableXP> and <disableLoot> are true/false values and will make it so that the summoned entities do not drop XP or Loot respectively",
            "<?sound> is the (optional) sound that will be played when the summon happens",
            "?<nbt> optional NBT to apply to the summon on spawn.",
            "?<id> optional number if you want to have a master:summon combo more than once. This has to be unique.",
            "Example:",
            "S:\"Summon Spawns\" <",
            "   minecraft:zombie;minecraft:skeleton;100;1;3-5;false;true;0;true;true;minecraft:ambient.cave;{NoAI:1b}",
            "   minecraft:zombie;minecraft:skeleton;50;3-4;5-20;false;true;30;true;false;minecraft:entity.enderdragon.growl;1",
            "   minecraft:zombie;minecraft:bat;60;1;6-10;true;false;30;false;false;minecraft:ambient.cave",
            "   minecraft:player;minecraft:rabbit;80;3-7;5-20;false;true;0;false;true;minecraft:ambient.cave",
            ">",
    })
    public String[] rawSummonSpawns = new String[]{};

    @Config.Ignore
    public final Multimap<ResourceLocation, SummonSpawnType> summonSpawns = HashMultimap.create();

    @Config.Name("Death Spawns")
    @Config.Comment({
            "Death Spawns allows mobs to be summoned when an entity dies.",
            "Format: <modid:entity>;<modid:spawn>;<spawnRange>;<disableXP>;<disableLoot>;<?sound>;?<nbt>",
            "Where: <modid:master> and <modid:spawn> are the entities for the entity that dies and the mob that spawns respectively.",
            "<spawnRange> is the min-max range of summons that can spawn in one cycle.",
            "<disableXP> and <disableLoot> are true/false values and will make it so that the summoned entities do not drop XP or Loot respectively",
            "<?sound> is the (optional) sound that will be played when the summon happens",
            "?<nbt> optional NBT to apply to the summon on spawn.",
            "Example:",
            "S:\"Death Spawns\" <",
            "   minecraft:zombie;minecraft:spider;2-5;true;false;minecraft:ambient.cave",
            "   minecraft:zombie;minecraft:villager;1;false;false;minecraft:entity.enderdragon.growl;{NoAI:1b}",
            ">",
    })
    public String[] rawDeathSpawns = new String[]{};

    @Config.Ignore
    public final Multimap<ResourceLocation, DeathSpawnType> deathSpawns = HashMultimap.create();

    @Config.Name("Mob Offense")
    @Config.Comment({
            "Mob Offense allows to apply potions effects when a mob damages another entity.",
            "Format: <modid:entity>;<potionEffect>;<health>;<potionLevel>;<potionDuration>;<chance>;<?sound>;?<dimension>;?<damageType>",
            "Where: <modid:entity> is the entity that does the melee.",
            "<potionEffect> is the name of the potion effect to use.",
            "<health> is the percentage (0-100 exclusive-inclusive) that the entity has to be at to apply the effect.",
            "<potionLevel> is the tier of the potion starting at 0. For example, Poison I has level 0, Speed II has level 1.",
            "<potionDuration> is the duration in seconds of the potion.",
            "<chance> is the percentage (0-100 exclusive-inclusive) chance that the effect will take place on attack.",
            "<?sound> is the (optional) sound that will be played when the attack happens",
            "?<dimension> is an optional list of dimension ids in which the effect can/cannot be applied in. This takes form of '[1, -1, 2, 3, ...]'. You can prefix this list with '!' to turn it into a list of dimensions to block instead. For example '![0]' would block only the Overworld.",
            "?<damageType> is the kind of damage that this is triggered by. Leaving this empty results in every damage counting, 'mob' and 'player' results in mob and player melee only respectively, 'arrow' results in an arrow shot etc."
    })
    public String[] rawMobOffense = new String[]{};

    @Config.Ignore
    public final Multimap<ResourceLocation, MobOffenseType> mobOffense = HashMultimap.create();

    @Config.Name("Mob Defense")
    @Config.Comment({
            "Mob Defense allows to apply potions effects to the attacker when a mob takes damage.",
            "Format: <modid:entity><potionEffect>;<health>;<potionLevel>;<potionDuration>;<chance>;<?sound>;?<dimension>;?<damageType>",
            "Where: <modid:entity> is the entity that takes damage.",
            "<potionEffect> is the name of the potion effect to use.",
            "<health> is the percentage (0-100 exclusive-inclusive) that the entity has to be at to apply the effect.",
            "<potionLevel> is the tier of the potion starting at 0. For example, Poison I has level 0, Speed II has level 1.",
            "<potionDuration> is the duration in seconds of the potion.",
            "<chance> is the percentage (0-100 exclusive-inclusive) chance that the effect will take place on attack.",
            "<?sound> is the (optional) sound that will be played when the defense happens",
            "?<dimension> is an optional list of dimension ids in which the effect can/cannot be applied in. This takes form of '[1, -1, 2, 3, ...]'. You can prefix this list with '!' to turn it into a list of dimensions to block instead. For example '![0]' would block only the Overworld.",
            "?<damageType> is the kind of damage that this is triggered by. Leaving this empty results in every damage counting, 'mob' and 'player' results in mob and player melee only respectively, 'arrow' results in an arrow shot etc."
    })
    public String[] rawMobDefenses = new String[]{};

    @Config.Ignore
    public final Multimap<ResourceLocation, MobDefenseType> mobDefenses = HashMultimap.create();

    @Config.Name("Instant Health Regain")
    @Config.Comment({
            "Allows entities to regain health after killing enemies of certain target type",
            "Format: <modid:entity>;?<modid:target>;<healthPercentage>;<lastManStanding>;?<radius>",
            "Where: <modid:entity> is the entity which will regain health.",
            "?<modid:target> is the optional target entity which has to die in the region for the health regain to occur. If left empty, any entity which dies will count as a target",
            "<healthPercentage> is the health percentage (0-100 inclusive) of the 'entity's max health which will be regained.",
            "<lastManStanding> is a true/false value. If true, then the health regain will only happen if last target type has died nearby.",
            "?<radius> is an optional value representing the distance range to check. If left empty or <= 0 then the area is turned global and will check all loaded entities of the target type"
    })
    public String[] rawHealthRegains = new String[]{};

    @Config.Ignore
    public Multimap<ResourceLocation, HealthRegainType> healthRegains = HashMultimap.create();

    @Override
    public void onConfigChanged() {
        this.updatePotionsConfig();
        this.updateDamageSourceConfigs();
        this.updateSummonSpawnConfigs();
        this.updateDeathSpawnsConfig();
        this.updateMobOffensesConfigs();
        this.updateMobDefensesConfigs();
        this.updateHealthRegain();
    }

    private void updatePotionsConfig() {
        this.potionsToCancel.clear();
        for (String s : this.rawCancelPotions) {
            List<ResourceLocation> potions = new ArrayList<>();
            Settable<ResourceLocation> entity = Settable.create();
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(entity::set)
                        .rest().parseAs(ResourceLocation::new).validate(ForgeRegistries.POTIONS::containsKey).<ResourceLocation>accept(potions::add);
            } catch (StringConfigException exception) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, exception.getLocalizedMessage());
            }

            this.potionsToCancel.putAll(entity.get(), potions);
        }
    }

    private void updateDamageSourceConfigs() {
        this.damagesToCancel.clear();
        for (String s : this.rawCancelDamage) {
            Settable<String> damageType = Settable.create();
            Settable<ResourceLocation> entity = Settable.create();
            Settable<ResourceLocation> target = Settable.create();
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(entity::set)
                        .next().accept(damageType::set)
                        .next().optional(null).parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(target::set);
            } catch (StringConfigException exception) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, exception.getLocalizedMessage());
                continue;
            }

            this.damagesToCancel.put(Pair.of(entity.get(), target.get()), damageType.get());
        }
    }

    private void updateSummonSpawnConfigs() {
        this.summonSpawns.clear();
        for (String s : this.rawSummonSpawns) {
            SummonSpawnType.Builder builder = SummonSpawnType.builder();
            Settable<ResourceLocation> masterSetter = Settable.create();

            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(masterSetter::set)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(builder::summon)
                        .next().parseAs(Float::parseFloat).<Float>validate(i -> (i >= 0.0 && i <= 100.0)).accept(builder::health)
                        .next().parseAs(Integer::parseInt).<Integer>validateRange((min, max) -> min <= max).acceptRange(builder::minSpawn, builder::maxSpawn)
                        .next().parseAs(Integer::parseInt).<Integer>validateRange((min, max) -> min <= max).acceptRange(builder::minCooldown, builder::maxCooldown)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::bypassMaxSpawns)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::despawnOnDeath)
                        .next().parseAs(Integer::parseInt).accept(builder::cap)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::disableXP)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::disableLoot)
                        .next().optional(null).parseAs(ResourceLocation::new).validate(ForgeRegistries.SOUND_EVENTS::containsKey).accept(builder::sound)
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

    private void updateDeathSpawnsConfig() {
        this.deathSpawns.clear();
        for (String s : this.rawDeathSpawns) {
            DeathSpawnType.Builder builder = DeathSpawnType.builder();
            Settable<ResourceLocation> masterSetter = Settable.create();

            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(masterSetter::set)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(builder::summon)
                        .next().parseAs(Integer::parseInt).<Integer>validateRange((min, max) -> min <= max).acceptRange(builder::minSpawn, builder::maxSpawn)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::disableXP)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::disableLoot)
                        .next().optional(null).parseAs(ResourceLocation::new).validate(ForgeRegistries.SOUND_EVENTS::containsKey).accept(builder::sound)
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

            this.deathSpawns.put(masterSetter.get(), builder.build());
        }
    }

    private void updateMobOffensesConfigs() {
        this.mobOffense.clear();
        for (String s : this.rawMobOffense) {
            Settable<ResourceLocation> entity = Settable.create();
            MobOffenseType.Builder builder = MobOffenseType.builder();
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(entity::set)
                        .next().parseAs(ResourceLocation::new).validate(ForgeRegistries.POTIONS::containsKey).accept(builder::potion)
                        .next().parseAs(Float::parseFloat).<Float>validate(health -> health > 0 && health <= 100).accept(builder::health)
                        .next().parseAs(Integer::parseInt).<Integer>validate(level -> level >= 0).accept(builder::level)
                        .next().parseAs(Integer::parseInt).<Integer>validate(duration -> duration >= 0).accept(builder::duration)
                        .next().parseAs(Float::parseFloat).<Float>validate(chance -> chance > 0 && chance <= 100).accept(builder::chance)
                        .next().optional(null).parseAs(ResourceLocation::new).validate(ForgeRegistries.SOUND_EVENTS::containsKey).accept(builder::sound)
                        .next().optional("![]").parseAs(Integer::parseInt).validateList(DimensionManager::isDimensionRegistered).blockList(builder::blockDimensions).acceptList(builder::dimension)
                        .next().optional("").accept(builder::damageType);
            } catch (StringConfigException exception) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, exception.getLocalizedMessage());
            }
            this.mobOffense.put(entity.get(), builder.build());
        }
    }

    private void updateMobDefensesConfigs() {
        this.mobDefenses.clear();
        for (String s : this.rawMobDefenses) {
            Settable<ResourceLocation> entity = Settable.create();
            MobDefenseType.Builder builder = MobDefenseType.builder();
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(entity::set)
                        .next().parseAs(ResourceLocation::new).validate(ForgeRegistries.POTIONS::containsKey).accept(builder::potion)
                        .next().parseAs(Float::parseFloat).<Float>validate(health -> health > 0 && health <= 100).accept(builder::health)
                        .next().parseAs(Integer::parseInt).<Integer>validate(level -> level >= 0).accept(builder::level)
                        .next().parseAs(Integer::parseInt).<Integer>validate(duration -> duration >= 0).accept(builder::duration)
                        .next().parseAs(Float::parseFloat).<Float>validate(chance -> chance > 0 && chance <= 100).accept(builder::chance).next().optional(null).parseAs(ResourceLocation::new).validate(ForgeRegistries.SOUND_EVENTS::containsKey).accept(builder::sound)
                        .next().optional("![]").parseAs(Integer::parseInt).validateList(DimensionManager::isDimensionRegistered).blockList(builder::blockDimensions).acceptList(builder::dimension)
                        .next().optional("").accept(builder::damageType);
            } catch (StringConfigException exception) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, exception.getLocalizedMessage());
            }
            this.mobDefenses.put(entity.get(), builder.build());
        }
    }

    private void updateHealthRegain() {
        this.healthRegains.clear();
        for (String s : this.rawHealthRegains) {
            Settable<ResourceLocation> entity = Settable.create();
            HealthRegainType.Builder builder = HealthRegainType.builder();
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(entity::set)
                        .next().optional(null).parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(builder::target)
                        .next().parseAs(Float::parseFloat).<Float>validate(health -> health > 0 && health <= 100).accept(builder::healthPercentage)
                        .next().parseAs(Boolean::parseBoolean).accept(builder::lastManStanding)
                        .next().optional(0).parseAs(Float::parseFloat).accept(builder::radius);
            } catch (StringConfigException exception) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, exception.getLocalizedMessage());
            }
            this.healthRegains.put(entity.get(), builder.build());
        }
    }
}
