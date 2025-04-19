package net.dragonmounts.client.breath.impl;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.client.breath.ClientBreathNodeEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by TGG on 21/06/2015.
 * EntityFX that makes up the flame breath weapon; client side.
 *
 * Usage:
 * (1) create a new FlameBreathFX using createFlameBreathFX
 * (2) spawn it as per normal
 *
 */
public class FlameBreathFX extends ClientBreathNodeEntity {
    public static final ResourceLocation DRAGON_BREATH_TEXTURE = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/entities/breath_fire.png");

    /**
     * creates a single EntityFX from the given parameters.  Applies some random spread to direction.
     *
     * @param world
     * @param position              world [x,y,z] to spawn at (inates are the centre point of the fireball)
     * @param direction             initial world direction [x,y,z] - will be normalised.
     * @param power                 the power of the ball
     * @param partialTicksHeadStart if spawning multiple EntityFX per tick, use this parameter to spread the starting
     *                              location in the direction
     */
    public FlameBreathFX(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicksHeadStart) {
        super(world, position, direction, power, partialTicksHeadStart, false);
    }

    @Override
    public ResourceLocation getTexture() {
        return DRAGON_BREATH_TEXTURE;
    }
}

