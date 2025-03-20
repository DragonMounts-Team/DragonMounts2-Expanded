package net.dragonmounts.entity.goal.target;

import net.dragonmounts.entity.ServerDragonEntity;
import net.minecraft.entity.ai.EntityAIBase;

public class ControlledTargetGoal extends EntityAIBase {
    public final ServerDragonEntity dragon;

    public ControlledTargetGoal(ServerDragonEntity dragon) {
        this.dragon = dragon;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        return this.dragon.getControllingPlayer() != null;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }
}
