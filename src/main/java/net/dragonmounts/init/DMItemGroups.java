package net.dragonmounts.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DMItemGroups {
    public static final CreativeTabs MAIN;
    public static final CreativeTabs COMBAT;

    static {
        int length = CreativeTabs.getNextID();
        COMBAT = new CreativeTabs(length + 1, "dragonmounts.combat") {
            @Override
            @SideOnly(Side.CLIENT)
            public ItemStack createIcon() {
                return new ItemStack(DMItems.ENDER_DRAGON_SCALE_SWORD);
            }
        };
        MAIN = new CreativeTabs(length, "dragonmounts.main") {
            @Override
            @SideOnly(Side.CLIENT)
            public ItemStack createIcon() {
                return new ItemStack(Blocks.DRAGON_EGG);
            }
        };
    }

    public static void init() {}
}
