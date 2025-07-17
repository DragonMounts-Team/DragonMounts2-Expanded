package net.dragonmounts.item;

import net.dragonmounts.compat.DragonMountsCompat;
import net.minecraft.item.ItemBlock;

public class DragonEggCompatItem extends ItemBlock {
    public DragonEggCompatItem() {
        super(DragonMountsCompat.DRAGON_EGG_BLOCK);
        this.setMaxDamage(0).setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }
}
