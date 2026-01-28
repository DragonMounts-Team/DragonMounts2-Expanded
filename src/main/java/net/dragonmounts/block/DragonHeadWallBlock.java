package net.dragonmounts.block;

import net.dragonmounts.registry.DragonVariant;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.EnumMap;

import static net.minecraft.block.BlockHorizontal.FACING;

public class DragonHeadWallBlock extends DragonHeadBlock {
    private static final EnumMap<EnumFacing, AxisAlignedBB> AABBS = new EnumMap<>(EnumFacing.class);

    static {
        AABBS.put(EnumFacing.NORTH, new AxisAlignedBB(0.25D, 0.25D, 0.5D, 0.75D, 0.75D, 1.0D));
        AABBS.put(EnumFacing.SOUTH, new AxisAlignedBB(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 0.5D));
        AABBS.put(EnumFacing.EAST, new AxisAlignedBB(0.5D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D));
        AABBS.put(EnumFacing.WEST, new AxisAlignedBB(0.0D, 0.25D, 0.25D, 0.5D, 0.75D, 0.75D));
    }

    public DragonHeadWallBlock(Material material, DragonVariant variant) {
        super(material, variant, true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public EnumFacing getFacing(int meta) {
        return EnumFacing.byHorizontalIndex(meta);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess level, BlockPos pos) {
        return AABBS.getOrDefault(state.getValue(FACING), SHAPE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }
}
