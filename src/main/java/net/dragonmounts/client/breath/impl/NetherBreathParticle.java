package net.dragonmounts.client.breath.impl;

import net.dragonmounts.client.breath.IBreathParticleFactory;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class NetherBreathParticle extends FlameBreathParticle {
    public static final IBreathParticleFactory FACTORY = NetherBreathParticle::new;

    public NetherBreathParticle(
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
    public int getBrightnessForRender() {
        return super.getBrightnessForRender() & 0x00FF0000 | 0xF0;
    }

    @Override
    protected EnumParticleTypes getParticleType() {
        return this.rand.nextFloat() <= SPECIAL_PARTICLE_CHANCE ? EnumParticleTypes.LAVA : EnumParticleTypes.SMOKE_LARGE;
    }
}

