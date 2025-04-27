package net.dragonmounts.inventory;

import net.dragonmounts.capability.IWhistleHolder;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.item.DragonWhistleItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class WhistleSlot extends Slot {
    public final IWhistleHolder holder;
    public final DragonContainer<?> container;
    public ISlotListener<? super WhistleSlot> listener;
    public String desiredName;

    public WhistleSlot(@Nullable IWhistleHolder holder, DragonContainer<?> container, int x, int y) {
        super(holder == null ? new InventoryBasic(
                "Invalid", true, 1
        ) : holder, 0, x, y);
        this.holder = holder;
        this.container = container;
        this.setBackgroundName("dragonmounts:items/slot/empty_whistle");
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.holder != null && !stack.isEmpty() && stack.getItem() == DMItems.DRAGON_WHISTLE;
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
            if (stack.getItem() != DMItems.DRAGON_WHISTLE) return;
            DragonWhistleItem.bindWhistle(stack, this.container.dragon, this.container.player);
            if (StringUtils.isBlank(this.desiredName)) {
                if (stack.hasDisplayName()) {
                    stack.clearCustomName();
                }
            } else if (!this.desiredName.equals(stack.getDisplayName())) {
                stack.setStackDisplayName(this.desiredName);
            }
            this.holder.setWhistle(stack);
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
