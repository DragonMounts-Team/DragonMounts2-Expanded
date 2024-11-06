package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath;

import net.minecraft.util.EnumFacing;

/**
* Created by TGG on 7/08/2015.
 * Models a block which is being affected by the breath weapon
 * Every tick that a block is exposed to the breath weapon, its "hit density" increases.
*/
public class BreathAffectedBlock implements IBreathEffectHandler {
  public BreathAffectedBlock() {
    hitDensity = new float[EnumFacing.values().length];
    timeSinceLastHit = 0;
  }

  /**
   * increases the hit density of the specified face.
   * @param face the face being hit; null = no particular face
   * @param increase the amount to increase the hit density by
   */
  public void addHitDensity(EnumFacing face, float increase) {
    if (face == null) {
      increase /= EnumFacing.values().length;
      for (EnumFacing facing : EnumFacing.values()) {
        hitDensity[facing.getIndex()] += increase;
      }
    } else {
      hitDensity[face.getIndex()] += increase;
    }
    timeSinceLastHit = 0;
  }

  public float getHitDensity(EnumFacing face)
  {
    return hitDensity[face.getIndex()];
  }

  public float getMaxHitDensity() {
    float maxDensity = 0;
    for (EnumFacing facing : EnumFacing.values()) {
      maxDensity = Math.max(maxDensity, hitDensity[facing.getIndex()]);
    }
    return maxDensity;
  }

  private final float BLOCK_DECAY_PERCENTAGE_PER_TICK = 10.0F;
  private final float BLOCK_RESET_EFFECT_THRESHOLD = 0.01F;
  private final int TICKS_BEFORE_DECAY_STARTS = 10;

  /** updates the breath weapon's effect for a given block
   *   called every tick; used to decay the cumulative effect on the block
   *   for example - a block being gently bathed in flame might gain 0.2 every time from the beam, and lose 0.2 every
   *     tick in this method.
   */
  @Override
  public boolean decayEffectTick() {
    if (++timeSinceLastHit < TICKS_BEFORE_DECAY_STARTS) return this.isUnaffected();
    boolean flag = true;
    for (EnumFacing facing : EnumFacing.values()) {
      float density = hitDensity[facing.getIndex()];
      density *= (1.0F - BLOCK_DECAY_PERCENTAGE_PER_TICK / 100.0F);
      if (density < BLOCK_RESET_EFFECT_THRESHOLD) {
        density = 0.0F;//expired
      } else {
        flag = false;
      }
      hitDensity[facing.getIndex()] = density;
    }
    return flag;
  }

  /**
   * Check if this block is unaffected by the breath weapon
   * @return true if the block is currently unaffected
   */
  @Override
  public boolean isUnaffected() {
    for (EnumFacing facing : EnumFacing.values()) {
      if (hitDensity[facing.getIndex()] >= BLOCK_RESET_EFFECT_THRESHOLD) return false;
    }
    return true;
  }

  private final float[] hitDensity;
  private int timeSinceLastHit;
}
