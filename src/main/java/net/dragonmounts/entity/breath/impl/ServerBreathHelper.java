package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedArea;
import net.dragonmounts.entity.breath.BreathState;
import net.dragonmounts.entity.breath.DragonBreathHelper;

public class ServerBreathHelper extends DragonBreathHelper<ServerDragonEntity> {
    public final BreathAffectedArea breathAffectedArea = new BreathAffectedArea();
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
            this.breathAffectedArea.continueBreathing(
                    dragon.world,
                    this.getBreathSpawnPosition(),
                    dragon.getLookVec(),
                    dragon.lifeStageHelper.getLifeStage().power
            );
        }
        this.breathAffectedArea.updateTick(dragon.world, this.breath);
    }
}
