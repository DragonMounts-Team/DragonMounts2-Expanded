package net.dragonmounts.type;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

public class SkeletonType extends DragonType {
    public SkeletonType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public boolean isHabitatEnvironment(Entity egg) {
        return egg.posY * 5 < egg.world.getHeight() && egg.getBrightness() < 0.25;
    }

    @Nullable
    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return null;
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isChild() ? DMSounds.DRAGON_PURR_SKELETON_HATCHLING : DMSounds.DRAGON_PURR_SKELETON;
    }
}
