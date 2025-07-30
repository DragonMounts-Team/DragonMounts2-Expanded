package net.dragonmounts.client.breath;

import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IBreathParticleFactory {
    ClientBreathNodeEntity createParticle(
            World level,
            Vec3d position,
            Vec3d direction,
            BreathPower power,
            ResourceLocation texture,
            float partialTicks
    );
}