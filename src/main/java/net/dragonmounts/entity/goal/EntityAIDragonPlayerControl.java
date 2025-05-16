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
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.math.MathX;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.Vec3d;

/**
 * Abstract "AI" for player-controlled movements.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityAIDragonPlayerControl extends EntityAIBase {
    public final ServerDragonEntity dragon;

    public EntityAIDragonPlayerControl(ServerDragonEntity dragon) {
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
    public void updateTask() {
        ServerDragonEntity dragon = this.dragon;
        EntityPlayer rider = dragon.getControllingPlayer();
        assert rider != null;
        Vec3d wp = rider.getLookVec();

        double x = dragon.posX;
        double y = dragon.posY;
        double z = dragon.posZ;

        if (dragon.getVariant().type == DragonTypes.WATER && rider.isInWater()) {
            EntityUtil.addOrResetEffect(rider, MobEffects.WATER_BREATHING, 200, 0, true, true, 21);
        }

        // if we're breathing at a target, look at it
        if (dragon.isUsingBreathWeapon() && dragon.breathHelper.canBreathe()) {
            Vec3d endOfLook = dragon.getPositionVector().add(
                    wp.x,
                    wp.y + dragon.getEyeHeight(),
                    wp.z
            );
            dragon.getLookHelper().setLookPosition(endOfLook.x, endOfLook.y, endOfLook.z,
                    90, 120);
            dragon.updateIntendedRideRotation(rider);
        } else if (dragon.followYaw() && dragon.moveStrafing == 0) {
            dragon.updateIntendedRideRotation(rider);
        }
        // control direction with movement keys
        if (rider.moveStrafing != 0 || rider.moveForward != 0) {
            if (rider.moveForward < 0) {
                wp = wp.rotateYaw(MathX.PI_F);
            } else if (rider.moveStrafing > 0) {
                wp = wp.rotateYaw(MathX.PI_F * 0.5f);
            } else if (rider.moveStrafing < 0) {
                wp = wp.rotateYaw(MathX.PI_F * -0.5f);
            }

            x += wp.x * 20;
            if (!dragon.isYLocked()) y += wp.y * 20;
            z += wp.z * 20;
        }
        // lift off from a jump
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

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void resetTask() {
        this.dragon.setUsingBreathWeapon(false);
    }
}