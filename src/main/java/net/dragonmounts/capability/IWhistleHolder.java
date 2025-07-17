package net.dragonmounts.capability;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IWhistleHolder extends IInventory {
    ItemStack getWhistle();

    void setWhistle(ItemStack whistle);
}
