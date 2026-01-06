package net.dragonmounts.food;

import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static net.dragonmounts.capability.DMCapabilities.DRAGON_FOOD;

public class Carrot implements ICapabilityProvider, IDragonFood {
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
