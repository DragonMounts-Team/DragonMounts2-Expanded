package net.dragonmounts.client.breath.impl;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.client.breath.ClientBreathNodeEntity;
import net.dragonmounts.client.breath.IBreathParticleFactory;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class IceBreathParticle extends SimpleBreathParticle {
    public static final IBreathParticleFactory FACTORY = IceBreathParticle::new;

    public IceBreathParticle(
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
        return EnumParticleTypes.SNOW_SHOVEL;
    }
}
