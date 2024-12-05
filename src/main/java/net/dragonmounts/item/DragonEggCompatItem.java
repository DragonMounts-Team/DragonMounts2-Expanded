package net.dragonmounts.item;

import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.util.IHasModel;
import net.minecraft.item.ItemBlock;

public class DragonEggCompatItem extends ItemBlock implements IHasModel {
    public DragonEggCompatItem() {
        super(DMBlocks.DRAGON_EGG_COMPAT);
        this.setMaxDamage(0).setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public void registerModel() {
        ClientUtil.registerDragonEggModel(this);
    }
}
