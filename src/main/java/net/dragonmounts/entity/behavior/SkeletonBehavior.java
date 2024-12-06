package net.dragonmounts.entity.behavior;

import net.dragonmounts.inits.ModSounds;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import net.dragonmounts.registry.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

public class SkeletonBehavior implements DragonType.Behavior {
    @Override
    public void tick(EntityTameableDragon dragon) {}

    @Override
    public boolean isHabitatEnvironment(Entity egg) {
        return egg.posY * 5 < egg.world.getHeight() && egg.getBrightness() < 0.25;
    }

    @Nullable
    @Override
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
        return null;
    }

    @Override
    public SoundEvent getLivingSound(EntityTameableDragon dragon) {
        return dragon.isBaby() ? ModSounds.ENTITY_DRAGON_HATCHLING_GROWL : ModSounds.ENTITY_SKELETON_DRAGON_GROWL;
    }
}
