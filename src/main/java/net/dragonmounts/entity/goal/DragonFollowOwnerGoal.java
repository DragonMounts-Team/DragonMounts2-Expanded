/*
 ** 2013 November 05
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.goal;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Modified EntityAIFollowOwner that has dynamic navigator
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * @see net.minecraft.entity.ai.EntityAIFollowOwner
 */
public class DragonFollowOwnerGoal extends EntityAIBase {
    public final ServerDragonEntity dragon;
    private EntityLivingBase owner;
    private final double speed;
    private final float stopDistance;
    private final float squaredStart;
    private float squaredStop;
    private int pathValidity;

    public DragonFollowOwnerGoal(ServerDragonEntity dragon, double speed, float startDist, float stopDist) {
        this.dragon = dragon;
        this.speed = speed;
        this.squaredStart = startDist * startDist;
        this.stopDistance = stopDist;
        this.setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity dragon = this.dragon;
        if (!dragon.followOwner || dragon.isSitting()) return false;
        EntityLivingBase owner = dragon.getOwner();
        if (owner == null
                || EntityUtil.isSpectator(owner)
                || dragon.getDistanceSq(owner) < this.squaredStart
        ) return false;
        this.owner = owner;
        float dist = this.stopDistance * dragon.getAdjustedSize();
        this.squaredStop = dist > 1.0F ? dist * dist : 1.0F;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        ServerDragonEntity dragon = this.dragon;
        return dragon.followOwner
                && !dragon.getNavigator().noPath()
                && !EntityUtil.isSpectator(this.owner)
                && dragon.getDistanceSq(this.owner) > this.squaredStop;
    }

    @Override
    public void startExecuting() {
        this.pathValidity = 0;
    }

    @Override
    public void resetTask() {
        this.owner = null;
        this.dragon.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        ServerDragonEntity dragon = this.dragon;
        EntityLivingBase owner = this.owner;
        // look towards owner
        dragon.getLookHelper().setLookPositionWithEntity(owner, 120, 90);
        // update every 25 ticks only from here
        if (--this.pathValidity > 0) return;
        this.pathValidity = 25;
        PathNavigate navigator = dragon.getNavigator();
        // finish task if it can move to the owner
        if (navigator.tryMoveToEntityLiving(owner, this.speed)) return;
        // move only but don't teleport if leashed
        if (dragon.getLeashed() || dragon.isRiding() || dragon.getDistanceSq(owner) < 256.0) return;
        int x = MathHelper.floor(owner.posX) - 2;
        int y = MathHelper.floor(owner.getEntityBoundingBox().minY);
        int z = MathHelper.floor(owner.posZ) - 2;
        MutableBlockPosEx pos = new MutableBlockPosEx(0, 0, 0);
        for (int dX = 0; dX <= 4; ++dX) {
            for (int dZ = 0; dZ <= 4; ++dZ) {
                if ((dX < 1 || dZ < 1 || dX > 3 || dZ > 3) && this.isTeleportFriendlyBlock(dragon.world, pos.with(x + dX, y, z + dZ))) {
                    dragon.setLocationAndAngles((x + dX) + 0.5F, y, (z + dZ) + 0.5F, dragon.rotationYaw, dragon.rotationPitch);
                    navigator.clearPath();
                    return;
                }
            }
        }
    }

    protected boolean isTeleportFriendlyBlock(World level, MutableBlockPosEx pos) {
        IBlockState state = level.getBlockState(pos);
        ServerDragonEntity dragon = this.dragon;
        return state.getBlockFaceShape(level, pos, EnumFacing.DOWN) == BlockFaceShape.SOLID &&
                state.canEntitySpawn(dragon) &&
                !dragon.world.collidesWithAnyBlock(dragon.getEntityBoundingBox().grow(-1).offset(
                        pos.getX() - dragon.posX + 0.5,
                        pos.getY() - dragon.posY + 0.25,
                        pos.getZ() - dragon.posZ + 0.5
                ));
    }
}
