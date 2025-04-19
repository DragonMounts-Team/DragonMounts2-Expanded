package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.breath.BreathState;
import net.dragonmounts.entity.breath.DragonBreathHelper;
import net.minecraft.util.math.Vec3d;

public class ServerBreathHelper extends DragonBreathHelper<ServerDragonEntity> {
    public ServerBreathHelper(ServerDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void update() {
        ++tickCounter;
        if (this.breath == null) return;
        ServerDragonEntity dragon = this.dragon;
        this.updateBreathState(dragon.isUsingBreathWeapon());
        if (BreathState.SUSTAIN == this.currentBreathState) {
            Vec3d origin = dragon.getThroatPosition();
            Vec3d lookDirection = dragon.getLookVec();
            Vec3d endOfLook = origin.add(lookDirection.x, lookDirection.y, lookDirection.z);
            this.breathAffectedArea.continueBreathing(dragon.world, origin, endOfLook, dragon.lifeStageHelper.getLifeStage().power);
        }
        this.breathAffectedArea.updateTick(dragon.world, this.breath);
    }
}
