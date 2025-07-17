package net.dragonmounts.item;


import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

public class VariationOrbItem extends Item {
    public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "variation_orb";
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (target instanceof TameableDragonEntity) {
            TameableDragonEntity dragon = (TameableDragonEntity) target;
            // requires the actual owner even if dragon is unlocked
            if (Relation.denyIfNotOwner(dragon, player)) return false;
            if (target.world.isRemote) return true;
            DragonVariant current = dragon.getVariant();
            DragonVariant neo = current.type.variants.draw(dragon.getRNG(), current);
            if (current != neo) {
                dragon.setVariant(neo);
                dragon.world.playSound(null, player.getPosition(), DMSounds.VARIATION_ORB_ACTIVATE, SoundCategory.PLAYERS, 1, 1);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(TextFormatting.GRAY + ClientUtil.translateToLocal("tooltip.dragonmounts.variation_orb"));
    }
}