package net.dragonmounts.item;

import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class DragonArmorItem extends Item {
    public static final String TEXTURE_PREFIX = "textures/entities/dragon_armor/";
    public static final UUID MODIFIER_UUID = UUID.fromString("f4dbd212-cf15-57e9-977c-0019cc5a8933");
    public final ResourceLocation texture;
    public final int protection;

    public DragonArmorItem(ResourceLocation texture, int protection) {
        this.texture = texture;
        this.protection = protection;
        this.setMaxStackSize(1).setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        if (stack.isEmpty() || !(entity instanceof TameableDragonEntity)) return false;
        TameableDragonEntity dragon = (TameableDragonEntity) entity;
        if (dragon.isArmored() || Relation.denyIfNotOwner(dragon, player)) return false;
        if (dragon.world.isRemote) return true;
        if (player.capabilities.isCreativeMode) {
            ItemStack armor = stack.copy();
            armor.setCount(1);
            dragon.setArmor(armor);
        } else {
            dragon.setArmor(stack.splitStack(1));
        }
        player.swingArm(hand);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add("");
        tooltips.add(ClientUtil.translateToLocal("item.modifiers.equipped"));
        tooltips.add(TextFormatting.BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus.0", ItemStack.DECIMALFORMAT.format(this.protection), I18n.translateToLocal("attribute.name.generic.armor")));
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
