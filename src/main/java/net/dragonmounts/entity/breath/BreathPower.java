package net.dragonmounts.entity.breath;

public enum BreathPower {
    TINY(0.5F, 0.04F, 0.05F, 0.05F),
    SMALL(1.5F, 0.35F, 0.35F, 1.2F),
    MEDIUM(3.95F, 0.7F, 2.0F, 1.2F),
    LARGE(4.0F, 0.8F, 3.25F, 1.4F),
    HUGE(5.0F, 1.0F, 3.5F, 1.6F);
    public final float speed;
    public final float lifetime;
    public final float size;
    public final float intensity;

    BreathPower(
            float speed,
            float lifetime,
            float size,
            float intensity
    ) {
        this.speed = speed;
        this.lifetime = lifetime;
        this.size = size;
        this.intensity = intensity;
    }
}
