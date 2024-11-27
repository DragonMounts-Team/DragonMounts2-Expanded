package net.dragonmounts.objects.items;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.inits.DMArmors;
import net.dragonmounts.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;


public class ItemDragonArmor extends Item implements IHasModel {

    public String name;

    public ItemDragonArmor(String name) {
        this.name = name;
        this.setTranslationKey(name);
        this.maxStackSize = 1;
        this.setRegistryName(DragonMountsTags.MOD_ID, name);
        this.setCreativeTab(CreativeTabs.COMBAT);

        DMArmors.ARMOR.add(this);
    }

    /**
     * This method determines where the item is displayed
     */
    @Override
    public @Nonnull CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{DragonMounts.armoryTab};
    }

    @Override
    protected boolean isInCreativeTab(CreativeTabs targetTab) {
        for (CreativeTabs tab : this.getCreativeTabs())
            if (tab == targetTab) return true;
        return targetTab == CreativeTabs.SEARCH;
    }

    @Override
    public void RegisterModels() {
        DragonMounts.proxy.registerItemRenderer(this, 0, "inventory");
    }
}