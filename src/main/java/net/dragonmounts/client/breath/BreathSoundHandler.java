package net.dragonmounts.client.breath;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.breath.DragonBreath;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public class BreathSoundHandler {
    private final ObjectArrayFIFOQueue<Function<BreathSoundHandler, BreathSound>> sounds = new ObjectArrayFIFOQueue<>();
    private DragonBreath breath;
    private Vec3d soundSource;
    private DragonLifeStage stage;
    private float volumeScale;
    private float distance;
    private BreathSound playing;

    public void update(DragonBreath breath, Vec3d pos, DragonLifeStage stage, float scale) {
        this.breath = breath;
        this.soundSource = pos;
        this.stage = stage;
        this.volumeScale = scale;
        this.distance = (float) Minecraft.getMinecraft().player.getDistance(pos.x, pos.y, pos.z);
        if (!this.sounds.isEmpty() && (
                this.playing == null || !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this.playing)
        )) {
            this.play(this.sounds.dequeue());
        }
    }

    public void softStop() {
        if (this.playing != null && this.playing.canRepeat()) {
            this.playing.stop();
        }
        this.playing = null;
    }

    public void clear() {
        this.sounds.clear();
        this.softStop();
    }

    public void play(Function<BreathSoundHandler, BreathSound> sound) {
        if (this.playing != null) {
            this.playing.stop();
        }
        Minecraft.getMinecraft().getSoundHandler().playSound(this.playing = sound.apply(this));
    }

    public void schedule(Function<BreathSoundHandler, BreathSound> sound) {
        this.sounds.enqueue(sound);
    }

    public DragonLifeStage getStage() {
        return this.stage;
    }

    public DragonBreath getBreath() {
        return this.breath;
    }

    public Vec3d getSoundSource() {
        return this.soundSource;
    }

    public float getVolumeScale() {
        return this.volumeScale;
    }

    public float getDistance() {
        return this.distance;
    }
}
