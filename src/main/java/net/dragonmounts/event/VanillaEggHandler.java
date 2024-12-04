package net.dragonmounts.event;

import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.util.DMUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handler for the vanilla dragon egg block
 * @deprecated - Should be handled in a different way. (Replacing the vanilla egg with our custom egg etc) This is a temporary solution
 * @author WolfShotz
 */
public class VanillaEggHandler {
		
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		World level = event.getWorld();
		if (level.isRemote) return; //do nothing on client world
		BlockPos pos = event.getPos();
		if (level.getBlockState(pos).getBlock() != Blocks.DRAGON_EGG) return; //ignore all other blocks
		if (DragonMountsConfig.isDisableBlockOverride()) return; //do nothing if config is set
		if (level.provider.getDimensionType() == DimensionType.THE_END) {
			event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation(DMUtils.translateToLocal("egg.cantHatchEnd.DragonMounts")), true);
			return;  //cant hatch in the end
		}
		HatchableDragonEggBlock.spawn(level, pos, DragonTypes.ENDER).getReproductionHelper().setBreeder(event.getEntityPlayer());
	}
}