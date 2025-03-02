package net.dragonmounts.block;

import net.dragonmounts.init.DMItemGroups;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class DragonScalesBlock extends Block {
	public DragonScalesBlock(Material dragonMountMaterial) {
		super(dragonMountMaterial);
		this.setResistance(20);
		this.setHardness(4);
		this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(DMItemGroups.MAIN);
        this.setHarvestLevel("pickaxe", 3);
		this.setTranslationKey("tile.dragonmounts.dragon_scales_block.name");
	}
}
