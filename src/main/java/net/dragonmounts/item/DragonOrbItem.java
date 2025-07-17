package net.dragonmounts.item;

import net.dragonmounts.init.DMItemGroups;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Created by TGG on 3/07/2015.
 * The Dragon Orb.
 */
public class DragonOrbItem extends Item {
    public DragonOrbItem() {
        this.setMaxStackSize(1).setCreativeTab(DMItemGroups.MAIN);
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