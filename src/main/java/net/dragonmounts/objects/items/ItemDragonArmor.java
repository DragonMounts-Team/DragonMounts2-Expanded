package net.dragonmounts.objects.items;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.inits.DMArmors;
import net.dragonmounts.inits.DMItemGroups;
import net.dragonmounts.inits.ModItems;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;

public class ItemDragonArmor extends Item {
    public String name;

    public ItemDragonArmor(String name) {
        this.name = name;
        this.setTranslationKey(name);
        this.maxStackSize = 1;
        this.setRegistryName(DragonMountsTags.MOD_ID, name);
        this.setCreativeTab(CreativeTabs.COMBAT);

        ModItems.ITEMS.add(this);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        if (stack.isEmpty() || !(entity instanceof EntityTameableDragon)) return false;
        EntityTameableDragon dragon = (EntityTameableDragon) entity;
        if (!dragon.isOwner(player) || DMArmors.DRAGON_ARMORS.getInt(stack.getItem()) <= dragon.getArmorType())
            return false;
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

    /**
     * This method determines where the item is displayed
     */
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