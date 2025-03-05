/*
 ** 2012 March 18
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.ai;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.math.MathX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.Vec3d;

/**
 * Abstract "AI" for player-controlled movements.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityAIDragonPlayerControl extends EntityAIDragonBase {
    protected EntityPlayer rider;

    public EntityAIDragonPlayerControl(TameableDragonEntity dragon) {
        super(dragon);
        setMutexBits(0xffffffff);
    }

    @Override
    public boolean shouldExecute() {
        rider = dragon.getControllingPlayer();
        return rider != null;
    }

    @Override
    public void startExecuting() {
        dragon.getNavigator().clearPath();
        dragon.getAISit().setSitting(false);
        dragon.resetInLove();
    }

    @Override
    public void updateTask() {
        TameableDragonEntity dragon = this.dragon;
        Vec3d wp = rider.getLook(1.0F);

        double x = dragon.posX;
        double y = dragon.posY;
        double z = dragon.posZ;

        if (dragon.getVariant().type == DragonTypes.WATER && this.rider.isInWater()) {
            EntityUtil.addOrResetEffect(this.rider, MobEffects.WATER_BREATHING, 200, 0, true, true, 21);
        }

        // if we're breathing at a target, look at it
        if (dragon.isUsingBreathWeapon() && dragon.breathHelper.canBreathe()) {
            Vec3d lookDirection = rider.getLook(1.0F);
            Vec3d endOfLook = dragon.getPositionVector().add(
                    lookDirection.x,
                    lookDirection.y + dragon.getEyeHeight(),
                    lookDirection.z
            );
            dragon.getLookHelper().setLookPosition(endOfLook.x, endOfLook.y, endOfLook.z,
                    90, 120);
            dragon.updateIntendedRideRotation(rider);
        } else if (dragon.followYaw() && dragon.moveStrafing == 0) {
            dragon.updateIntendedRideRotation(rider);
        }

        if (dragon.isServerWorld()) {
            // control direction with movement keys
            if (rider.moveStrafing != 0 || rider.moveForward != 0) {
                if (rider.moveForward < 0) {
                    wp = wp.rotateYaw(MathX.PI_F);
                } else if (rider.moveStrafing > 0) {
                    wp = wp.rotateYaw(MathX.PI_F * 0.5f);
                } else if (rider.moveStrafing < 0) {
                    wp = wp.rotateYaw(MathX.PI_F * -0.5f);
                }

                x += wp.x * 10;
                if (!dragon.isYLocked()) y += wp.y * 10;
                z += wp.z * 10;
            }

//         lift off from a jump
            if (rider.isJumping) {
                if (!dragon.isFlying()) {
                    dragon.liftOff();
                } else {
                    y += 10;
                }
            } else if (dragon.isGoingDown()) {
                y -= 10;
            }

            dragon.getMoveHelper().setMoveTo(x, y, z, 1.2);
        }
    }

    @Override
    public void resetTask() {
        this.dragon.setUsingBreathWeapon(false);
    }
}