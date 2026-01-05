package net.dragonmounts.inventory;

import net.dragonmounts.capability.IFluteHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.dragonmounts.capability.DMCapabilities.FLUTE_HOLDER;

public class FluteHolder implements IFluteHolder {
    public static void onPlayerClone(EntityPlayer neoPlayer, EntityPlayer oldPlayer) {
        IFluteHolder neo = neoPlayer.getCapability(FLUTE_HOLDER, null);
        if (neo == null) return;
        IFluteHolder old = oldPlayer.getCapability(FLUTE_HOLDER, null);
        if (old == null) return;
        neo.setFlute(old.getFlute());
    }

    private @Nonnull ItemStack flute = ItemStack.EMPTY;

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getFlute() {
        return this.flute;
    }

    @Override
    public void setFlute(ItemStack flute) {
        this.flute = flute;
    }

    @Override
    public boolean isEmpty() {
        return this.getFlute().isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.getFlute();
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack flute = this.getFlute();
        if (flute.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = flute.splitStack(count);
        if (!result.isEmpty()) {
            this.markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.getFlute();
        this.setFlute(ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.setFlute(stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {
        ItemStack flute = this.getFlute();
        if (flute.isEmpty()) return;
        if (!player.inventory.addItemStackToInventory(flute)) {
            player.dropItem(flute, false);
        }
        this.setFlute(ItemStack.EMPTY);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.setFlute(ItemStack.EMPTY);
    }

    @Override
    public String getName() {
        return "container.dragonmounts.flute_holder";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(this.getName());
    }

    @Override
    public @Nullable NBTTagCompound validateTag(@Nullable NBTBase tag) {
        return tag instanceof NBTTagCompound ? ((NBTTagCompound) tag) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        ItemStack flute = this.getFlute();
        return flute.isEmpty() ? new NBTTagCompound() : flute.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        this.setFlute(tag.isEmpty() ? ItemStack.EMPTY : new ItemStack(tag));
    }

    @Override
    public void deserializeNothing() {
        this.setFlute(ItemStack.EMPTY);
    }
}
