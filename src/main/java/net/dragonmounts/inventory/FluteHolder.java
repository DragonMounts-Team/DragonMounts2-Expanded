package net.dragonmounts.inventory;

import net.dragonmounts.capability.IFluteHolder;
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
        return this.flute.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.flute;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.flute.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = this.flute.splitStack(count);
        if (!result.isEmpty()) {
            this.markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.flute;
        this.flute = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.flute = stack;
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
        if (this.flute.isEmpty()) return;
        if (!player.inventory.addItemStackToInventory(this.flute)) {
            player.dropItem(this.flute, false);
        }
        this.flute = ItemStack.EMPTY;
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
        this.flute = ItemStack.EMPTY;
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

    public static class Storage implements Capability.IStorage<IFluteHolder> {
        @Override
        public NBTTagCompound writeNBT(Capability<IFluteHolder> capability, IFluteHolder instance, EnumFacing side) {
            ItemStack flute = instance.getFlute();
            return flute.isEmpty() ? new NBTTagCompound() : flute.writeToNBT(new NBTTagCompound());
        }

        @Override
        public void readNBT(Capability<IFluteHolder> capability, IFluteHolder instance, EnumFacing side, @Nullable NBTBase tag) {
            instance.setFlute(tag instanceof NBTTagCompound ? new ItemStack((NBTTagCompound) tag) : ItemStack.EMPTY);
        }
    }
}
