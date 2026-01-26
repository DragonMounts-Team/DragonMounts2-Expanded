package net.dragonmounts.util.math;

import static net.minecraft.util.math.MathHelper.clamp;

/**
 * Simple class to interpolate a float value that is smoothed between its
 * current and previous tick value.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public final class InterpolatedFloat {
    public final float min;
    public final float max;
    private float previous;
    private float current;

    public InterpolatedFloat(float init) {
        this(init, 0.0F, 1.0F);
    }

    public InterpolatedFloat(float init, float min, float max) {
        this.previous = this.current = clamp(init, this.min = min, this.max = max);
    }

    public float get() {
        return this.current;
    }

    public float get(float delta) {
        if (delta <= 0.0F) return this.previous;
        if (delta >= 1.0F) return this.current;
        return this.previous + delta * (this.current - this.previous);
    }

    public void sync() {
        this.previous = this.current;
    }

    public void set(float value) {
        this.sync();
        this.current = clamp(value, this.min, this.max);
    }

    public void add(float value) {
        this.set(this.current + value);
    }
}
