package net.dragonmounts.entity.behavior;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.weapons.BreathWeapon;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

public class SkeletonBehavior implements DragonType.Behavior {
    @Override
    public void tick(TameableDragonEntity dragon) {}

    @Override
    public boolean isHabitatEnvironment(Entity egg) {
        return egg.posY * 5 < egg.world.getHeight() && egg.getBrightness() < 0.25;
    }

    @Nullable
    @Override
    public BreathWeapon createBreathWeapon(TameableDragonEntity dragon) {
        return null;
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isBaby() ? DMSounds.ENTITY_DRAGON_HATCHLING_GROWL : DMSounds.ENTITY_SKELETON_DRAGON_GROWL;
    }
}
