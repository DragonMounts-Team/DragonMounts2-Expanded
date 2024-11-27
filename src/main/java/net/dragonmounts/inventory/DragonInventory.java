package net.dragonmounts.inventory;

import net.dragonmounts.network.SSyncBannerPacket;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.util.DMUtils;
import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

import static net.dragonmounts.DragonMounts.NETWORK_WRAPPER;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DragonInventory implements IInventory {
    public final EntityTameableDragon dragon;
    /**
     * item stacks in chest
     */
    private final ItemStack[] stacks = new ItemStack[27];
    /**
     * banner stacks
     */
    private final ItemStack[] banners = new ItemStack[4];

    public DragonInventory(EntityTameableDragon dragon) {
        this.dragon = dragon;
        this.clear();
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
                return this.dragon.getChest();
            case 5:
                return this.dragon.getArmor();
            case 6:
                return this.dragon.getSaddle();
            default:
                return slot < 4 ? this.banners[slot] : ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = this.getStackInSlot(slot);
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
        EntityTameableDragon dragon = this.dragon;
        switch (slot -= this.stacks.length) {
            case 4:
                stack = dragon.getChest();
                dragon.setChest(ItemStack.EMPTY);
                return stack;
            case 5:
                stack = dragon.getArmor();
                dragon.setArmor(ItemStack.EMPTY);
                return stack;
            case 6:
                stack = dragon.getSaddle();
                dragon.setSaddle(ItemStack.EMPTY);
                return stack;
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
                this.dragon.setChest(stack);
                break;
            case 5:
                this.dragon.setArmor(stack);
                break;
            case 6:
                this.dragon.setSaddle(stack);
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
    public void markDirty() {
        //this.dragon.refreshInventory();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
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
            NETWORK_WRAPPER.sendToAllTracking(new SSyncBannerPacket(
                    this.dragon.getEntityId(),
                    1 << slot,
                    this.banners
            ), this.dragon);
        }
    }

    public ItemStack getBanner(int slot) {
        return (slot < 0 || slot > 3) ? ItemStack.EMPTY : this.banners[slot];
    }

    public void saveAdditionalData(NBTTagCompound tag) {
        EntityTameableDragon dragon = this.dragon;
        ItemStack stack;
        if (!(stack = dragon.getSaddle()).isEmpty()) {
            tag.setTag("Saddle", stack.writeToNBT(new NBTTagCompound()));
        }
        if (!(stack = dragon.getChest()).isEmpty()) {
            tag.setTag("Chest", stack.writeToNBT(new NBTTagCompound()));
        }
        if (!(stack = dragon.getArmor()).isEmpty()) {
            tag.setTag("Armor", stack.writeToNBT(new NBTTagCompound()));
        }
        NBTTagList banners = DMUtils.writeToNBT(this.banners);
        if (!banners.isEmpty()) {
            tag.setTag("Banners", banners);
        }
        NBTTagList stacks = DMUtils.writeToNBT(this.stacks);
        if (!stacks.isEmpty()) {
            tag.setTag("Inventory", stacks);
        }
    }

    public void readAdditionalData(NBTTagCompound tag) {
        if (tag.hasKey("Saddle")) {
            this.dragon.setSaddle(new ItemStack(tag.getCompoundTag("Saddle")));
        }
        if (tag.hasKey("Chest")) {
            this.dragon.setChest(new ItemStack(tag.getCompoundTag("Chest")));
        }
        if (tag.hasKey("Armor")) {
            this.dragon.setArmor(new ItemStack(tag.getCompoundTag("Armor")));
        }
        if (tag.hasKey("Banners")) {
            DMUtils.readFromNBT(this.banners, tag.getTagList("Banners", 10));
        }
        if (tag.hasKey("Inventory")) {
            DMUtils.readFromNBT(this.stacks, tag.getTagList("Inventory", 10));
        }
        // Compat
        NBTTagList items = tag.getTagList("Items", 10);
        for (int i = 0, size = items.tagCount(); i < size; ++i) {
            NBTTagCompound stack = items.getCompoundTagAt(i);
            int j = stack.getByte("Slot") & 255;
            switch (j) {
                case 0:
                    this.dragon.setSaddle(new ItemStack(stack));
                    continue;
                case 1:
                    this.dragon.setChest(new ItemStack(stack));
                    continue;
                case 2:
                    this.dragon.setArmor(new ItemStack(stack));
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
        SSyncBannerPacket.writeBanners(buffer, -1, this.banners);
    }

    public void readSpawnData(ByteBuf buffer) {
        SSyncBannerPacket.readBanners(buffer, this.banners);
    }

    public void dropItemsInChest() {
        EntityTameableDragon dragon = this.dragon;
        if (dragon.world.isRemote) {
            Arrays.fill(this.stacks, ItemStack.EMPTY);
        } else {
            DMUtils.dropItems(this.dragon, this.stacks);
        }
    }

    /**
     * Server only
     */
    public void dropAllItems() {
        EntityTameableDragon dragon = this.dragon;
        DMUtils.dropItems(dragon, this.stacks);
        DMUtils.dropItems(dragon, this.banners);
        dragon.entityDropItem(dragon.getChest(), 0.5F);
        dragon.entityDropItem(dragon.getArmor(), 0.5F);
        dragon.entityDropItem(dragon.getSaddle(), 0.5F);
        dragon.setChest(ItemStack.EMPTY);
        dragon.setArmor(ItemStack.EMPTY);
        dragon.setSaddle(ItemStack.EMPTY);
    }
}
