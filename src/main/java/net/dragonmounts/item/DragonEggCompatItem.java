package net.dragonmounts.item;

import net.dragonmounts.block.DragonEggCompatBlock;
import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.compat.DragonTypeCompat;
import net.dragonmounts.init.DMBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DragonEggCompatItem extends ItemBlock {
    public static final DragonEggCompatItem INSTANCE = new DragonEggCompatItem();
    public static ItemStack upgradeStack(ItemStack stack) {
        return new ItemStack(DragonTypeCompat.byId(stack.getMetadata()).getInstance(
                HatchableDragonEggBlock.class,
                DMBlocks.ENDER_DRAGON_EGG
        ), stack.getCount());
    }

    private DragonEggCompatItem() {
        super(DragonEggCompatBlock.INSTANCE);
        this.setMaxDamage(0).setHasSubtypes(true);
        this.setRegistryName(DragonEggCompatBlock.IDENTIFIER);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public void onUpdate(ItemStack stack, World level, Entity entity, int slot, boolean selected) {
        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).inventory.setInventorySlotContents(slot, upgradeStack(stack));
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem item) {
        item.setItem(upgradeStack(item.getItem()));
        return false;
    }
}
