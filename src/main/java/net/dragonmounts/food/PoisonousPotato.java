package net.dragonmounts.food;

import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.network.SSyncDragonAgePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

import static net.dragonmounts.DragonMounts.NETWORK_WRAPPER;

public class PoisonousPotato implements IDragonFoodCapable {
    @Override
    public boolean tryFeed(TameableDragonEntity dragon, EntityPlayer player, Relation relation, ItemStack stack, EnumHand hand) {
        if (Relation.OWNER != relation) {
            relation.onDeny(player);
            return false;
        }
        if (dragon.isGrowthPaused()) return false;
        if (dragon.world.isRemote) {
            player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.dragon.growthPaused"), true);
            return true;
        }
        dragon.setGrowthPaused(true);
        NETWORK_WRAPPER.sendToAllTracking(new SSyncDragonAgePacket(dragon.getEntityId(), dragon.getGrowingAge(), dragon.getLifeStage()), dragon);
        dragon.attackEntityFrom(DamageSource.STARVE, 1.0F);
        dragon.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, 0.8F);
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        player.swingArm(hand);
        return true;
    }
}
