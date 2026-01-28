package net.dragonmounts.inventory;

import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.util.DMUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Container of the Dragon Core
 *
 * @author WolfShotz
 */
public class DragonCoreContainer extends Container {
    public final DragonCoreBlockEntity core;
    private final int numRows;

    public DragonCoreContainer(InventoryPlayer playerInv, DragonCoreBlockEntity core, EntityPlayer player) {
        this.core = core;
        this.numRows = core.getSizeInventory();
        core.openInventory(player);

        //Build Dragon Core Inventory Slots
        this.addSlotToContainer(DMUtils.applyBackground(
                new Slot(core, 0, 80, 36),
                "dragonmounts:items/slot/empty_essence"
        ));

        //Build Player Inventory Slots
        for (int i1=0; i1 < 3; ++i1) {
            for (int k1=0; k1 < 9; ++k1) {
                this.addSlotToContainer(new Slot(playerInv, k1 + i1 * 9 + 9, 8 + k1 * 18, 84 + i1 * 18));
            }
        }

        //Build Player Hotbar Slots
        for (int j1=0; j1 < 9; ++j1) {
            this.addSlotToContainer(new Slot(playerInv, j1, 8 + j1 * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.core.isUsableByPlayer(playerIn);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.core.closeInventory(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack=ItemStack.EMPTY;
        Slot slot=this.inventorySlots.get(index);

        if (slot!=null && slot.getHasStack()) {
            ItemStack itemstack1=slot.getStack();
            itemstack=itemstack1.copy();

            if (index < this.numRows * 9) {
                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
