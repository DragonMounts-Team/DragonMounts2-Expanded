package net.dragonmounts.entity.ai;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.List;

public class EntityAIDragonFollowParent extends EntityAIBase {
    public final ServerDragonEntity dragon;
    // assume any adult dragon nearby is a parent even if its not
    TameableDragonEntity adultDragon;
    double moveSpeed;
    private int delayCounter;

    public EntityAIDragonFollowParent(ServerDragonEntity dragon, double speed) {
        this.dragon = dragon;
        this.moveSpeed = speed;
        setMutexBits(1);
    }

    public boolean shouldExecute() {
        if (!dragon.isChild()) {
            return false;
        } else {
            List<TameableDragonEntity> list = this.dragon.world.<TameableDragonEntity>getEntitiesWithinAABB(this.dragon.getClass(), this.dragon.getEntityBoundingBox().grow(8.0D, 4.0D, 8.0D));
            TameableDragonEntity adultDragon1 = null;
            double d0 = Double.MAX_VALUE;

            for (TameableDragonEntity adultDragon11 : list) {
                if (adultDragon11.getGrowingAge() >= 0) {
                    double d1 = this.dragon.getDistanceSq(adultDragon11);

                    if (d1 <= d0) {
                        d0 = d1;
                        adultDragon1 = adultDragon11;
                    }
                }
            }

            // play the follow owner method
            if (dragon.getOwner() != null && dragon.getOwner().isSneaking() && adultDragon != null) {
                return false;
            }

            if (adultDragon1 == null) {
                return false;
            } else if (d0 < 9.0D) {
                return false;
            } else if (!adultDragon1.isTamed() && adultDragon1.getControllingPlayer() != null) {
                return false;
            } else if (dragon.isSitting()) {
                return false;
            } else {
                this.adultDragon = adultDragon1;
                return true;
            }
        }
    }

    public boolean shouldContinueExecuting() {
        if (this.dragon.getGrowingAge() >= 0) {
            return false;
        } else if (!this.adultDragon.isEntityAlive()) {
            return false;
        } else {
            double d0 = this.dragon.getDistanceSq(this.adultDragon);
            return d0 >= 9.0D && d0 <= 256.0D;
        }
    }

    public void startExecuting() {
        this.delayCounter = 0;
    }

    public void resetTask() {
        this.adultDragon = null;
    }

    public void updateTask() {
        if (--this.delayCounter <= 0) {
            this.delayCounter = 10;
            this.dragon.getNavigator().tryMoveToEntityLiving(this.adultDragon, this.moveSpeed);
        }
    }
}