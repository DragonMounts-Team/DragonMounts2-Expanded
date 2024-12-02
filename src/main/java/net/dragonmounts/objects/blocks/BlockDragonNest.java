package net.dragonmounts.objects.blocks;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.inits.ModBlocks;
import net.dragonmounts.inits.ModItems;
import net.dragonmounts.objects.items.CraftableBlockItem;
import net.dragonmounts.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockDragonNest extends Block implements IHasModel {

	public BlockDragonNest(String name) {
		super(Material.WOOD);
		this.setTranslationKey(name);
		this.setRegistryName(name);
		this.setResistance(1);
		this.setHardness(1);
		this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);

		ModBlocks.BLOCKS.add(this);
		ModItems.ITEMS.add(new CraftableBlockItem(this, DragonMounts.mainTab).setRegistryName(this.getRegistryName()));
	}

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 30;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 77;
    }

	@Override
	public void RegisterModels() {
		DragonMounts.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}
}
