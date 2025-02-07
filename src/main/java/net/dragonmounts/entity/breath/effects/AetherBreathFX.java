package net.dragonmounts.entity.breath.effects;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
public class AetherBreathFX extends ClientBreathNodeEntity {
    public static final ResourceLocation DRAGON_BREATH_TEXTURE = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/entities/breath_air.png");

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
    public AetherBreathFX(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicksHeadStart) {
        super(world, position, direction, power, partialTicksHeadStart, true);
    }

    @Override
    protected void handleMovement() {
//        if (ENDER_CHANCE != 0 && rand.nextFloat() < lifetimeFraction && rand.nextFloat() <= ENDER_CHANCE && new BlockPos(posX,posY,posZ) !=null) {
//            world.spawnParticle(getParticleType(), posX, posY, posZ, motionX * 1.5, motionY * 1.5, motionZ * 1.5, Block.getStateId(world.getBlockState(new BlockPos(posX,posY,posZ))));
//        }

        // smoke / steam when hitting water.  node is responsible for aging to death
//        if (handleWaterMovement()) {
//            world.spawnParticle(getParticleType(), posX, posY, posZ, 0, 0, 0);
//        }
    }

    @Override
    protected EnumParticleTypes getParticleType() {
        BlockPos pos = new BlockPos(posX, posY, posZ);
        if (world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) {
            return EnumParticleTypes.BLOCK_CRACK;
        }
        return null;
    }

    @Override
    public ResourceLocation getTexture() {
        return DRAGON_BREATH_TEXTURE;
    }

    @Override
    public float getRenderScale() {
        return this.scale * 0.625F;
    }
}
