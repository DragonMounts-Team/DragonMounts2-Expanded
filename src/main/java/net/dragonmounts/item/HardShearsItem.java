package net.dragonmounts.item;

import net.dragonmounts.capability.IHardShears;
import net.dragonmounts.init.DMCapabilities;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.util.DMUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HardShearsItem extends ItemShears implements IHardShears, ICapabilityProvider {
    public final ToolMaterial tier;
    public final float speedFactor;

    public HardShearsItem(ToolMaterial tier) {
        this.tier = tier;
        this.speedFactor = tier.getEfficiency() / ToolMaterial.IRON.getEfficiency();
        this.setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public int onShear(ItemStack stack, EntityPlayer player, TameableDragonEntity dragon) {
        DragonScalesItem item = dragon.getVariant().type.getInstance(DragonScalesItem.class, null);
        if (item == null) return 0;
        ItemStack scales = new ItemStack(item, 2 + dragon.getRNG().nextInt(3));
        if (player.inventory.addItemStackToInventory(scales)) {
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        } else {
            player.dropItem(scales, false);
        }
        stack.damageItem(20, player);
        return 3000;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        return speed > 1.0F ? speed * this.speedFactor : speed;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack ingredient) {
        ItemStack matcher = this.tier.getRepairItemStack();
        return !matcher.isEmpty() && OreDictionary.itemMatches(matcher, ingredient, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(TextFormatting.GRAY + DMUtils.translateToLocal("tooltip.dragonmounts.hard_shears"));
    }

    @Override
    public @Nonnull CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{DMItemGroups.MAIN};
    }

    @Override
    protected boolean isInCreativeTab(CreativeTabs targetTab) {
        for (CreativeTabs tab : this.getCreativeTabs())
            if (tab == targetTab) return true;
        return targetTab == CreativeTabs.SEARCH;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return this;
    }

    @Override
    public boolean hasCapability(@Nullable Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == DMCapabilities.HARD_SHEARS;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nullable Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == DMCapabilities.HARD_SHEARS ? (T) this : null;
    }
}
