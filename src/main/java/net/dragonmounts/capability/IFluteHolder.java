package net.dragonmounts.capability;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IFluteHolder extends IInventory {
    ItemStack getFlute();

    void setFlute(ItemStack flute);
}
