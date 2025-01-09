package net.dragonmounts.block;

import net.dragonmounts.block.entity.DragonHeadBlockEntity;
import net.dragonmounts.item.DragonHeadItem;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class DragonHeadBlock extends BlockContainer {
    protected static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D);
    public final DragonVariant variant;
    public final boolean isOnWall;

    protected DragonHeadBlock(Material material, DragonVariant variant, boolean isOnWall) {
        super(material);
        this.setSoundType(SoundType.STONE);
        this.variant = variant;
        this.isOnWall = isOnWall;
    }

    public abstract EnumFacing getFacing(int meta);

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public DragonHeadBlockEntity createNewTileEntity(World level, int meta) {
        return new DragonHeadBlockEntity();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this.variant.head.item);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return this.variant.head.item;
    }

    public static class Holder {
        public final DragonHeadStandingBlock standing;
        public final DragonHeadWallBlock wall;
        public final DragonHeadItem item;

        public Holder(DragonHeadStandingBlock standing, DragonHeadWallBlock wall, DragonHeadItem item) {
            this.standing = standing;
            this.wall = wall;
            this.item = item;
        }
    }
}
