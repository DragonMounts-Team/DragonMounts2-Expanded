/*
 ** 2012 April 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.ai.ground;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.ai.EntityAIDragonBase;
import net.minecraft.entity.EntityLivingBase;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityAIDragonWatchLiving extends EntityAIDragonBase {
    private final float maxDist;
    private final float watchChance;
    private EntityLivingBase target;
    private int watchTicks;

    public EntityAIDragonWatchLiving(TameableDragonEntity dragon, float maxDist, float watchChance) {
        super(dragon);
        this.maxDist = maxDist;
        this.watchChance = watchChance;
        setMutexBits(2);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (this.random.nextFloat() >= this.watchChance) return false;
        TameableDragonEntity dragon = this.dragon;
        EntityLivingBase target = this.world.findNearestEntityWithinAABB(
                EntityLivingBase.class,
                dragon.getEntityBoundingBox().grow(this.maxDist, dragon.height, this.maxDist),
                dragon
        );
        // don't try to look at the rider when being ridden
        if (target == null || target == dragon.getControllingPlayer()) return false;
        this.target = target;
        // watch the owner a little longer
        if (dragon.isOwner(target)) {
            this.watchTicks *= 3;
        }
        return true;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting() {
        return target.isEntityAlive() && !(dragon.getDistanceSq(target) > maxDist * maxDist) && watchTicks > 2;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        watchTicks = 40 + random.nextInt(40);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        dragon.renderYawOffset = 0;
        target = null;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        dragon.getLookHelper().setLookPosition(
                target.posX,
                target.posY + target.getEyeHeight(),
                target.posZ,
                10,
                dragon.getVerticalFaceSpeed()
        );
        watchTicks--;
    }
}
