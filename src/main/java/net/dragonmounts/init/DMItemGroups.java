package net.dragonmounts.init;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@MethodsReturnNonnullByDefault
public class DMItemGroups {
    public static final CreativeTabs BLOCKS;
    public static final CreativeTabs ITEMS;
    public static final CreativeTabs COMBAT;

    static {
        int length = CreativeTabs.getNextID();
        COMBAT = new CreativeTabs(length + 2, "dragonmounts.combat") {
            @Override
            @SideOnly(Side.CLIENT)
            public ItemStack createIcon() {
                return new ItemStack(DMItems.ENDER_DRAGON_SCALE_SWORD);
            }
        };
        ITEMS = new CreativeTabs(length + 1, "dragonmounts.items") {
            @Override
            @SideOnly(Side.CLIENT)
            public ItemStack createIcon() {
                return new ItemStack(DMItems.ENDER_DRAGON_SCALES);
            }
        };
        BLOCKS = new CreativeTabs(length, "dragonmounts.blocks") {
            @Override
            @SideOnly(Side.CLIENT)
            public ItemStack createIcon() {
                return new ItemStack(DMBlocks.ENDER_DRAGON_EGG);
            }
        };
    }

    public static void init() {}
}
