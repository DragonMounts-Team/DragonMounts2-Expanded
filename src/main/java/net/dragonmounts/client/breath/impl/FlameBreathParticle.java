package net.dragonmounts.client.breath.impl;

import net.dragonmounts.client.breath.IBreathParticleFactory;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlameBreathParticle extends TexturedBreathParticle {
    public static final IBreathParticleFactory FACTORY = FlameBreathParticle::new;

    public FlameBreathParticle(
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
    protected void handleMovement() {
        // spawn a smoke trail after some time
        if (this.rand.nextFloat() < NORMAL_PARTICLE_CHANCE && this.rand.nextFloat() < this.node.getLifetimeFraction()) {
            this.world.spawnParticle(this.getParticleType(), this.posX, this.posY, this.posZ, this.motionX * 0.5, this.motionY * 0.5, this.motionZ * 0.5);
        }

        // smoke / steam when hitting water.  node is responsible for aging to death
        if (this.handleWaterMovement()) {
            this.world.spawnParticle(this.getParticleType(), this.posX, this.posY, this.posZ, 0, 0, 0);
        }
    }

    protected EnumParticleTypes getParticleType() {
        return this.rand.nextFloat() < SPECIAL_PARTICLE_CHANCE ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.SMOKE_NORMAL;
    }
}