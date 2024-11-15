package top.dragonmounts.inits;

import top.dragonmounts.objects.blocks.BlockDragonNest;
import top.dragonmounts.objects.blocks.BlockDragonShulker;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks
{
	public static final List<Block> BLOCKS = new ArrayList<Block>();

	public static final Block NESTBLOCK = new BlockDragonNest("pileofsticks");
	public static final BlockDragonShulker DRAGONSHULKER = new BlockDragonShulker("block_dragon_shulker");
}