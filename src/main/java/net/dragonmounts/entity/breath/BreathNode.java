package net.dragonmounts.entity.breath;

import net.dragonmounts.util.math.MathX;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by TGG on 30/07/2015.
 * BreathNode represents the age, size, and initial speed of each node in a breath weapon stream;
 * It is used with an associated Entity:
 * BreathNode tracks the age and size
 * Entity tracks the position, motion, and collision detection
 * <p>
 * updateAge() is used to keep the two synchronised
 * <p>
 * A breathnode has three characteristic diameters:
 * 1) getCurrentRenderDiameter() - the size for rendering
 * 2) getCurrentDiameterOfEffect() - the diameter that will be affected by the breath weapon node
 * 3) getCurrentAABBCollionSize() - the size used for collision detection with entities or the world
 */
public class BreathNode {
  private static final int DEFAULT_AGE_IN_TICKS = 40;
  private static final float RATIO_OF_RENDER_DIAMETER_TO_EFFECT_DIAMETER = 1.0F;
  private static final float RATIO_OF_COLLISION_DIAMETER_TO_EFFECT_DIAMETER = 0.5F;  // change to 0.5F
  private static final float INITIAL_SPEED = 1.2F; // blocks per tick at full speed
  private static final float NODE_DIAMETER_IN_BLOCKS = 2.0F;
  private static final float YOUNG_AGE = 0.25F;
  private static final float OLD_AGE = 0.75F;

  public final BreathPower power;

  public BreathNode(@Nonnull BreathPower power) {
    this.power = power;
  }

  private float ageTicks;

  private float relativeSizeOfThisNode = 1.0F;
  private float relativeLifetimeOfThisNode = 1.0F;

  private static final double SPEED_VARIATION_ABS = 0.1;  // plus or minus this amount (3 std deviations)
  private static final double AGE_VARIATION_FACTOR = 0.25;  // plus or minus this amount (3 std deviations)
  private static final double SIZE_VARIATION_FACTOR = 0.25;   // plus or minus this amount (3 std deviations)

  /**
   * Randomise the maximum lifetime and the node size
   *
   * @param rand
   */
  public void randomiseProperties(Random rand) {
    relativeLifetimeOfThisNode = (float) (MathX.getTruncatedGaussian(rand, 1, AGE_VARIATION_FACTOR));
    relativeSizeOfThisNode = (float) (MathX.getTruncatedGaussian(rand, 1, SIZE_VARIATION_FACTOR));
  }

  /**
   * Get an initial motion vector for this node, randomised around the initialDirection
   *
   * @param initialDirection the initial direction
   * @param rand
   * @return the initial motion vector (speed and direction)
   */
  public Vec3d getRandomisedStartingMotion(Vec3d initialDirection, Random rand) {
    float initialSpeed = getStartingSpeed();
    Vec3d direction = initialDirection.normalize();

    double actualMotionX = direction.x + MathX.getTruncatedGaussian(rand, 0, SPEED_VARIATION_ABS);
    double actualMotionY = direction.y + MathX.getTruncatedGaussian(rand, 0, SPEED_VARIATION_ABS);
    double actualMotionZ = direction.z + MathX.getTruncatedGaussian(rand, 0, SPEED_VARIATION_ABS);
    actualMotionX *= initialSpeed;
    actualMotionY *= initialSpeed;
    actualMotionZ *= initialSpeed;
    return new Vec3d(actualMotionX, actualMotionY, actualMotionZ);
  }

  public float getStartingSpeed() {
    return this.power.speed * INITIAL_SPEED;
  }

  public float getMaxLifeTime() {
    return this.power.lifetime * relativeLifetimeOfThisNode * DEFAULT_AGE_IN_TICKS;
  }

  public float getAgeTicks() {
    return ageTicks;
  }

  public boolean isDead() {
    return ageTicks > getMaxLifeTime();
  }

  /**
   * Update the age of the node based on what is happening (collisions) to the associated entity
   * Should be called once per tick
   *
   * @param parentEntity the entity associated with this node
   */
  public void updateAge(Entity parentEntity) {
    if (parentEntity.isInWater()) {  // extinguish in water
      ageTicks = getMaxLifeTime() + 1;
      return;
    }

    if (ageTicks++ > getMaxLifeTime()) {
      return;
    }

    // collision ages breath node faster
    if (parentEntity.collided) {
      ageTicks += 5;
    }

    // slow breath nodes age very fast (they look silly when sitting still)
    final double SPEED_THRESHOLD = getStartingSpeed() * 0.25;
    double speedSQ = parentEntity.motionX * parentEntity.motionX + parentEntity.motionY * parentEntity.motionY + parentEntity.motionZ * parentEntity.motionZ;
    if (speedSQ < SPEED_THRESHOLD * SPEED_THRESHOLD) {
      ageTicks += 20;
    }
  }

  /**
   * get the current render size (diameter) of the breathnode in blocks
   *
   * @return the rendering size (diameter) of the breathnode in blocks
   */
  public float getCurrentRenderDiameter() {
    return getCurrentDiameterOfEffect() * RATIO_OF_RENDER_DIAMETER_TO_EFFECT_DIAMETER;
  }

  /**
   * get the current width and height of the breathnode collision AABB, in blocks
   *
   * @return the width and height of the breathnode collision AABB, in blocks
   */
  public float getCurrentAABBcollisionSize() {
    return getCurrentDiameterOfEffect() * RATIO_OF_COLLISION_DIAMETER_TO_EFFECT_DIAMETER;
  }

  /**
   * get the current size (diameter) of the area of effect of the breath node, in blocks
   *
   * @return the size (diameter) of the area of effect of the breathnode in blocks
   */
  public float getCurrentDiameterOfEffect() {
    float lifetimeFraction = getLifetimeFraction();

    float fractionOfFullSize = 1.0F;
    if (lifetimeFraction < YOUNG_AGE) {
      fractionOfFullSize = MathHelper.sin(lifetimeFraction / YOUNG_AGE * MathX.PI_F * 0.5F);
    }

    final float NODE_MAX_SIZE = NODE_DIAMETER_IN_BLOCKS * this.power.size * relativeSizeOfThisNode;
    final float INITIAL_SIZE = 0.2F * NODE_MAX_SIZE;
    return INITIAL_SIZE + (NODE_MAX_SIZE - INITIAL_SIZE) * MathHelper.clamp(fractionOfFullSize, 0.0F, 1.0F);
  }


  /**
   * returns the current intensity of the node (eg for flame = how hot it is)
   *
   * @return current relative intensity - 0.0 = none, 1.0 = full
   */
  public float getCurrentIntensity() {
    float lifetimeFraction = getLifetimeFraction();

    float fractionOfFullPower = 1.0F;
    if (lifetimeFraction >= 1.0F) return 0.0F;
    if (lifetimeFraction < YOUNG_AGE) {
      fractionOfFullPower = MathHelper.sin(lifetimeFraction / YOUNG_AGE * MathX.PI_F * 0.5F);
    } else if (lifetimeFraction > OLD_AGE) {
      fractionOfFullPower = MathHelper.sin((1.0F - lifetimeFraction) / (1.0F - OLD_AGE) * MathX.PI_F * 0.5F);
    }
    return fractionOfFullPower * this.power.intensity;
  }


  public float getLifetimeFraction() {
    return MathHelper.clamp(this.ageTicks / this.getMaxLifeTime(), 0.0F, 1.0F);
  }
}

