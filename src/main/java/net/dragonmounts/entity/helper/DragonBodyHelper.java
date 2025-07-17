/*
 ** 2012 March 20
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.helper;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.math.MathX;
import net.minecraft.entity.EntityBodyHelper;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonBodyHelper extends EntityBodyHelper {

    private final TameableDragonEntity dragon;
    private int turnTicks;
    private int turnTicksLimit = 20;
    private float prevRotationYawHead;

    public DragonBodyHelper(TameableDragonEntity dragon) {
        super(dragon);
        this.dragon = dragon;
    }

    @Override
    public void updateRenderAngles() {
        TameableDragonEntity dragon = this.dragon;
        float maximumHeadBodyAngleDifference = 90;

        // rotate instantly if flying, sitting or moving
        if (dragon.isUsingBreathWeapon() || dragon.isFlying() || dragon.isSitting() || EntityUtil.isMoving(dragon)) {
            dragon.renderYawOffset = dragon.rotationYaw;
            dragon.rotationYawHead = MathX.clampedRotate(dragon.rotationYawHead, dragon.renderYawOffset, maximumHeadBodyAngleDifference);
            prevRotationYawHead = dragon.rotationYawHead;
            turnTicks = 0;
            return;
        }

        if (Math.abs(dragon.rotationYawHead - prevRotationYawHead) > 15.0F) {
            turnTicks = 0;
            prevRotationYawHead = dragon.rotationYawHead;
        } else {
            turnTicks++;

            if (turnTicks > turnTicksLimit) {
                maximumHeadBodyAngleDifference = Math.max(1 - (float) (turnTicks - turnTicksLimit) / turnTicksLimit, 0) * 75;
            }
        }

        dragon.renderYawOffset = MathX.clampedRotate(dragon.renderYawOffset, dragon.getRotationYawHead(), maximumHeadBodyAngleDifference);
        dragon.rotationYaw = dragon.renderYawOffset;
    }

}