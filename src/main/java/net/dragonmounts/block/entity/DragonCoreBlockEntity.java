package net.dragonmounts.block.entity;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.block.DragonCoreBlock;
import net.dragonmounts.inventory.DragonCoreContainer;
import net.dragonmounts.util.math.MathX;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Dragon Core TileEntity
 *
 * @author WolfShotz
 */

public class DragonCoreBlockEntity extends TileEntityLockableLoot implements ITickable {
    private final NonNullList<ItemStack> chestContents = NonNullList.withSize(1, ItemStack.EMPTY);
    public int numPlayersUsing, ticksSinceSync;
    private float progress, progressOld;
    private AnimationStatus animationStatus;

    public DragonCoreBlockEntity() {
        this.animationStatus = AnimationStatus.CLOSED;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;

            if (type == 0) {
                this.animationStatus = AnimationStatus.CLOSING;
            } else if (type == 1) {
                this.animationStatus = AnimationStatus.OPENING;
            }

            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.chestContents.get(0).isEmpty();
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.dragonmounts.dragon_core";
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!this.checkLootAndRead(compound)) {
            NBTTagCompound stack = compound.getCompoundTag("Item");
            if (!stack.isEmpty()) {
                this.chestContents.set(0, new ItemStack(stack));
            }
        }
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!this.checkLootAndWrite(compound)) {
            NBTTagCompound item = new NBTTagCompound();
            this.chestContents.get(0).writeToNBT(item);
            compound.setTag("Item", item);
        }
        compound.setString("CustomName", this.customName);
        return compound;
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new DragonCoreContainer(playerInventory, this, playerIn);
    }

    @Override
    public String getGuiID() {
        return DragonMountsTags.MOD_ID + ":dragon_core";
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    public void update() {
        this.updateAnimation();

        if (this.animationStatus == AnimationStatus.OPENING || this.animationStatus == AnimationStatus.CLOSING) {
            this.moveCollidedEntities();
        }
    }

    private AxisAlignedBB getTopBoundingBox(EnumFacing p_190588_1_) {
        EnumFacing enumfacing = p_190588_1_.getOpposite();
        return this.getBoundingBox(p_190588_1_).contract(enumfacing.getXOffset(), enumfacing.getYOffset(), enumfacing.getZOffset());
    }

    public AxisAlignedBB getBoundingBox(EnumFacing p_190587_1_) {
        return Block.FULL_BLOCK_AABB.expand(0.5F * this.getProgress(1.0F) * (float) p_190587_1_.getXOffset(), 0.5F * this.getProgress(1.0F) * (float) p_190587_1_.getYOffset(), 0.5F * this.getProgress(1.0F) * (float) p_190587_1_.getZOffset());
    }

    private void moveCollidedEntities() {
        IBlockState iblockstate = this.world.getBlockState(this.getPos());
        if (iblockstate.getBlock() instanceof DragonCoreBlock) {
            EnumFacing enumfacing = iblockstate.getValue(BlockDirectional.FACING);
            AxisAlignedBB axisalignedbb = this.getTopBoundingBox(enumfacing).offset(this.pos);
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);
            if (list.isEmpty()) return;
            for (Entity entity : list) {
                if (entity.getPushReaction() != EnumPushReaction.IGNORE) {
                    double d0 = 0.0D;
                    double d1 = 0.0D;
                    double d2 = 0.0D;
                    AxisAlignedBB axisalignedbb1 = entity.getEntityBoundingBox();

                    switch (enumfacing.getAxis()) {
                        case X:

                            if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
                                d0 = axisalignedbb.maxX - axisalignedbb1.minX;
                            } else {
                                d0 = axisalignedbb1.maxX - axisalignedbb.minX;
                            }

                            d0 = d0 + 0.01D;
                            break;
                        case Y:

                            if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
                                d1 = axisalignedbb.maxY - axisalignedbb1.minY;
                            } else {
                                d1 = axisalignedbb1.maxY - axisalignedbb.minY;
                            }

                            d1 = d1 + 0.01D;
                            break;
                        case Z:

                            if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
                                d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
                            } else {
                                d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
                            }

                            d2 = d2 + 0.01D;
                    }

                    entity.move(MoverType.SHULKER_BOX, d0 * enumfacing.getXOffset(), d1 * enumfacing.getYOffset(), d2 * enumfacing.getZOffset());
                }
            }
        }
    }

    protected void updateAnimation() {
        this.progressOld = this.progress;

        switch (this.animationStatus) {
            case CLOSED:
                this.progress = 0.0F;
                break;
            case OPENING:
                this.progress += 0.1F;
                if (this.progress >= 1.0F) {
                    this.animationStatus = AnimationStatus.OPENED;
                    this.progress = 1.0F;
                }
                break;
            case CLOSING:
                this.progress -= 0.1F;
                if (this.progress <= 0.0F) {
                    this.animationStatus = AnimationStatus.CLOSED;
                    this.progress = 0.0F;
                    if (!this.world.isRemote && this.isEmpty()) {
                        this.world.destroyBlock(this.getPos(), false);
                    }
                }
                break;
            case OPENED:
                this.progress = 1.0F;
        }
    }

    public AnimationStatus getAnimationStatus() {
        return this.animationStatus;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        ++this.numPlayersUsing;
        World level = this.world;
        BlockPos pos = this.pos;
        level.addBlockEvent(pos, this.getBlockType(), 1, this.numPlayersUsing);
        level.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
        if (numPlayersUsing == 1) {
            double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
            level.playSound(null, x, y, z, SoundEvents.BLOCK_ENDERCHEST_OPEN, SoundCategory.BLOCKS, 0.9F, level.rand.nextFloat() * 0.1F + 0.9F);
            level.playSound(null, x, y, z, SoundEvents.ENTITY_ENDERDRAGON_AMBIENT, SoundCategory.HOSTILE, 0.05F, level.rand.nextFloat() * 0.3F + 0.9F);
            level.playSound(null, x, y, z, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.BLOCKS, 0.08F, level.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        --this.numPlayersUsing;
        World level = this.world;
        BlockPos pos = this.pos;
        level.addBlockEvent(pos, this.getBlockType(), 1, this.numPlayersUsing);
        level.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
        if (numPlayersUsing <= 0) {
            double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
            level.playSound(null, x, y, z, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.3F, level.rand.nextFloat() * 0.1F + 0.3F);
            level.playSound(null, x, y, z, SoundEvents.ENTITY_ENDEREYE_DEATH, SoundCategory.NEUTRAL, 2.0F, level.rand.nextFloat() * 0.1F + 0.3F);
            level.playEvent(2003, pos.up(), 0);
        }
    }

    public float getProgress(float partialTicks) {
        return MathX.lerp(this.progress, this.progressOld, partialTicks);
    }

    public enum AnimationStatus {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;
    }
}
