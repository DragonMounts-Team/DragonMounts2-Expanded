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

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

public class DragonScaleShieldItem extends ItemShield {
    public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "dragon_scale_shield";
    public final DragonType type;
    private String translationKeyCompat;
    private String translationKey;

    public DragonScaleShieldItem(DragonType type, ItemArmor.ArmorMaterial material) {
        this.setMaxDamage(material.getDurability(EntityEquipmentSlot.CHEST) * 10 / 3).setMaxStackSize(1);
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
        return I18n.translateToLocal(this.translationKey);
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

    @Override
    public DragonScaleShieldItem setTranslationKey(String translationKey) {
        this.translationKey = "item." + translationKey + ".name";
        this.translationKeyCompat = this.translationKey.substring(0, this.translationKey.length() - 5);
        return this;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKeyCompat;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.translationKeyCompat;
    }
}
