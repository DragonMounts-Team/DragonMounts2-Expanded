package net.dragonmounts.item;

import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMItemGroups;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static net.dragonmounts.DragonMountsTags.MOD_ID;
import static net.dragonmounts.util.ItemUtil.isInCreativeInventory;

public class DragonArmorItem extends Item {
    public static final UUID MODIFIER_UUID = UUID.fromString("f4dbd212-cf15-57e9-977c-0019cc5a8933");
    public static final ResourceLocation BUILTIN_MATERIAL_COPPER = new ResourceLocation(MOD_ID, "copper");
    public static final ResourceLocation BUILTIN_MATERIAL_IRON = new ResourceLocation(MOD_ID, "iron");
    public static final ResourceLocation BUILTIN_MATERIAL_GOLD = new ResourceLocation(MOD_ID, "gold");
    public static final ResourceLocation BUILTIN_MATERIAL_EMERALD = new ResourceLocation(MOD_ID, "emerald");
    public static final ResourceLocation BUILTIN_MATERIAL_DIAMOND = new ResourceLocation(MOD_ID, "diamond");
    public final ResourceLocation material;
    public final int protection;

    public DragonArmorItem(ResourceLocation material, int protection) {
        this.material = material;
        this.protection = protection;
        this.setMaxStackSize(1).setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        if (stack.isEmpty() || !(entity instanceof TameableDragonEntity)) return false;
        TameableDragonEntity dragon = (TameableDragonEntity) entity;
        if (dragon.isArmored() || Relation.denyIfNotOwner(dragon, player)) return false;
        if (!dragon.world.isRemote) {
            dragon.armor.placeItem(stack, 1, player.capabilities.isCreativeMode);
            player.swingArm(hand);
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add("");
        tooltips.add(ClientUtil.translateToLocal("item.modifiers.body"));
        tooltips.add(TextFormatting.BLUE + I18n.format("attribute.modifier.plus.0", ItemStack.DECIMALFORMAT.format(this.protection), ClientUtil.translateToLocal("attribute.name.generic.armor")));
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
