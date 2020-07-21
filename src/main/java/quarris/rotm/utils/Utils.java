package quarris.rotm.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static net.minecraft.world.WorldEntitySpawner.isValidEmptySpawnBlock;

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

    public static boolean canEntitySpawn(IBlockAccess world, BlockPos pos) {
        IBlockState iblockstate = world.getBlockState(pos);

        BlockPos downPos = pos.down();
        IBlockState downState = world.getBlockState(downPos);


        if (downState.getMaterial().isSolid()) {
            Block block = downState.getBlock();
            boolean flag = block != Blocks.BEDROCK && block != Blocks.BARRIER;
            return flag && isValidEmptySpawnBlock(iblockstate) && isValidEmptySpawnBlock(world.getBlockState(pos.up()));
        }

        return false;
    }

    public static boolean isEntityInCollision(World world, Entity entity) {
       return world.collidesWithAnyBlock(entity.getEntityBoundingBox());
    }
}
