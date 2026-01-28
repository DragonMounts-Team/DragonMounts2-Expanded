package net.dragonmounts.entity.goal;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAISit;

public class DragonSitGoal extends EntityAISit {
    public final TameableDragonEntity dragon;
    private boolean isSitting;

    public DragonSitGoal(TameableDragonEntity dragon) {
        super(dragon);
        this.dragon = dragon;
    }

    @Override
    public boolean shouldExecute() {
        TameableDragonEntity dragon = this.dragon;
        if (!this.isSitting ||
                !dragon.isTamed() ||
                dragon.isInWater() ||
                !dragon.onGround
        ) return false;
        EntityLivingBase owner = dragon.getOwner();
        return owner == null || dragon.getDistanceSq(owner) >= 256.0 || owner.getRevengeTarget() == null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.isSitting;
    }

    @Override
    public void startExecuting() {
        this.dragon.getNavigator().clearPath();
        this.dragon.setSitting(true);
        this.dragon.setAttackTarget(null);
    }

    @Override
    public void resetTask() {
        this.dragon.setSitting(false);
    }

    @Override
    public void setSitting(boolean sitting) {
        this.isSitting = sitting;
    }
}