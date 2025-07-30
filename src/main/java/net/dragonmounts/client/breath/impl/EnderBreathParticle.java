package net.dragonmounts.client.breath.impl;

import net.dragonmounts.client.breath.IBreathParticleFactory;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EnderBreathParticle extends SimpleBreathParticle {
    public static final IBreathParticleFactory FACTORY = EnderBreathParticle::new;

    public EnderBreathParticle(
            World level,
            Vec3d position,
            Vec3d direction,
            BreathPower power,
            ResourceLocation texture,
            float partialTicks
    ) {
        super(level, position, direction, power, texture, partialTicks);
    }

    @Override
    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.DRAGON_BREATH;
    }
}
