/*
 ** 2012 August 26
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.ai;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;

import java.util.List;

/**
 * Derivative EntityAIMate class to deal with some special values that can't be
 * applied with an extension thanks to the visibility.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityAIDragonMate extends EntityAIDragonBase {

    private TameableDragonEntity dragonMate;
    private int spawnBabyDelay = 0;
    private final double speed;

    public EntityAIDragonMate(TameableDragonEntity dragon, double speed) {
        super(dragon);
        this.speed = speed;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (dragon.isSitting() || !dragon.isInLove()) {
            return false;
        } else {
            dragonMate = getNearbyMate();
            return dragonMate != null && /*!*/dragonMate.isInLove();
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting() {
        return dragonMate.isEntityAlive() && dragonMate.isInLove() && spawnBabyDelay < 60;
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        dragonMate = null;
        spawnBabyDelay = 0;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        dragon.getLookHelper().setLookPositionWithEntity(dragonMate, 10.0F, (float) dragon.getVerticalFaceSpeed());
        dragon.getNavigator().tryMoveToEntityLiving(dragonMate, speed);

        ++spawnBabyDelay;
        if (dragon.getControllingPlayer() != null) {
            dragon.resetInLove();
        }

        if (spawnBabyDelay == 60) {
            spawnBaby();
        }
    }

    /**
     * Loops through nearby animals and finds another animal of the same type
     * that can be mated with. Returns the first valid mate found.
     */
    private TameableDragonEntity getNearbyMate() {
        double followRange = dragon.getNavigator().getPathSearchRange();
        List<TameableDragonEntity> nearbyDragons = world.getEntitiesWithinAABB(
                TameableDragonEntity.class,
                dragon.getEntityBoundingBox().grow(followRange, followRange, followRange)
        );

        for (TameableDragonEntity nearbyDragon : nearbyDragons) {
            if (dragon.canMateWith(nearbyDragon)) {
                return nearbyDragon;
            }
        }

        return null;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    private void spawnBaby() {
        TameableDragonEntity dragonBaby = (TameableDragonEntity) dragon.createChild(dragonMate);

        if (dragonBaby != null) {
            dragon.resetInLove();
            dragonMate.resetInLove();
            dragonBaby.setLocationAndAngles(dragon.posX, dragon.posY, dragon.posZ, 0, 0);
            dragonBaby.getLifeStageHelper().setLifeStage(DragonLifeStage.EGG);
            // i cant figure out on how to get the highest number on the breed point map on the baby and set the breed with it
            // (turn on debug to see breed points per dragon)
            // inherit breed sets the both breed point from parents
            dragonBaby.setVariant(dragonMate.getVariant().type.variants.draw(world.rand, null));
            world.spawnEntity(dragonBaby);
        }
    }
}
