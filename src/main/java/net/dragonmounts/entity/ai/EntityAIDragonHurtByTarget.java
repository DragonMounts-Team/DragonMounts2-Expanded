package net.dragonmounts.entity.ai;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIDragonHurtByTarget extends EntityAITarget {
    /**
     * Store the previous revengeTimer value
     */
    private int revengeTimerOld;
    private final TameableDragonEntity dragon;

    public EntityAIDragonHurtByTarget(TameableDragonEntity dragon) {
        super(dragon, true, true);
        this.dragon = dragon;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        int i = this.dragon.getRevengeTimer();
        EntityLivingBase target = this.dragon.getRevengeTarget();
        return i != this.revengeTimerOld && target != null && this.isSuitableTarget(target, false) && this.dragon.shouldAttackEntity(target, dragon.getOwner());
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        TameableDragonEntity dragon = this.dragon;
        EntityLivingBase owner = dragon.getOwner();
        EntityLivingBase target = this.target = dragon.getRevengeTarget();
        dragon.setAttackTarget(target);
        this.revengeTimerOld = dragon.getRevengeTimer();
        this.unseenMemoryTicks = 300;
        super.startExecuting();
    }
}