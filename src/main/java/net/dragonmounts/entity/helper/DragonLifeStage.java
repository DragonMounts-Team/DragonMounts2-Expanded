/*
 ** 2012 August 23
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.helper;

import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.util.math.MathX;
import net.minecraft.util.math.MathHelper;

import static net.dragonmounts.util.DMUtils.TICKS_PER_MINECRAFT_HOUR;

/**
 * Enum for dragon life stages. Used as aliases for the age value of dragons.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public enum DragonLifeStage {
    EGG(null, 36, 0.25F, 0.25F, BreathPower.SMALL),
    HATCHLING(EGG, 48, 0.04F, 0.09F, BreathPower.SMALL),
    INFANT(HATCHLING, 24, 0.10f, 0.18F, BreathPower.SMALL),
    PREJUVENILE(INFANT, 32, 0.19F, 0.60F, BreathPower.SMALL),
    JUVENILE(PREJUVENILE, 60, 0.61F, 0.99F, BreathPower.MEDIUM),
    // scale of the final stage should be 1.00F to avoid breaking other code
    ADULT(JUVENILE, 0, 1.00F, 1.00f, BreathPower.LARGE);

//  desired durations (30 Jun 2019)
//  egg = 30 minutes
//  hatchling = 45 minutes
//          infant = 14 minutes (just filler)
//  prejuvenile = 22 minutes (just filler)
//  juvenile = 50 minutes
//          adult = 1 hour

    public final String identifier;
    public final String translationKey;
    public final BreathPower power;
    public final int durationTicks;
    public final float startScale;
    public final float finalScale;
    public final int boundaryTick;

    DragonLifeStage(DragonLifeStage prior, int minecraftTimeHours, float scaleAtStartOfStage, float scaleAtEndOfStage, BreathPower power) {
        this.durationTicks = minecraftTimeHours * TICKS_PER_MINECRAFT_HOUR;
        this.startScale = scaleAtStartOfStage;
        this.finalScale = scaleAtEndOfStage;
        this.boundaryTick = prior == null ? this.durationTicks : prior.boundaryTick + this.durationTicks;
        this.identifier = this.name().toLowerCase();
        this.translationKey = "life_stage.dragon." + this.identifier;
        this.power = power;
    }

    /**
     * does this stage act like a minecraft baby
     */
    public boolean isBaby() {
        return this == HATCHLING || this == INFANT;
    }

    public boolean isJuvenile() {
        return this == JUVENILE || this == PREJUVENILE;
    }

    public boolean isOldEnough(DragonLifeStage stage) {
        return this.ordinal() >= stage.ordinal();
    }

    /**
     * get the current life stage based on the dragon's age
     *
     * @param ticksSinceCreation number of ticks since the egg was created
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
        return 1.0F + MathHelper.clamp((ticksSinceCreation - stage.boundaryTick) / (float) stage.durationTicks, -1.0F, 0.0F);
    }

    public static float getScaleFromTickCount(int ticksSinceCreation) {
        DragonLifeStage stage = getLifeStageFromTickCount(ticksSinceCreation);
        if (stage.durationTicks == 0) return stage.startScale;
        return MathX.lerp(
                stage.startScale,
                stage.finalScale,
                1.0F + MathHelper.clamp((ticksSinceCreation - stage.boundaryTick) / (float) stage.durationTicks, -1.0F, 0.0F)
        );
    }

    public static int clipTickCountToValid(int ticksSinceCreation) {
        return MathHelper.clamp(ticksSinceCreation, 0, ADULT.boundaryTick);
    }
}
