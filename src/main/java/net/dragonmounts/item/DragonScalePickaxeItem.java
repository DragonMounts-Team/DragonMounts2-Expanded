package net.dragonmounts.item;

import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.registry.DragonType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;
import static net.dragonmounts.util.ItemUtil.isInCreativeInventory;

public class DragonScalePickaxeItem extends ItemPickaxe {
    public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "dragon_scale_pickaxe";
    public final DragonType type;

    public DragonScalePickaxeItem(Item.ToolMaterial material, DragonType type) {
        super(material);
        this.type = type;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(this.type.getDisplayName());
    }

    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{DMItemGroups.COMBAT};
    }

    @Override
    protected boolean isInCreativeTab(CreativeTabs tab) {
        return isInCreativeInventory(this, tab);
    }
}
