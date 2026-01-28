package net.dragonmounts.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

import static net.dragonmounts.util.ItemUtil.isInCreativeInventory;

public class CraftableBlockItem extends ItemBlock {
    public final CreativeTabs tab;

    public CraftableBlockItem(Block block, CreativeTabs modTab) {
        super(block);
        this.tab = modTab;
    }

    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{this.tab};
    }

    @Override
    protected boolean isInCreativeTab(CreativeTabs tab) {
        return isInCreativeInventory(this, tab);
    }
}
