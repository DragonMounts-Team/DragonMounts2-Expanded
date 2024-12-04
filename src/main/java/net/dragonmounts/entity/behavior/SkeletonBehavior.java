package net.dragonmounts.entity.behavior;

import net.dragonmounts.inits.ModSounds;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import net.dragonmounts.registry.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class SkeletonBehavior implements DragonType.Behavior {
    @Override
    public void tick(EntityTameableDragon dragon) {}

    @Override
    public boolean isHabitatEnvironment(Entity egg) {
        if (egg.posY * 4 > egg.world.getHeight()) return false;// woah dude, too high!
        return egg.world.getLightBrightness(new BlockPos(egg)) < 4;// too bright!
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
