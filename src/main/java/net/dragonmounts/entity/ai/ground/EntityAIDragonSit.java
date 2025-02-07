package net.dragonmounts.entity.ai.ground;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAISit;

public class EntityAIDragonSit extends EntityAISit {
    public TameableDragonEntity dragon;

    /**
     * If the Entitydragon is sitting.
     */
    private boolean isSitting;

    public EntityAIDragonSit(TameableDragonEntity dragon) {
        super(dragon);
        this.dragon = dragon;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if (!this.isSitting ||
                !this.dragon.isTamed() ||
                this.dragon.isInWater() ||
                !this.dragon.onGround ||
                this.dragon.getControllingPlayer() != null
        ) return false;
        EntityLivingBase owner = this.dragon.getOwner();
        return owner == null || (this.dragon.getDistanceSq(owner) >= 144.0D || owner.getRevengeTarget() == null);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.dragon.getNavigator().clearPath();
        this.dragon.setSitting(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void resetTask() {
        this.dragon.setSitting(false);
    }

    /**
     * Sets the sitting flag.
     */
    @Override
    public void setSitting(boolean sitting) {
        this.isSitting = sitting;
    }
}