package quarris.rotm.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import quarris.rotm.config.ModConfigs;
import quarris.rotm.items.ModItems;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerItemModels() {
        if (ModConfigs.debugConfigs.enableDebugMode) {
            this.registerItemModel(ModItems.debugDamages, "inventory");
            this.registerItemModel(ModItems.debugEntities, "inventory");
            this.registerItemModel(ModItems.debugPotions, "inventory");
        }
    }

    private void registerItemModel(Item item, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), variant));
    }
}
