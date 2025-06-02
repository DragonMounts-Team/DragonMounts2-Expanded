package net.dragonmounts.entity.breath;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
* Created by TGG on 7/08/2015.
 * Models a block which is being affected by the breath weapon
 * Every tick that a block is exposed to the breath weapon, its "hit density" increases.
*/
public class BreathAffectedBlock implements IBreathEffectHandler {
  private static final float BLOCK_DECAY_FACTOR_PER_TICK = 0.9F;
  private static final float BLOCK_RESET_EFFECT_THRESHOLD = 0.01F;
  private static final int TICKS_BEFORE_DECAY_STARTS = 10;
  private final float[] hitDensity;
  private int timeSinceLastHit;

  public BreathAffectedBlock() {
    hitDensity = new float[EnumFacing.values().length];
    timeSinceLastHit = 0;
  }

  /**
   * increases the hit density of the specified face.
   * @param face the face being hit; null = no particular face
   * @param increase the amount to increase the hit density by
   */
  public void addHitDensity(@Nullable EnumFacing face, float increase) {
    final float[] hit = this.hitDensity;
    if (face == null) {
      int len = hit.length;
      increase /= len;
      for (int i = 0; i < len; ++i) {
        hit[i] += increase;
      }
    } else {
      hit[face.getIndex()] += increase;
    }
    timeSinceLastHit = 0;
  }

  public float getHitDensity(EnumFacing face) {
    return hitDensity[face.getIndex()];
  }

  public float getMaxHitDensity() {
    float maxDensity = 0;
    for (float density : this.hitDensity) {
      if (density > maxDensity) {
        maxDensity = density;
      }
    }
    return maxDensity;
  }

  /** updates the breath weapon's effect for a given block
   *   called every tick; used to decay the cumulative effect on the block
   *   for example - a block being gently bathed in flame might gain 0.2 every time from the beam, and lose 0.2 every
   *     tick in this method.
   */
  @Override
  public boolean decayEffectTick() {
    if (++timeSinceLastHit < TICKS_BEFORE_DECAY_STARTS) return this.isUnaffected();
    boolean flag = true;
    final float[] hit = this.hitDensity;
    for (int i = 0, end = hit.length; i < end; ++i) {
      if ((hit[i] *= BLOCK_DECAY_FACTOR_PER_TICK) < BLOCK_RESET_EFFECT_THRESHOLD) {
        hit[i] = 0.0F;//expired
      } else {
        flag = false;
      }
    }
    return flag;
  }

  /**
   * Check if this block is unaffected by the breath weapon
   * @return true if the block is currently unaffected
   */
  @Override
  public boolean isUnaffected() {
    for (float density : this.hitDensity) {
      if (density >= BLOCK_RESET_EFFECT_THRESHOLD) return false;
    }
    return true;
  }
}
