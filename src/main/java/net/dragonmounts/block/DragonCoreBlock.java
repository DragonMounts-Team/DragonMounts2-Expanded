package net.dragonmounts.block;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.client.gui.GuiHandler;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static net.minecraft.block.BlockDirectional.FACING;

/**
 * Physical Block of the Dragon Core
 *
 * @author WolfShotz
 */

public class DragonCoreBlock extends BlockContainer {
    public DragonCoreBlock() {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        this.setHardness(2000);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new DragonCoreBlockEntity();
    }

    @Override
    public void breakBlock(World level, BlockPos pos, IBlockState state) {
        TileEntity tileentity = level.getTileEntity(pos);
        if (tileentity instanceof DragonCoreBlockEntity) {
            InventoryHelper.dropInventoryItems(level, pos, (DragonCoreBlockEntity) tileentity);
        }
        super.breakBlock(level, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof DragonCoreBlockEntity) {
                ((DragonCoreBlockEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
    public boolean onBlockActivated(World level, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (level.isRemote || player.isSpectator()) return true;
        if (level.getTileEntity(pos) instanceof DragonCoreBlockEntity) {
            player.openGui(DragonMounts.getInstance(), GuiHandler.GUI_DRAGON_CORE, level, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, facing);
    }
    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    /**
     * Creates the particles that play around the block
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        for (int i = 0; i < 3; ++i) {
            int j = rand.nextInt(2) * 2 - 1;
            int k = rand.nextInt(2) * 2 - 1;
            //Coords
            double x = pos.getX() + 0.5D + 0.25D * j;
            double y = (pos.getY() + rand.nextFloat());
            double z = pos.getZ() + 0.5D + 0.25D * k;
            //Speed
            double s1 = rand.nextFloat() * j;
            double s2 = (rand.nextFloat() - 0.5D) * 0.125D;
            double s3 = rand.nextFloat() * k;
            worldIn.spawnParticle(EnumParticleTypes.PORTAL, x, y, z, s1, s2, s3);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("deprecation")
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}