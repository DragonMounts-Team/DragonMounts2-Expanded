package net.dragonmounts.food;

import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class Carrot implements IDragonFoodCapable {
    @Override
    public boolean tryFeed(TameableDragonEntity dragon, EntityPlayer player, Relation relation, ItemStack stack, EnumHand hand) {
        if (!dragon.isGrowthPaused()) return false;
        if (dragon.world.isRemote) {
            dragon.refreshForcedAgeTimer();
            return true;
        }
        dragon.setGrowthPaused(false);
        dragon.heal(0.125F);
        dragon.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1f, 0.8F);
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        player.swingArm(hand);
        return true;
    }
}
