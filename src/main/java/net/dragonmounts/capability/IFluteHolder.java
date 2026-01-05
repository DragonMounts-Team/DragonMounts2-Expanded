package net.dragonmounts.capability;

import net.dragonmounts.api.IValidatedNBTSerializable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IFluteHolder extends IInventory, IValidatedNBTSerializable<NBTTagCompound> {
    ItemStack getFlute();

    void setFlute(ItemStack flute);
}
