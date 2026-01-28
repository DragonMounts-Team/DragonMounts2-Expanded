package net.dragonmounts.inventory;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.network.SUpdateBannerPacket;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import static net.dragonmounts.DragonMounts.NETWORK_WRAPPER;
import static net.dragonmounts.util.ByteBufferUtil.readStackSilently;

public final class DragonInventory implements IInventory {
    public static boolean isValidChest(ItemStack stack) {
        return !stack.isEmpty() && ArrayUtils.contains(
                OreDictionary.getOreIDs(stack),
                OreDictionary.getOreID("chestWood")
        );
    }

    public final TameableDragonEntity dragon;
    private final ItemStack[] banners = new ItemStack[4];
    /// item stacks in chest
    private final ItemStack[] stacks = new ItemStack[27];

    public DragonInventory(TameableDragonEntity dragon) {
        this.dragon = dragon;
        this.clear(); // fill arrays
    }

    @Override
    public int getSizeInventory() {
        return this.stacks.length + this.banners.length + 3;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0) return ItemStack.EMPTY;
        if (slot < this.stacks.length) return this.stacks[slot];
        switch (slot -= this.stacks.length) {
            case 4:
                return this.dragon.chest.getItem();
            case 5:
                return this.dragon.armor.getItem();
            case 6:
                return this.dragon.saddle.getItem();
            default:
                return slot < 4 ? this.banners[slot] : ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (slot < 0) return ItemStack.EMPTY;
        ItemStack stack;
        if (slot >= this.stacks.length) {
            int take;
            ItemStack result;
            switch (slot -= this.stacks.length) {
                case 4:
                    return this.dragon.chest.takeItem(count);
                case 5:
                    return this.dragon.armor.takeItem(count);
                case 6:
                    return this.dragon.saddle.takeItem(count);
                default:
                    stack = slot < 4 ? this.banners[slot] : ItemStack.EMPTY;
            }
        } else {
            stack = this.stacks[slot];
        }
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = stack.splitStack(count);
        if (!result.isEmpty()) {
            this.markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        if (slot < 0) return ItemStack.EMPTY;
        ItemStack stack;
        if (slot < this.stacks.length) {
            stack = this.stacks[slot];
            this.stacks[slot] = ItemStack.EMPTY;
            return stack;
        }
        switch (slot -= this.stacks.length) {
            case 4:
                return this.dragon.chest.clearItem();
            case 5:
                return this.dragon.armor.clearItem();
            case 6:
                return this.dragon.saddle.clearItem();
            default:
                if (slot < 4) {
                    stack = this.banners[slot];
                    this.banners[slot] = ItemStack.EMPTY;
                    return stack;
                }
                return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < 0) return;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        if (slot < this.stacks.length) {
            this.stacks[slot] = stack;
            this.markDirty();
            return;
        }
        switch (slot -= this.stacks.length) {
            case 4:
                this.dragon.chest.setItem(stack);
                break;
            case 5:
                this.dragon.armor.setItem(stack);
                break;
            case 6:
                this.dragon.saddle.setItem(stack);
                break;
            default:
                if (slot < 4) {
                    this.banners[slot] = stack;
                }
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return Relation.checkRelation(this.dragon, player).isTrusted;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
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
        Arrays.fill(this.stacks, ItemStack.EMPTY);
        Arrays.fill(this.banners, ItemStack.EMPTY);
    }

    @Override
    public String getName() {
        return this.dragon.getName();
    }

    @Override
    public boolean hasCustomName() {
        return this.dragon.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.dragon.getDisplayName();
    }

    public void setBanner(int slot, ItemStack stack) {
        if (slot < 0 || slot > 3) return;
        this.banners[slot] = stack;
        if (!this.dragon.world.isRemote) {
            NETWORK_WRAPPER.sendToAllTracking(new SUpdateBannerPacket(
                    this.dragon.getEntityId(),
                    slot,
                    stack
            ), this.dragon);
        }
    }

    public ItemStack getBanner(int slot) {
        return (slot < 0 || slot > 3) ? ItemStack.EMPTY : this.banners[slot];
    }

    public void saveAdditionalData(NBTTagCompound tag) {
        TameableDragonEntity dragon = this.dragon;
        ItemStack stack;
        if (!(stack = dragon.saddle.getItem()).isEmpty()) {
            tag.setTag("Saddle", stack.writeToNBT(new NBTTagCompound()));
        }
        if (!(stack = dragon.chest.getItem()).isEmpty()) {
            tag.setTag("Chest", stack.writeToNBT(new NBTTagCompound()));
        }
        if (!(stack = dragon.armor.getItem()).isEmpty()) {
            tag.setTag("Armor", stack.writeToNBT(new NBTTagCompound()));
        }
        NBTTagList list = ItemUtil.writeToNBT(this.banners);
        if (!list.isEmpty()) {
            tag.setTag("Banners", list);
        }
        list = ItemUtil.writeToNBT(this.stacks);
        if (!list.isEmpty()) {
            tag.setTag("Inventory", list);
        }
    }

    public void readAdditionalData(NBTTagCompound tag) {
        TameableDragonEntity dragon = this.dragon;
        if (tag.hasKey("Saddle")) {
            dragon.saddle.setItem(new ItemStack(tag.getCompoundTag("Saddle")));
        }
        if (tag.hasKey("Chest")) {
            dragon.chest.setItem(new ItemStack(tag.getCompoundTag("Chest")));
        }
        if (tag.hasKey("Armor")) {
            dragon.armor.setItem(new ItemStack(tag.getCompoundTag("Armor")));
        }
        if (tag.hasKey("Banners")) {
            ItemUtil.readFromNBT(this.banners, tag.getTagList("Banners", 10));
        }
        if (tag.hasKey("Inventory")) {
            ItemUtil.readFromNBT(this.stacks, tag.getTagList("Inventory", 10));
        }
        // Compat
        NBTTagList items = tag.getTagList("Items", 10);
        for (int i = 0, size = items.tagCount(); i < size; ++i) {
            NBTTagCompound stack = items.getCompoundTagAt(i);
            int j = stack.getByte("Slot") & 255;
            switch (j) {
                case 0:
                    dragon.saddle.setItem(new ItemStack(stack));
                    continue;
                case 1:
                    dragon.chest.setItem(new ItemStack(stack));
                    continue;
                case 2:
                    dragon.armor.setItem(new ItemStack(stack));
                    continue;
                default:
                    if (j < 30) {
                        this.stacks[j - 3] = new ItemStack(stack);
                    } else if (j > 30 && j < 35) {
                        this.banners[j - 31] = new ItemStack(stack);
                    }
            }
        }
    }

    public void writeSpawnData(ByteBuf buffer) {
        PacketBuffer wrapped = new PacketBuffer(buffer);
        for (ItemStack stack : this.banners) {
            wrapped.writeItemStack(stack);
        }
    }

    public void readSpawnData(ByteBuf buffer) {
        PacketBuffer wrapped = new PacketBuffer(buffer);
        ItemStack[] banners = this.banners;
        for (int i = 0; i < banners.length; ++i) {
            banners[i] = readStackSilently(wrapped);
        }
    }

    public void dropItemsInChest() {
        if (this.dragon.world.isRemote) {
            Arrays.fill(this.stacks, ItemStack.EMPTY);
        } else {
            EntityUtil.dropItems(this.dragon, this.stacks);
        }
    }

    /// Should only be called on SERVER side
    public void dropAllItems() {
        TameableDragonEntity dragon = this.dragon;
        EntityUtil.dropItems(dragon, this.stacks);
        EntityUtil.dropItems(dragon, this.banners);
        dragon.entityDropItem(dragon.chest.clearItem(), 0.5F);
        dragon.entityDropItem(dragon.armor.clearItem(), 0.5F);
        dragon.entityDropItem(dragon.saddle.clearItem(), 0.5F);
    }
}
