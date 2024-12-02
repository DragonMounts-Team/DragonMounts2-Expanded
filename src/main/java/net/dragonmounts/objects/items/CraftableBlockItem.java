package net.dragonmounts.objects.items;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nonnull;

public class CraftableBlockItem extends ItemBlock {
    public final CreativeTabs tab;

    public CraftableBlockItem(Block block, CreativeTabs modTab) {
        super(block);
        this.tab = modTab;
    }

    /**
     * This method determines where the item is displayed
     */
    @Override
    public @Nonnull CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{this.tab};
    }

    @Override
    protected boolean isInCreativeTab(CreativeTabs targetTab) {
        for (CreativeTabs tab : this.getCreativeTabs())
            if (tab == targetTab) return true;
        return targetTab == CreativeTabs.SEARCH;
    }
}
