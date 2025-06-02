package net.dragonmounts.entity.breath;

public interface IBreathEffectHandler {
    boolean decayEffectTick();

    boolean isUnaffected();
}
