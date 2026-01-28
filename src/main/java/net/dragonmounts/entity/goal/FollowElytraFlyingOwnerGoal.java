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
 * @deprecated TODO merge into {@link DragonFollowOwnerGoal}
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class FollowElytraFlyingOwnerGoal extends EntityAIBase {
    public final ServerDragonEntity dragon;
    protected EntityLivingBase owner;

    public FollowElytraFlyingOwnerGoal(ServerDragonEntity dragon) {
        this.dragon = dragon;
        this.setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity dragon = this.dragon;
        // don't follow if leashed, sitting or already being ridden
        if (dragon.isSitting()
                || dragon.getLeashed()
                || !dragon.isSaddled()
                || dragon.isRiding()
                || dragon.getControllingPlayer() != null
        ) return false;
        EntityLivingBase owner = this.owner = dragon.getOwner();
        if (owner != null && owner.isElytraFlying()) {
            // don't follow if owner is too far away
            double range = dragon.getNavigator().getPathSearchRange();
            return dragon.getDistanceSq(owner) < range * range;
        }
        return false;
    }

    @Override
    public void updateTask() {
        ServerDragonEntity dragon = this.dragon;
        EntityLivingBase owner = this.owner;
        if (!dragon.isFlying()) {
            dragon.liftOff();
        }
        dragon.setBoosting(dragon.getDistanceSq(owner) > 25.0);
        dragon.getNavigator().tryMoveToXYZ(owner.posX, owner.posY, owner.posZ, 1);
    }
}
