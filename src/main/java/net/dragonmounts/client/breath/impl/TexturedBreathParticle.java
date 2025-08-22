package net.dragonmounts.client.breath.impl;

import net.dragonmounts.client.breath.ClientBreathNodeEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class TexturedBreathParticle extends ClientBreathNodeEntity {
    public final ResourceLocation texture;

    public TexturedBreathParticle(
            World level,
            Vec3d position,
            Vec3d direction,
            BreathPower power,
            ResourceLocation texture,
            float partialTicks
    ) {
        super(level, position, direction, power, partialTicks);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTexture() {
        return this.texture;
    }
}

