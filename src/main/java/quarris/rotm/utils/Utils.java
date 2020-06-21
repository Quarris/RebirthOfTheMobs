package quarris.rotm.utils;

import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

public class Utils {

    public static boolean doesEntityExist(ResourceLocation entityName) {
        return entityName.toString().equals("minecraft:player") || EntityList.isRegistered(entityName);
    }
}
