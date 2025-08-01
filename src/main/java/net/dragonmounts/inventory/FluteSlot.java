package net.dragonmounts.inventory;

import net.dragonmounts.capability.IFluteHolder;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.item.FluteItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class FluteSlot extends Slot {
    public final IFluteHolder holder;
    public final DragonContainer<?> container;
    public ISlotListener<? super FluteSlot> listener;
    public String desiredName;

    public FluteSlot(@Nullable IFluteHolder holder, DragonContainer<?> container, int x, int y) {
        super(holder == null ? new InventoryBasic(
                "Invalid", true, 1
        ) : holder, 0, x, y);
        this.holder = holder;
        this.container = container;
        this.setBackgroundName("dragonmounts:items/slot/empty_flute");
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.holder != null && !stack.isEmpty() && stack.getItem() == DMItems.FLUTE;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return this.holder == null ? 0 : 1;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return this.holder != null;
    }

    @Override
    public boolean isEnabled() {
        return this.holder != null;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (this.listener != null) {
            this.listener.beforePlaceItem(this, stack);
        }
        super.putStack(stack);
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        if (this.listener != null) {
            this.listener.afterTakeItem(this, stack);
        }
        return super.onTake(player, stack);
    }

    @Override
    public void onSlotChanged() {
        if (this.holder == null) return;
        if (this.getHasStack()) {
            ItemStack stack = this.getStack().copy();
            if (stack.getItem() != DMItems.FLUTE) return;
            FluteItem.bindToFlute(stack, this.container.dragon, this.container.player);
            if (StringUtils.isBlank(this.desiredName)) {
                if (stack.hasDisplayName()) {
                    stack.clearCustomName();
                }
            } else if (!this.desiredName.equals(stack.getDisplayName())) {
                stack.setStackDisplayName(this.desiredName);
            }
            this.holder.setFlute(stack);
        }
        super.onSlotChanged();
    }

    public void applyName(String name) {
        this.desiredName = name;
        if (this.getHasStack()) {
            ItemStack stack = this.getStack();
            if (StringUtils.isBlank(name)) {
                stack.clearCustomName();
            } else {
                stack.setStackDisplayName(this.desiredName);
            }
        }
        this.onSlotChanged();
    }
}
