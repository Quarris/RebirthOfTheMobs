package quarris.rotm.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class Utils {

    public static final ResourceLocation PLAYER_RES = new ResourceLocation("player");

    public static boolean doesEntityExist(ResourceLocation entityName) {
        return entityName.toString().equals("minecraft:player") || EntityList.isRegistered(entityName);
    }

    public static ResourceLocation getEntityName(Entity entity) {
        if (entity instanceof EntityPlayer)
            return PLAYER_RES;
        else return EntityList.getKey(entity);
    }
}
