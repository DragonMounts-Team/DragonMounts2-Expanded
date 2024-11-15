/*
 ** 2012 August 23
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package top.dragonmounts.objects.entity.entitytameabledragon.helper;

import top.dragonmounts.util.math.MathX;
import net.minecraft.util.math.MathHelper;

import static top.dragonmounts.util.DMUtils.TICKS_PER_MINECRAFT_HOUR;

/**
 * Enum for dragon life stages. Used as aliases for the age value of dragons.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public enum DragonLifeStage {

  //order, duration of stage in hours of minecraft time, scaleAtStartOfStage, scaleAtEndOfStage
  EGG(null, 36, 0.25f, 0.25f),
  HATCHLING(EGG, 48, 0.04f, 0.09f),
  INFANT(HATCHLING, 24, 0.10f, 0.18f),
  PREJUVENILE(INFANT, 32, 0.19f, 0.60f),
  JUVENILE(PREJUVENILE, 60, 0.61f, 0.99f),
  ADULT(JUVENILE, 0, 1.00f, 1.00f);        // scale of the final stage should be 1.00F to avoid breaking other code

//  desired durations (30 Jun 2019)
//  egg = 30 minutes
//  hatchling = 45 minutes
//          infant = 14 minutes (just filler)
//  prejuvenile = 22 minutes (just filler)
//  juvenile = 50 minutes
//          adult = 1 hour

  public final String identifier;
  public final int durationTicks; // -1 means infinite
  public final float startScale;
  public final float finalScale;
  public final int boundaryTick;

  /**
   * Which life stage is the dragon in?
   * @param minecraftTimeHours = the duration of this stage in game time hours (each hour game time is 50 seconds in real life)
   * @param scaleAtEndOfStage size of this stage relative to the final scale (adult)
   */
  DragonLifeStage(DragonLifeStage prior, int minecraftTimeHours, float scaleAtStartOfStage, float scaleAtEndOfStage) {
    this.durationTicks = minecraftTimeHours * TICKS_PER_MINECRAFT_HOUR;
    this.startScale = scaleAtStartOfStage;
    this.finalScale = scaleAtEndOfStage;
    this.boundaryTick = prior == null ? this.durationTicks : prior.boundaryTick + this.durationTicks;
    this.identifier = this.name().toLowerCase();
  }

  /** true if we're in the egg, false otherwise
   * @return
   */
  public boolean isEgg() {
    return this == EGG;
  }

  /**
   * does this stage act like a minecraft baby
   * @return
   */
  public boolean isBaby() {
    return this == HATCHLING || this == INFANT;
  }

  public boolean isJuvenile() {
    return  this == JUVENILE || this == PREJUVENILE;
  }

  /**
   * is the dragon fully grown?
   * @return
   */
  public boolean isFullyGrown() {
    return this == ADULT;
  }

  public boolean isOldEnough(DragonLifeStage stage) {
    return this.ordinal() >= stage.ordinal();
  }

  /**
   * get the current life stage based on the dragon's age
   * @param ticksSinceCreation number of ticks since the egg was created
   * @return
   */
  public static DragonLifeStage getLifeStageFromTickCount(int ticksSinceCreation) {
    for (DragonLifeStage stage : values()) {
      if (ticksSinceCreation < stage.boundaryTick) return stage;
    }
    return DragonLifeStage.ADULT;
  }

  public static float getStageProgressFromTickCount(int ticksSinceCreation) {
    DragonLifeStage stage = getLifeStageFromTickCount(ticksSinceCreation);
    if (stage.durationTicks == 0) return 1.0F;
    return 1.0F + MathHelper.clamp(ticksSinceCreation - stage.boundaryTick, -1, 0) / (float) stage.durationTicks;
  }

  public static float getScaleFromTickCount(int ticksSinceCreation) {
    DragonLifeStage stage = getLifeStageFromTickCount(ticksSinceCreation);
    if (stage.durationTicks == 0) return stage.startScale;
    return MathX.lerp(
            stage.startScale,
            stage.finalScale,
            1.0F + MathHelper.clamp(ticksSinceCreation - stage.boundaryTick, -1, 0) / (float) stage.durationTicks
    );
  }

  public static int clipTickCountToValid(int ticksSinceCreation) {
    return MathHelper.clamp(ticksSinceCreation, 0, ADULT.boundaryTick);
  }
}
