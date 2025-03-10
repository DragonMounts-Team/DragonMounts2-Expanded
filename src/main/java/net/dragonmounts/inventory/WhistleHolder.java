package net.dragonmounts.inventory;

import net.dragonmounts.capability.IWhistleHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.dragonmounts.capability.DMCapabilities.WHISTLE_HOLDER;

public class WhistleHolder implements IWhistleHolder {
    public static void onPlayerClone(EntityPlayer neoPlayer, EntityPlayer oldPlayer) {
        IWhistleHolder neo = neoPlayer.getCapability(WHISTLE_HOLDER, null);
        if (neo == null) return;
        IWhistleHolder old = oldPlayer.getCapability(WHISTLE_HOLDER, null);
        if (old == null) return;
        neo.setWhistle(old.getWhistle());
    }

    private @Nonnull ItemStack whistle = ItemStack.EMPTY;

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getWhistle() {
        return this.whistle;
    }

    @Override
    public void setWhistle(ItemStack whistle) {
        this.whistle = whistle;
    }

    @Override
    public boolean isEmpty() {
        return this.whistle.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.whistle;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.whistle.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = this.whistle.splitStack(count);
        if (!result.isEmpty()) {
            this.markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.whistle;
        this.whistle = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.whistle = stack;
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
        if (this.whistle.isEmpty()) return;
        if (!player.inventory.addItemStackToInventory(this.whistle)) {
            player.dropItem(this.whistle, false);
        }
        this.whistle = ItemStack.EMPTY;
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
        this.whistle = ItemStack.EMPTY;
    }

    @Override
    public String getName() {
        return "container.dragonmounts.whistle_holder";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(this.getName());
    }

    public static class Storage implements Capability.IStorage<IWhistleHolder> {
        @Override
        public NBTTagCompound writeNBT(Capability<IWhistleHolder> capability, IWhistleHolder instance, EnumFacing side) {
            ItemStack whistle = instance.getWhistle();
            return whistle.isEmpty() ? new NBTTagCompound() : whistle.writeToNBT(new NBTTagCompound());
        }

        @Override
        public void readNBT(Capability<IWhistleHolder> capability, IWhistleHolder instance, EnumFacing side, @Nullable NBTBase tag) {
            instance.setWhistle(tag instanceof NBTTagCompound ? new ItemStack((NBTTagCompound) tag) : ItemStack.EMPTY);
        }
    }
}
