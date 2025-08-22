package net.dragonmounts.client.breath.impl;

import net.dragonmounts.client.breath.IBreathParticleFactory;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AirflowBreathParticle extends TexturedBreathParticle {
    public static final IBreathParticleFactory FACTORY = AirflowBreathParticle::new;

    public AirflowBreathParticle(
            World level,
            Vec3d position,
            Vec3d direction,
            BreathPower power,
            ResourceLocation texture,
            float partialTicks
    ) {
        super(level, position, direction, power, texture, partialTicks);
        this.rollSpeed = this.rand.nextBoolean() ? 60.0F : -60.0F;
    }

    @Override
    protected void handleMovement() {}

    @Override
    public float getRenderScale() {
        return this.scale * 0.625F;
    }
}
