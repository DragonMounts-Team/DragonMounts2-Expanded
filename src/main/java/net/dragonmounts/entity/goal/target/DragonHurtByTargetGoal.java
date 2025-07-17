package net.dragonmounts.entity.goal.target;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

/**
 * @see net.minecraft.entity.ai.EntityAIHurtByTarget
 */
public class DragonHurtByTargetGoal extends EntityAITarget {
    private int revengeTimerOld;
    private final TameableDragonEntity dragon;

    public DragonHurtByTargetGoal(TameableDragonEntity dragon) {
        super(dragon, true, false);
        this.dragon = dragon;
        this.setMutexBits(1);
    }

    public boolean shouldExecute() {
        int time = this.dragon.getRevengeTimer();
        EntityLivingBase target = this.dragon.getRevengeTarget();
        return time != this.revengeTimerOld &&
                target != null &&
                this.isSuitableTarget(target, false);
    }

    public void startExecuting() {
        TameableDragonEntity dragon = this.dragon;
        dragon.setAttackTarget(this.target = dragon.getRevengeTarget());
        this.revengeTimerOld = dragon.getRevengeTimer();
        this.unseenMemoryTicks = 300;
        super.startExecuting();
    }
}