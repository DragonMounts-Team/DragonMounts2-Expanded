package net.dragonmounts.entity.goal;

import com.google.common.base.Predicate;
import net.dragonmounts.entity.ServerDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;

import javax.annotation.Nullable;
import java.util.List;

public class DragonFollowParentGoal extends EntityAIBase implements Predicate<ServerDragonEntity> {
    public final ServerDragonEntity dragon;
    /// assume any adult dragon nearby is a parent even if it is not
    private ServerDragonEntity parent;
    private Entity owner;
    public final double speedModifier;
    private int pathValidity;

    public DragonFollowParentGoal(ServerDragonEntity dragon, double speed) {
        this.dragon = dragon;
        this.speedModifier = speed;
        this.setMutexBits(0b01);
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity dragon = this.dragon;
        this.owner = dragon.getOwner();
        List<ServerDragonEntity> list = dragon.world.getEntitiesWithinAABB(
                ServerDragonEntity.class,
                dragon.getEntityBoundingBox().grow(8.0D, 4.0D, 8.0D),
                this
        );
        ServerDragonEntity parent = null;
        double min = Double.MAX_VALUE;
        for (ServerDragonEntity candidate : list) {
            if (candidate != dragon) {
                double dist = dragon.getDistanceSq(candidate);
                if (dist <= min) {
                    parent = candidate;
                    min = dist;
                }
            }
        }
        if (parent == null || min < 16.0) return false;
        this.parent = parent;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (!this.parent.isEntityAlive()) return false;
        double dist = this.dragon.getDistanceSq(this.parent);
        return dist >= 16.0 && dist <= 400.0;
    }

    @Override
    public void startExecuting() {
        this.pathValidity = 0;
    }

    @Override
    public void resetTask() {
        this.dragon.getNavigator().clearPath();
        this.parent = null;
    }

    @Override
    public void updateTask() {
        if (--this.pathValidity <= 0) {
            this.pathValidity = 10;
            this.dragon.getNavigator().tryMoveToEntityLiving(this.parent, this.speedModifier);
        }
    }

    @Override
    public boolean apply(@Nullable ServerDragonEntity other) {
        return other != null && other.getControllingPassenger() == null && (
                this.owner == null || this.owner.equals(other.getOwner())
        );
    }
}