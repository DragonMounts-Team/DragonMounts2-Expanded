package net.dragonmounts.item;

import net.dragonmounts.init.DMItemGroups;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Created by TGG on 3/07/2015.
 * The Dragon Orb.
 */
public class DragonOrbItem extends Item {
    public static int getTintColor(ItemStack stack, int layer) {
        if (layer != 0) return -1;// claw
        // orb jewel
        final long GLOW_CYCLE_PERIOD_SECONDS = 4;
        final float MIN_GLOW_BRIGHTNESS = 0.4F;
        final float MAX_GLOW_BRIGHTNESS = 1.0F;
        final long NANO_SEC_PER_SEC = 1000L * 1000L * 1000L;
        long cyclePosition = System.nanoTime() % (GLOW_CYCLE_PERIOD_SECONDS * NANO_SEC_PER_SEC);
        double cyclePosRadians = 2 * Math.PI * cyclePosition / (GLOW_CYCLE_PERIOD_SECONDS * NANO_SEC_PER_SEC);
        final float BRIGHTNESS_MIDPOINT = (MIN_GLOW_BRIGHTNESS + MAX_GLOW_BRIGHTNESS) * 0.5F;
        final float BRIGHTNESS_AMPLITUDE = (MAX_GLOW_BRIGHTNESS - BRIGHTNESS_MIDPOINT);
        int brightness = MathHelper.clamp((int) (255 * (BRIGHTNESS_MIDPOINT + BRIGHTNESS_AMPLITUDE * MathHelper.sin((float) cyclePosRadians))), 0, 255);
        return ((brightness & 0xFF) << 16) | ((brightness & 0xFF) << 8) | (brightness & 0xFF);
    }

    public DragonOrbItem() {
        this.setMaxStackSize(1).setCreativeTab(DMItemGroups.ITEMS);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World level, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    /**
     * How long to hold the block action for
     */
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }
}