package net.dragonmounts.item;

import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.registry.DragonType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

public class DragonScaleBowItem extends ItemBow {
    public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "dragon_scale_bow";
    public final DragonType type;
    public final int enchantability;

    public DragonScaleBowItem(DragonType type, ToolMaterial tier) {
        this.type = type;
        this.setMaxDamage(tier.getMaxUses() / 4);
        this.enchantability = tier.getEnchantability() / 5;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {return 67000;}

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World level, EntityLivingBase user, int timeLeft) {
        if (!(user instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) user;
        boolean infinity = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        ItemStack ammo = this.findAmmo(player);
        int i = this.getMaxItemUseDuration(stack) - timeLeft;
        i = ForgeEventFactory.onArrowLoose(stack, level, player, i, infinity || !ammo.isEmpty());
        if (i < 0) return;
        if (ammo.isEmpty()) {
            if (!infinity) return;
            ammo = new ItemStack(Items.ARROW);
        }

        float f = getArrowVelocity(i);

        if (f >= 0.1D) {
            Item item = ammo.getItem();
            boolean flag1 = player.capabilities.isCreativeMode || (item instanceof ItemArrow && ((ItemArrow) item).isInfinite(ammo, stack, player));
            if (!level.isRemote) {
                EntityArrow arrow = ((ItemArrow) (item instanceof ItemArrow ? item : Items.ARROW)).createArrow(level, ammo, player);
                arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);

                if (f == 1.0F) {
                    arrow.setIsCritical(true);
                }

                int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                if (j > 0) {
                    arrow.setDamage(arrow.getDamage() + j * 0.5D + 0.5D);
                }

                int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

                if (k > 0) {
                    arrow.setKnockbackStrength(k);
                }

                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                    arrow.setFire(100);
                }

                stack.damageItem(1, player);

                if (flag1 || player.capabilities.isCreativeMode && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW)) {
                    arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                }

                level.spawnEntity(arrow);
            }

            level.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (!flag1 && !player.capabilities.isCreativeMode) {
                ammo.shrink(1);
                if (ammo.isEmpty()) {
                    player.inventory.deleteStack(ammo);
                }
            }

            player.addStat(StatList.getObjectUseStats(this));
        }
    }

    public static float getArrowVelocity(int charge) {
        float f = charge / 10.0F;// twice as much as vanilla
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack ingredient) {
        return ingredient.getItem() == this.type.getInstance(DragonScalesItem.class, null);
    }

    @Override
    public int getItemEnchantability() {
        return this.enchantability;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(this.type.getName());
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
