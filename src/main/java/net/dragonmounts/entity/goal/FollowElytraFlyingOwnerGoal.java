/*
 ** 2016 April 26
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.goal;

import net.dragonmounts.entity.ServerDragonEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class FollowElytraFlyingOwnerGoal extends EntityAIBase {
    public final ServerDragonEntity dragon;
    protected EntityLivingBase owner;

    public FollowElytraFlyingOwnerGoal(ServerDragonEntity dragon) {
        this.dragon = dragon;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity dragon = this.dragon;
        if (!dragon.canFly())
            return false;

        // don't follow if sitting
        if (dragon.isSitting())
            return false;


        if (dragon.getLeashed())
            return false;

        this.owner = dragon.getOwner();

        // don't follow if ownerless 
        if (this.owner == null) return false;

        if (dragon.isRiding() || dragon.isPassenger(this.owner)) return false;

        // follow only if the owner is using an Elytra
        return this.owner.isElytraFlying();
    }

    @Override
    public void updateTask() {
        ServerDragonEntity dragon = this.dragon;
        EntityLivingBase owner = this.owner;
        // liffoff
        if (!dragon.isFlying()) dragon.liftOff();

        // mount owner if close enough, otherwise move to owner
        if (dragon.getDistance(owner) <= dragon.width || dragon.getDistance(owner) <= dragon.height || (owner.isSneaking() && dragon.isFlying()))
            owner.startRiding(dragon);

        dragon.getNavigator().tryMoveToXYZ(owner.posX, owner.posY, owner.posZ, 1);
        dragon.setBoosting(dragon.getDistance(owner) > 18);
    }
}
