/*
 ** 2012 April 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.goal;

import com.google.common.base.Predicate;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class LookAtOtherGoal extends EntityAIBase implements Predicate<EntityLivingBase> {
    public final ServerDragonEntity dragon;
    protected final float lookDistance;
    protected final float probability;
    protected @Nullable EntityLivingBase lookAt;
    protected int lookTime;

    public LookAtOtherGoal(ServerDragonEntity dragon, float lookDistance, float probability) {
        this.dragon = dragon;
        this.lookDistance = lookDistance;
        this.probability = probability;
        this.setMutexBits(0b10);
    }

    @Override
    public boolean shouldExecute() {
        TameableDragonEntity dragon = this.dragon;
        if (!dragon.onGround || dragon.getRNG().nextFloat() >= this.probability) return false;
        this.lookAt = dragon.getAttackTarget();
        if (this.lookAt != null) return true;
        this.lookAt = EntityUtil.findNearestEntityWithinAABB(
                dragon,
                EntityLivingBase.class,
                dragon.getEntityBoundingBox().grow(this.lookDistance, dragon.height, this.lookDistance),
                this
        );
        return this.lookAt != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase target = this.lookAt;
        return target != null && target.isEntityAlive() && !(this.dragon.getDistanceSq(target) > this.lookDistance * this.lookDistance) && this.lookTime > 0;
    }

    @Override
    public void startExecuting() {
        this.lookTime = 40 + this.dragon.getRNG().nextInt(40);
        // watch the owner a little longer
        if (this.dragon.isOwner(this.lookAt)) {
            this.lookTime *= 3;
        }
    }

    @Override
    public void resetTask() {
        this.dragon.renderYawOffset = 0;
        this.lookAt = null;
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = this.lookAt;
        assert target != null;
        dragon.getLookHelper().setLookPosition(
                target.posX,
                target.posY + target.getEyeHeight(),
                target.posZ,
                10,
                dragon.getVerticalFaceSpeed()
        );
        --this.lookTime;
    }

    @Override
    public boolean apply(@Nullable EntityLivingBase target) {
        if (target == null) return false;
        Entity vehicle = target.getRidingEntity();
        while (vehicle != null) {
            if (this.dragon == vehicle) return false;
            vehicle = vehicle.getRidingEntity();
        }
        return !(target instanceof EntityPlayer) || !((EntityPlayer) target).isSpectator();
    }
}
