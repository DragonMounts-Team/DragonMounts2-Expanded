package net.dragonmounts.client.breath.impl;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.breath.BreathSound;
import net.dragonmounts.client.breath.BreathSoundHandler;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.BreathState;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.DragonBreathHelper;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.dragonmounts.util.math.MathX.interpolateVec;

public class ClientBreathHelper extends DragonBreathHelper<ClientDragonEntity> {
    private final BreathSoundHandler sound = new BreathSoundHandler();
    private Vec3d previousOrigin;
    private Vec3d previousDirection;
    private int previousTickCount;

    public ClientBreathHelper(ClientDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void update() {
        ++this.tickCounter;
        ClientDragonEntity dragon = this.dragon;
        if (this.breath == null) {
            this.currentBreathState = BreathState.IDLE;
            this.onBreathStop();
            return;
        }
        Vec3d headLocation = dragon.getThroatPosition();
        DragonLifeStage stage = dragon.lifeStageHelper.getLifeStage();
        this.sound.update(
                this.breath,
                headLocation,
                stage,
                dragon.lifeStageHelper.getScale()
        );
        this.updateBreathState(dragon.isUsingBreathWeapon());
        if (this.currentBreathState == BreathState.SUSTAIN) {
            /*
             * Created by TGG on 21/06/2015.
             * Used to spawn breath particles on the client side (in future: will be different for different breath weapons)
             * Spawn breath particles for this tick.  If the beam endpoints have moved, interpolate between them, unless
             * the beam stopped for a while (tickCount skipped one or more tick)
             */
            World level = dragon.world;
            BreathPower power = stage.power;
            Vec3d direction = dragon.getLookVec();
            if (this.tickCounter != previousTickCount + 1) {
                previousDirection = direction;
                previousOrigin = headLocation;
            } else {
                if (previousDirection == null) previousDirection = direction;
                if (previousOrigin == null) previousOrigin = headLocation;
            }
            final int PARTICLES_PER_TICK = 4;
            for (int i = 0; i < PARTICLES_PER_TICK; ++i) {
                float partialTickHeadStart = i / (float) PARTICLES_PER_TICK;
                this.breath.spawnClientBreath(
                        level,
                        interpolateVec(previousOrigin, headLocation, partialTickHeadStart),
                        interpolateVec(previousDirection, direction, partialTickHeadStart),
                        power,
                        partialTickHeadStart
                );
            }
            previousDirection = direction;
            previousOrigin = headLocation;
            previousTickCount = this.tickCounter;
        }
    }

    @Override
    protected void onBreathStart() {
        this.sound.clear();
        this.sound.play(handler ->
                new BreathSound.Scheduled(handler, DragonBreath::getStartSound, 20)
        );
        this.sound.schedule(handler ->
                new BreathSound.Repeated(handler, DragonBreath::getLoopSound)
        );
    }

    @Override
    protected void onBreathStop() {
        this.sound.clear();
        this.sound.play(handler ->
                new BreathSound.Scheduled(handler, DragonBreath::getStopSound, 60)
        );
    }
}
