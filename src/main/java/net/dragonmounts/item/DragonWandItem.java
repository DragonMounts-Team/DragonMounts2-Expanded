package net.dragonmounts.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class DragonWandItem extends Item {
    public DragonWandItem() {
        this.setMaxStackSize(1);
    }

    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[0];
    }
}
