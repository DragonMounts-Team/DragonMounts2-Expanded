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
        double deltaX = dragon.posX - dragon.prevPosX;
        double deltaY = dragon.posZ - dragon.prevPosZ;
        double dist = deltaX * deltaX + deltaY * deltaY;

        float maximumHeadBodyAngleDifference = 90;

        // rotate instantly if flying, sitting or moving
        if (dragon.isUsingBreathWeapon() || dragon.isFlying() || dragon.isSitting() || dist > 0.0001) {
            dragon.renderYawOffset = dragon.rotationYaw;
            dragon.rotationYawHead = MathX.clampedRotate(dragon.rotationYawHead, dragon.renderYawOffset, maximumHeadBodyAngleDifference);
            prevRotationYawHead = dragon.rotationYawHead;
            turnTicks = 0;
            return;
        }

        double changeInHeadYaw = Math.abs(dragon.rotationYawHead - prevRotationYawHead);
        if (changeInHeadYaw > 15) {
            turnTicks = 0;
            prevRotationYawHead = dragon.rotationYawHead;
        } else {
            turnTicks++;

            if (turnTicks > turnTicksLimit) {
                maximumHeadBodyAngleDifference = Math.max(1 - (float) (turnTicks - turnTicksLimit) / turnTicksLimit, 0) * 75;
            }
        }

        float rotationYawHead = dragon.getRotationYawHead();
        dragon.renderYawOffset = MathX.clampedRotate(dragon.renderYawOffset, rotationYawHead, maximumHeadBodyAngleDifference);
        dragon.rotationYaw = dragon.renderYawOffset;
    }
}