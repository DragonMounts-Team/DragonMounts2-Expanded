package net.dragonmounts.inventory;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public interface ISlotListener<T extends Slot> {
    void beforePlaceItem(T slot, ItemStack stack);

    void afterTakeItem(T slot, ItemStack stack);
}
