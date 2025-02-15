package net.dragonmounts.event;

import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DragonTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonMisc {
    @SubscribeEvent
    public static void onDismount(EntityMountEvent event) {
        if (event.isMounting()) return;
        Entity passenger = event.getEntityMounting();
        if (passenger instanceof EntityPlayerMP) {
            Entity vehicle = event.getEntityBeingMounted();
            if (vehicle instanceof TameableDragonEntity) {
                passenger.setPositionAndUpdate(
                        vehicle.posX,
                        vehicle.posY - ((TameableDragonEntity) vehicle).getScale() * 0.2,
                        vehicle.posZ
                );
            }
        }
    }

    /**
     * TODO: Should be handled in a different way. (Replacing the vanilla egg with our custom egg etc) This is a temporary solution
     *
     * @author WolfShotz
     */
    @SubscribeEvent
    public static void tryHatchVanillaEgg(PlayerInteractEvent.RightClickBlock event) {
        World level = event.getWorld();
        if (level.isRemote) return; //do nothing on client world
        BlockPos pos = event.getPos();
        if (level.getBlockState(pos).getBlock() != Blocks.DRAGON_EGG) return; //ignore all other blocks
        if (!DMConfig.BLOCK_OVERRIDE.value) return; //do nothing if config is set
        if (level.provider.getDimensionType() == DimensionType.THE_END) {
            event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("message.dragonmounts.egg.wrongDimension"), true);
            return;  //cant hatch in the end
        }
        HatchableDragonEggBlock.spawn(level, pos, DragonTypes.ENDER);
    }
}
