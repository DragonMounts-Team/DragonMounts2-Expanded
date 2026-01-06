package net.dragonmounts.food;

import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.network.SSyncDragonAgePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static net.dragonmounts.DragonMounts.NETWORK_WRAPPER;
import static net.dragonmounts.capability.DMCapabilities.DRAGON_FOOD;

public class PoisonousPotato implements ICapabilityProvider, IDragonFood {
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

    @Override
    public boolean hasCapability(@Nullable Capability<?> capability, @Nullable EnumFacing facing) {
        return DRAGON_FOOD == capability;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nullable Capability<T> capability, @Nullable EnumFacing facing) {
        //noinspection DataFlowIssue
        return DRAGON_FOOD == capability ? DRAGON_FOOD.cast(this) : null;
    }
}
