package net.dragonmounts.item;

import net.dragonmounts.def.EnchantType;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.registry.DragonType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DragonScaleShieldItem extends ItemShield {
    public final DragonType type;

    public DragonScaleShieldItem(DragonType type, ItemArmor.ArmorMaterial material) {
        this.setMaxDamage(material.getDurability(EntityEquipmentSlot.CHEST) * 10 / 3);
        this.setMaxStackSize(1);
        this.type = type;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(this.type.getName());
    }

    //Necessary because were extending from ItemShield, which creates its own displayname method
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal("item.dragon_scale_shield.name");
    }

    @Override
    public boolean isShield(ItemStack stack, EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack ingredient) {
        return ingredient.getItem() == this.type.getInstance(DragonScalesItem.class, null);
    }

    @Override
    public int getItemEnchantability() {
        return this.type instanceof EnchantType ? 1 : 0;
    }

    @Override
    public @Nonnull CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{DMItemGroups.COMBAT};
    }

    @Override
    protected boolean isInCreativeTab(CreativeTabs targetTab) {
        for (CreativeTabs tab : this.getCreativeTabs())
            if (tab == targetTab) return true;
        return targetTab == CreativeTabs.SEARCH;
    }
}
