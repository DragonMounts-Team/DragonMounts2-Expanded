package net.dragonmounts.client.breath;

import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.util.math.MathX;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiFunction;

public abstract class BreathSound extends PositionedSound implements ITickableSound {
    public final BreathSoundHandler handler;
    private boolean isDone;

    public BreathSound(BreathSoundHandler handler, BiFunction<DragonBreath, DragonLifeStage, SoundEvent> sound) {
        super(sound.apply(handler.getBreath(), handler.getStage()), SoundCategory.NEUTRAL);
        this.handler = handler;
        this.attenuationType = ISound.AttenuationType.NONE;
    }

    public void stop() {
        this.isDone = true;
    }

    @Override
    public void update() {
        if (this.isDonePlaying()) {
            this.volume = 0.0F;
        } else {
            Vec3d source = this.handler.getSoundSource();
            this.xPosF = (float) source.x;
            this.yPosF = (float) source.y;
            this.zPosF = (float) source.z;
            this.volume = this.handler.getVolumeScale() * MathX.clamp(1.0F - this.handler.getDistance() / 40.0F);
        }
    }

    @Override
    public boolean isDonePlaying() {
        return this.isDone;
    }

    @Override
    public String toString() {
        return this.positionedSoundLocation.toString();
    }

    public static class Scheduled extends BreathSound {
        protected int timer;
        protected boolean isTimeout;

        public Scheduled(
                BreathSoundHandler handler,
                BiFunction<DragonBreath, DragonLifeStage, SoundEvent> sound,
                int duration
        ) {
            super(handler, sound);
            this.timer = duration;
        }

        @Override
        public void update() {
            super.update();
            if (this.isTimeout) {
                this.volume *= 0.5F;
            } else if (--this.timer < 0) {
                this.isTimeout = true;
                this.handler.softStop();
            }
        }
    }

    public static class Repeated extends BreathSound {
        public Repeated(BreathSoundHandler handler, BiFunction<DragonBreath, DragonLifeStage, SoundEvent> sound) {
            super(handler, sound);
            this.repeat = true;
            this.repeatDelay = 0;
        }
    }
}
