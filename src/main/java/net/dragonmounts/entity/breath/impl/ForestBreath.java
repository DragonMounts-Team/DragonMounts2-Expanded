package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.DMSounds;
import net.minecraft.util.SoundEvent;

public class ForestBreath extends FireBreath {
    public ForestBreath(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public SoundEvent getStartSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_START_FOREST;
    }

    @Override
    public SoundEvent getLoopSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_LOOP_FOREST;
    }

    @Override
    public SoundEvent getStopSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_STOP_FOREST;
    }
}
