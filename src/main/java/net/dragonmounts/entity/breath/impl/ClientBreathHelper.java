package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.DragonBreathHelper;
import net.dragonmounts.entity.breath.sound.SoundEffectBreathWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.dragonmounts.util.math.MathX.interpolateVec;

public class ClientBreathHelper extends DragonBreathHelper<ClientDragonEntity> {
    private final SoundEffectBreathWeapon soundEffectBreathWeapon = new SoundEffectBreathWeapon();
    protected Vec3d previousOrigin;
    protected Vec3d previousDirection;
    protected int previousTickCount;

    public ClientBreathHelper(ClientDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void update() {
        ++this.tickCounter;
        ClientDragonEntity dragon = this.dragon;
        if (this.breath == null) {
            this.currentBreathState = BreathState.IDLE;
            return;
        }
        this.updateBreathState(dragon.isUsingBreathWeapon());
        SoundEffectBreathWeapon.SoundContext context = new SoundEffectBreathWeapon.SoundContext(); // TODO: reuse
        context.dragonHeadLocation = dragon.getThroatPosition();
        context.relativeVolume = dragon.getScale();
        context.lifeStage = dragon.lifeStageHelper.getLifeStage();
        context.breath = this.breath;
        if (this.currentBreathState == BreathState.SUSTAIN) {
            context.breathingState = SoundEffectBreathWeapon.SoundContext.State.BREATHING;
            /*
             * Created by TGG on 21/06/2015.
             * Used to spawn breath particles on the client side (in future: will be different for different breath weapons)
             * Spawn breath particles for this tick.  If the beam endpoints have moved, interpolate between them, unless
             * the beam stopped for a while (tickCount skipped one or more tick)
             */
            World level = dragon.world;
            BreathPower power = context.lifeStage.power;
            Vec3d origin = context.dragonHeadLocation;
            Vec3d direction = dragon.getLookVec();
            if (this.tickCounter != previousTickCount + 1) {
                previousDirection = direction;
                previousOrigin = origin;
            } else {
                if (previousDirection == null) previousDirection = direction;
                if (previousOrigin == null) previousOrigin = origin;
            }
            final int PARTICLES_PER_TICK = 4;
            for (int i = 0; i < PARTICLES_PER_TICK; ++i) {
                float partialTickHeadStart = i / (float) PARTICLES_PER_TICK;
                this.breath.spawnClientBreath(
                        level,
                        interpolateVec(previousOrigin, origin, partialTickHeadStart),
                        interpolateVec(previousDirection, direction, partialTickHeadStart),
                        power,
                        partialTickHeadStart
                );
            }
            previousDirection = direction;
            previousOrigin = origin;
            previousTickCount = this.tickCounter;
        }
        this.soundEffectBreathWeapon.performTick(Minecraft.getMinecraft().player, dragon, context);
    }
}
