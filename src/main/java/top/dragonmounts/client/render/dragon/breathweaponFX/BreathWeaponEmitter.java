package top.dragonmounts.client.render.dragon.breathweaponFX;

import top.dragonmounts.objects.entity.entitytameabledragon.breath.BreathNode;
import top.dragonmounts.objects.entity.entitytameabledragon.breeds.DragonBreed;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static top.dragonmounts.util.math.MathX.interpolateVec;

/**
 * Created by TGG on 21/06/2015.
 * Used to spawn breath particles on the client side (in future: will be different for different breath weapons)
 * Usage:
 * Each tick:
 * (1) setBeamEndpoints() to set the current beam origin and destination
 * (2) spawnBreathParticles() to spawn the particles()
 */
public class BreathWeaponEmitter {
    protected Vec3d previousOrigin;
    protected Vec3d previousDirection;
    protected int previousTickCount;

    /**
     * Spawn breath particles for this tick.  If the beam endpoints have moved, interpolate between them, unless
     * the beam stopped for a while (tickCount skipped one or more tick)
     *
     * @param power the strength of the beam
     */
    public void spawnBreathParticles(World world, Vec3d origin, Vec3d direction, BreathNode.Power power, int tickCount, DragonBreed breed) {
        if (tickCount != previousTickCount + 1) {
            previousDirection = direction;
            previousOrigin = origin;
        } else {
            if (previousDirection == null) previousDirection = direction;
            if (previousOrigin == null) previousOrigin = origin;
        }
        final int PARTICLES_PER_TICK = 4;
        for (int i = 0; i < PARTICLES_PER_TICK; ++i) {
            float partialTickHeadStart = i / (float) PARTICLES_PER_TICK;
            breed.spawnClientNodeEntity(
                    world,
                    interpolateVec(previousOrigin, origin, partialTickHeadStart),
                    interpolateVec(previousDirection, direction, partialTickHeadStart),
                    power,
                    partialTickHeadStart
            );
        }
        previousDirection = direction;
        previousOrigin = origin;
        previousTickCount = tickCount;
    }
}
