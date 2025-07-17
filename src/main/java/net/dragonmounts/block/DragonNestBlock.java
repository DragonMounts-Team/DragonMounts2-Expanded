package net.dragonmounts.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class DragonNestBlock extends Block {
	public DragonNestBlock() {
		super(Material.WOOD);
		this.setResistance(1);
		this.setHardness(1);
		this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 30;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 77;
    }
}
