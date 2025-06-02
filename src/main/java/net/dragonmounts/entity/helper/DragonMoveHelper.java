package net.dragonmounts.entity.helper;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED;

public class DragonMoveHelper extends EntityMoveHelper {
    private final TameableDragonEntity dragon;

    public DragonMoveHelper(TameableDragonEntity dragon) {
        super(dragon);
        this.dragon = dragon;
        this.speed = 0.9D;
    }

    @Override
    public void onUpdateMoveHelper() {
        TameableDragonEntity dragon = this.dragon;
        // original movement behavior if the entity isn't flying
        if (dragon.isFlying()) {
            double posX = this.posX, posY = this.posY, posZ = this.posZ;
            // get Euclidean distance to target
            double dist = dragon.getDistanceSq(posX, posY, posZ);
            // move towards target if it's far away enough
            if (dist > dragon.width * dragon.width) {
                double flySpeed = dragon.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue();
                if (dragon.boosting()) {
                    flySpeed = flySpeed * 1.5 + 0.5;
                }
                // get direction vector by subtracting the current position from the target position and normalizing
                Vec3d dir = new Vec3d(posX - dragon.posX, posY - dragon.posY, posZ - dragon.posZ).normalize();
                // update velocity to approach target
                dragon.motionX = dir.x * flySpeed;
                dragon.motionY = dir.y * flySpeed;
                dragon.motionZ = dir.z * flySpeed;
            } else if (dragon.getControllingPlayer() != null) {
                // just slow down and hover at current location
                dragon.motionX *= 0.8;
                dragon.motionY *= 0.8;
                dragon.motionZ *= 0.8;

                dragon.motionY += MathHelper.sin(dragon.ticksExisted / 5.0F) * 0.03F;
            }

            // face entity towards target
            if (dist > 1E-10) {
                float YAW_SPEED = dragon.getControllingPlayer() != null ? 5 : 15;
                float newYaw = (float) Math.toDegrees(MathHelper.atan2(dragon.posX - posX, posZ - dragon.posZ));
                dragon.rotationYaw = this.limitAngle(dragon.rotationYaw, newYaw, YAW_SPEED);
                entity.setAIMoveSpeed((float) (speed * entity.getEntityAttribute(MOVEMENT_SPEED).getAttributeValue()));
            }

            // apply movement
            dragon.move(MoverType.SELF, dragon.motionX, dragon.motionY, dragon.motionZ);
        } else {
            super.onUpdateMoveHelper();
        }
    }
}
