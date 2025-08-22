package net.dragonmounts.client.breath.impl;

import net.dragonmounts.client.breath.IBreathParticleFactory;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WaterBreathParticle extends TexturedBreathParticle {
    public static final IBreathParticleFactory FACTORY = WaterBreathParticle::new;

    public WaterBreathParticle(
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
    protected void handleMovement() {
        if (this.rand.nextFloat() <= NORMAL_PARTICLE_CHANCE && this.rand.nextFloat() < this.node.getLifetimeFraction()) {
            this.world.spawnParticle(
                    EnumParticleTypes.WATER_SPLASH,
                    this.posX + (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F,
                    this.posY + 0.8F,
                    this.posZ + (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F,
                    this.motionX,
                    this.motionY,
                    this.motionZ
            );
        }
    }
}
