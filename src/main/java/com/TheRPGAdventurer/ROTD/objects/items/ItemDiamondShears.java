package com.TheRPGAdventurer.ROTD.objects.items;

import com.TheRPGAdventurer.ROTD.DragonMounts;
import com.TheRPGAdventurer.ROTD.DragonMountsTags;
import com.TheRPGAdventurer.ROTD.inits.ModTools;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.util.DMUtils;
import com.TheRPGAdventurer.ROTD.util.IHasModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemDiamondShears extends ItemShears implements IHasModel {

    private ToolMaterial material;
    private EntityTameableDragon dragon;

    public ItemDiamondShears(ToolMaterial material, String unlocalizedName) {
        this.setTranslationKey(unlocalizedName);
        this.setRegistryName(new ResourceLocation(DragonMountsTags.MOD_ID, unlocalizedName));
        this.setMaxDamage(345);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.TOOLS);

        ModTools.TOOLS.add(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GREEN + DMUtils.translateToLocal("item.diamondshears.info"));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, net.minecraft.util.EnumHand hand) {
        if (entity.world.isRemote) return false;

        if (entity instanceof EntityTameableDragon) {
            EntityTameableDragon target = (EntityTameableDragon) entity;
            BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);
            if (target.isShearable(itemstack, target.world, pos)) {
                List<ItemStack> drops = target.onSheared(itemstack, target.world, pos,
                        EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack));
                for (ItemStack stack : drops) {
                    if (player.inventory.addItemStackToInventory(stack)) {
                        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    } else {
                        player.dropItem(stack, false);
                    }
                }
                itemstack.damageItem(20, target);
            }
            return true;
        } else {
            return super.itemInteractionForEntity(itemstack, player, entity, hand);
        }
    }

    /**
     * This method determines where the item is displayed
     */
    @Override
    public @Nonnull CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{DragonMounts.mainTab};
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