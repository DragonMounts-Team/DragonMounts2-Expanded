package net.dragonmounts.entity.breath.sound;

import net.dragonmounts.entity.helper.DragonLifeStage;

import static net.dragonmounts.entity.breath.sound.SoundEffectName.*;

public enum SoundState {
    START(
            ADULT_BREATHE_FIRE_START,
            JUVENILE_BREATHE_FIRE_START,
            HATCHLING_BREATHE_FIRE_START,
            BREATHE_ICE_START,
            BREATHE_FOREST_START,
            BREATHE_AIR_START,
            BREATHE_WATER_START
    ),
    LOOP(
            ADULT_BREATHE_FIRE_LOOP,
            JUVENILE_BREATHE_FIRE_LOOP,
            HATCHLING_BREATHE_FIRE_LOOP,
            BREATHE_ICE_LOOP,
            BREATHE_FOREST_LOOP,
            BREATHE_AIR_LOOP,
            BREATHE_WATER_LOOP
    ),
    STOP(
            ADULT_BREATHE_FIRE_STOP,
            JUVENILE_BREATHE_FIRE_STOP,
            HATCHLING_BREATHE_FIRE_STOP,
            BREATHE_ICE_STOP,
            BREATHE_FOREST_STOP,
            BREATHE_AIR_STOP,
            BREATHE_WATER_STOP
    );

    public final SoundEffectName adult;
    public final SoundEffectName juvenile;
    public final SoundEffectName hatchling;
    public final SoundEffectName ice;
    public final SoundEffectName forest;
    public final SoundEffectName aether;
    public final SoundEffectName water;

    SoundState(
            SoundEffectName adult,
            SoundEffectName juvenile,
            SoundEffectName hatchling,
            SoundEffectName ice,
            SoundEffectName forest,
            SoundEffectName aether,
            SoundEffectName water
    ) {
        this.adult = adult;
        this.juvenile = juvenile;
        this.hatchling = hatchling;
        this.ice = ice;
        this.forest = forest;
        this.aether = aether;
        this.water = water;
    }

    public SoundEffectName getSoundByAge(DragonLifeStage stage) {
        switch (stage) {
            case JUVENILE:
                return this.juvenile;
            case ADULT:
                return this.adult;
            default:
                return this.hatchling;
        }
    }
}
