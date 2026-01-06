/*
 ** 2012 August 23
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.util.math.MathX;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.IStringSerializable;

import java.util.UUID;
import java.util.function.IntSupplier;

import static net.dragonmounts.config.DMConfig.*;

public enum DragonLifeStage implements IStringSerializable {
    EGG(BreathPower.SMALL, 0.25F, 0.25F, MIN_INCUBATION_DURATION),
    HATCHLING(BreathPower.SMALL, 0.04F, 0.09F, HATCHLING_STAGE_DURATION),
    INFANT(BreathPower.SMALL, 0.10F, 0.18F, INFANT_STAGE_DURATION),
    FLEDGLING(BreathPower.SMALL, 0.19F, 0.60F, FLEDGLING_STAGE_DURATION),
    JUVENILE(BreathPower.MEDIUM, 0.61F, 0.99F, JUVENILE_STAGE_DURATION),
    // scale of the final stage should be 1.00F to avoid breaking other code
    ADULT(BreathPower.LARGE, 1.00F, 1.00F, () -> 0);
    public static final UUID MODIFIER_ID = UUID.fromString("856d4ba4-9ffe-4a52-8606-890bb9be538b");
    public static final String SERIALIZATION_KEY = "LifeStage";
    public static final Object2ObjectMap<String, DragonLifeStage> BY_NAME;

    public static AttributeModifier makeModifier(int operator, double amount) {
        return new AttributeModifier(MODIFIER_ID, "LifeStageBonus", amount, operator).setSaved(false);
    }

    public static DragonLifeStage byId(int id) {
        DragonLifeStage[] values = values();
        return id < 0 || id >= values.length ? DragonLifeStage.ADULT : values[id];
    }

    public static DragonLifeStage byName(String name) {
        return BY_NAME.getOrDefault(name, DragonLifeStage.ADULT);
    }

    public static float getProgress(int age, float duration) {
        return age < 0 ? 1.0F + age / duration : 1.0F - age / duration;
    }
    public final String identifier;
    public final String translationKey;
    public final BreathPower power;
    public final IntSupplier duration;
    public final float startScale;
    public final float finalScale;

    DragonLifeStage(BreathPower power, float startScale, float finalScale, IntSupplier duration) {
        this.identifier = this.name().toLowerCase();
        this.translationKey = "life_stage.dragon." + this.identifier;
        this.power = power;
        this.startScale = startScale;
        this.finalScale = finalScale;
        this.duration = duration;
    }

    /// @return does this stage act like a Minecraft baby
    public boolean isBaby() {
        return this == HATCHLING || this == INFANT;
    }

    public boolean isOldEnough(DragonLifeStage stage) {
        return this.ordinal() >= stage.ordinal();
    }

    @Override
    public String getName() {
        return this.identifier;
    }

    public float getScale(int age) {
        int duration = this.duration.getAsInt();
        return duration == 0 ? 1.0F : MathX.lerp(this.startScale, this.finalScale, getProgress(age, duration));
    }

    public float getAverageScale() {
        return (this.finalScale + this.startScale) * 0.5F;
    }

    static {
        Object2ObjectOpenHashMap<String, DragonLifeStage> stages = new Object2ObjectOpenHashMap<>();
        for (DragonLifeStage stage : values()) {
            stages.put(stage.identifier, stage);
        }
        BY_NAME = Object2ObjectMaps.unmodifiable(stages);
    }
}
