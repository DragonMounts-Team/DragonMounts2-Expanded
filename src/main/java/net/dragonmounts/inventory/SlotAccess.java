package net.dragonmounts.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;

public final class SlotAccess {
    private final DataParameter<ItemStack> key;
    public final Entity entity;

    public SlotAccess(Entity entity, DataParameter<ItemStack> key) {
        this.entity = entity;
        this.key = key;
    }

    public ItemStack getItem() {
        return this.entity.getDataManager().get(this.key);
    }

    public void setItem(ItemStack stack) {
        this.entity.getDataManager().set(this.key, stack);
    }

    public ItemStack takeItem(int count) {
        EntityDataManager manager = this.entity.getDataManager();
        ItemStack stack = manager.get(this.key);
        ItemStack taken = stack.splitStack(count);
        if (!taken.isEmpty()) {
            this.entity.notifyDataManagerChange(this.key);
            manager.setDirty(this.key);
        }
        return taken;
    }

    public ItemStack clearItem() {
        EntityDataManager manager = this.entity.getDataManager();
        ItemStack stack = manager.get(this.key);
        manager.set(this.key, ItemStack.EMPTY);
        return stack;
    }
}
