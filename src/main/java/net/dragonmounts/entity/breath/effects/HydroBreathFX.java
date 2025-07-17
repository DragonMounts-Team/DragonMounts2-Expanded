package net.dragonmounts.entity.breath.effects;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.EnumParticleTypes;
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
public class HydroBreathFX extends ClientBreathNodeEntity {
    private static final ResourceLocation DRAGON_BREATH_TEXTURE = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/entities/breath_hydro.png");

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
    public HydroBreathFX(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicksHeadStart) {
        super(world, position, direction, power, partialTicksHeadStart, false);
    }

    @Override
    protected void handleMovement() {
        if (this.rand.nextFloat() <= NORMAL_PARTICLE_CHANCE && this.rand.nextFloat() < this.node.getLifetimeFraction()) {
            this.world.spawnParticle(
                    this.getParticleType(),
                    this.posX + (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F,
                    0.8F,
                    this.posZ + (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F,
                    this.motionX,
                    this.motionY,
                    this.motionZ
            );
        }
    }

    @Override
    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.WATER_SPLASH;
    }

    @Override
    public ResourceLocation getTexture() {
        return DRAGON_BREATH_TEXTURE;
    }
}
