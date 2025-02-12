package net.dragonmounts.entity.ai.ground;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAISit;

public class EntityAIDragonSit extends EntityAISit {
    public final TameableDragonEntity dragon;
    private boolean isSitting;

    public EntityAIDragonSit(TameableDragonEntity dragon) {
        super(dragon);
        this.dragon = dragon;
    }

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

    @Override
    public void startExecuting() {
        this.dragon.getNavigator().clearPath();
        this.dragon.setSitting(true);
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