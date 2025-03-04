package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.DMSounds;
import net.minecraft.util.SoundEvent;

public class StormBreath extends WaterBreath {
    public StormBreath(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public SoundEvent getStartSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_START_ICE;
    }

    @Override
    public SoundEvent getLoopSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_LOOP_ICE;
    }

    @Override
    public SoundEvent getStopSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_STOP_ICE;
    }
}
