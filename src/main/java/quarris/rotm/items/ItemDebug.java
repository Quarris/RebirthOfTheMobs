package quarris.rotm.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import quarris.rotm.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class ItemDebug extends Item {

    public DebugType type;

    public ItemDebug(DebugType type) {
        this.type = type;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote)
            return super.onItemRightClick(world, player, hand);

        if (player.isSneaking()) {
            String message;
            switch (this.type) {
                case DAMAGE: {
                    message = player.getEntityData().hasKey("LastDamageSource") ?
                            player.getEntityData().getString("LastDamageSource") :
                            "No damage taken!";
                    break;
                }
                case ENTITY: {
                    message = "minecraft:player";
                    break;
                }
                case POTION: {
                    List<String> activePotionEffectStrings = player.getActivePotionEffects().stream()
                            .map(effect -> effect.getPotion().getRegistryName().toString())
                            .collect(Collectors.toList());

                    message = activePotionEffectStrings.toString();
                    break;
                }
                default: {
                    message = "Invalid Debug Type";
                }
            }

            player.sendMessage(new TextComponentString(message));
            return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (player.world.isRemote)
            return false;

        String message;
        switch (this.type) {
            case DAMAGE: {
                message = target.getEntityData().hasKey("LastDamageSource") ?
                        target.getEntityData().getString("LastDamageSource") :
                        "No damage taken!";
                break;
            }
            case ENTITY: {
                message = Utils.getEntityName(target).toString();
                break;
            }
            case POTION: {
                List<String> activePotionEffectStrings = target.getActivePotionEffects().stream()
                        .map(effect -> effect.getPotion().getRegistryName().toString())
                        .collect(Collectors.toList());

                message = activePotionEffectStrings.toString();
                break;
            }
            default: {
                message = "Invalid Bug Type";
            }
        }
        player.sendMessage(new TextComponentString(message));
        return super.itemInteractionForEntity(stack, player, target, hand);
    }

    public enum DebugType {
        DAMAGE, ENTITY, POTION
    }
}
