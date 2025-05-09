package net.dragonmounts.util.math;

import static net.minecraft.util.math.MathHelper.clamp;

/**
 * Simple class to interpolate a float value that is smoothed between its
 * current and previous tick value.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class LinearInterpolation {
    protected float current;
    protected float previous;

    public LinearInterpolation(float init) {
        this.current = this.previous = init;
    }

    public final float get() {
        return this.current;
    }

    public final float get(float delta) {
        if (delta <= 0.0F) return this.previous;
        if (delta >= 1.0F) return this.current;
        return this.previous + delta * (this.current - this.previous);
    }

    public final void sync() {
        this.previous = this.current;
    }

    public void set(float value) {
        this.sync();
        this.current = value;
    }

    public final void add(float value) {
        this.set(this.current + value);
    }


    public static class Clamped extends LinearInterpolation {
        public final float min;
        public final float max;

        public Clamped(float init, float min, float max) {
            super(clamp(init, min, max));
            this.min = min;
            this.max = max;
        }

        @Override
        public void set(float value) {
            super.set(clamp(value, this.min, this.max));
        }
    }
}
