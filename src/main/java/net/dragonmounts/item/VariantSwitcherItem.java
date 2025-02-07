package net.dragonmounts.item;


import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;

public class VariantSwitcherItem extends Item {
    public VariantSwitcherItem() {
        this.setCreativeTab(DMItemGroups.MAIN);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (target instanceof TameableDragonEntity) {
            TameableDragonEntity dragon = (TameableDragonEntity) target;
            if (dragon.isOwner(player)) { // needs the actual owner even if dragon is unlocked
                if (target.world.isRemote) return true;
                DragonVariant current = dragon.getVariant();
                DragonVariant neo = current.type.variants.draw(dragon.getRNG(), current);
                if (current != neo) {
                    dragon.setVariant(neo);
                    dragon.world.playSound(null, player.getPosition(), DMSounds.DRAGON_SWITCH, SoundCategory.PLAYERS, 1, 1);
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                }
                return true;
            }
            player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.dragon.notOwner"), true);
        }
        return false;
    }
}