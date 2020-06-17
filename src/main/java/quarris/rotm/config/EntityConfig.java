package quarris.rotm.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import quarris.rotm.ROTM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityConfig implements ISubConfig {
    @Config.Comment({
            "Cancels a potion effect being able to be applied to a specified entity.",
            "Format: \"<modid:entity>;<effect1>;<effect2>;...<effectN>\"",
            "Where: <modid:entity> is the entity from the 'modid' mod for which to cancel the <effect#> potion effect.",
            "Each entity and potion pair on a new line.",
            "Example:",
            "S:\"Cancel Potion Effects\" <",
            "   minecraft:player;minecraft:strength;minecraft:regeneration;minecraft:weakness",
            "   minecraft:zombie;minecraft:invisibility;minecraft:speed",
            ">"
    })
    @Config.Name("Cancel Potion Effects")
    public String[] cancelPotionEffects = new String[]{};
    @Config.Ignore
    public final Multimap<ResourceLocation, ResourceLocation> potionsToCancel = HashMultimap.create();

    @Config.Comment({
            "Cancels a damage source affecting a specified entity.",
            "Format: \"<modid:entity>;<sourceName1>;<sourceName2>;...<sourceNameN>\"",
            "Where: <modid:entity> is the entity from the 'modid' mod for which to cancel the <sourceName#> potion effect.",
            "Each entity and potion pair on a new line."

    })
    @Config.Name("Cancel Damage Sources")
    public String[] cancelDamageSources = new String[]{};
    @Config.Ignore
    public final Multimap<ResourceLocation, String> damageSourcesToCancel = HashMultimap.create();

    @Override
    public void onConfigChanged() {
        this.potionsToCancel.clear();
        this.damageSourcesToCancel.clear();

        this.updatePotionsConfig();
        this.updateDamageSourceConfigs();
    }

    private void updatePotionsConfig() {
        for (String s : this.cancelPotionEffects) {
            String[] split = s.split(";");
            if (split.length < 2) {
                ROTM.logger.warn("Invalid format for CancelPotionEffect config, skipping; {}", s);
            }

            ResourceLocation entity = new ResourceLocation(split[0]);
            if (!entity.toString().equals("minecraft:player") && !EntityList.isRegistered(entity)) {
                ROTM.logger.warn("Entity does not exist for CancelPotionEffect config, skipping; {}", entity.toString());
                continue;
            }

            List<ResourceLocation> potions = new ArrayList<>();
            for (int i = 1; i < split.length; i++) {
                ResourceLocation potion = new ResourceLocation(split[i]);
                if (!ForgeRegistries.POTIONS.containsKey(potion)) {
                    ROTM.logger.warn("Potion does not exist for CancelPotionEffect config, skipping; {}", potion.toString());
                    continue;
                }
                potions.add(potion);
            }

            this.potionsToCancel.putAll(entity, potions);
        }
    }

    public void updateDamageSourceConfigs() {
        for (String s : this.cancelDamageSources) {
            String[] split = s.split(";");
            if (split.length < 2) {
                ROTM.logger.warn("Invalid format for CancelDamageSource config; {}", s);
            }

            ResourceLocation entity = new ResourceLocation(split[0]);
            if (!entity.toString().equals("minecraft:player") && !EntityList.isRegistered(entity)) {
                ROTM.logger.warn("Entity does not exist for CancelDamageSource config, skipping; {}", entity.toString());
                continue;
            }

            List<String> sources = new ArrayList<>();
            sources.addAll(Arrays.asList(split));
            sources.remove(0);
            this.damageSourcesToCancel.putAll(entity, sources);
        }
    }
}
