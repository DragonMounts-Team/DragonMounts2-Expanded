/*
 ** 2012 March 18
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.goal;

import net.dragonmounts.entity.ServerDragonEntity;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Abstract "AI" for player-controlled movements.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ControlledByPlayerGoal extends EntityAIBase {
    public final ServerDragonEntity dragon;

    public ControlledByPlayerGoal(ServerDragonEntity dragon) {
        this.dragon = dragon;
        setMutexBits(0xffffffff);
    }

    @Override
    public boolean shouldExecute() {
        return dragon.getControllingPlayer() != null;
    }

    @Override
    public void startExecuting() {
        dragon.getNavigator().clearPath();
        dragon.getAISit().setSitting(false);
        dragon.resetInLove();
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void resetTask() {
        this.dragon.setUsingBreathWeapon(false);
    }
}