package net.dragonmounts.entity.goal;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

import static net.dragonmounts.init.DragonFoods.isDragonFood;

/**
 * @see net.minecraft.entity.ai.EntityAITempt
 */
public class DragonTemptGoal extends EntityAIBase {
    public final TameableDragonEntity dragon;
    private final double speed;
    private EntityPlayer target;
    private double pitch;
    private double yaw;
    private int delay;

    public DragonTemptGoal(TameableDragonEntity dragon, double speed) {
        this.dragon = dragon;
        this.speed = speed;
        this.setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        if (this.delay > 0) {
            --this.delay;
            return false;
        }
        TameableDragonEntity dragon = this.dragon;
        this.target = dragon.world.getClosestPlayer(dragon.posX, dragon.posY, dragon.posZ, 12.0, DragonTemptGoal::shouldFollow);
        if (this.target == null) {
            this.delay = 10;
            return false;
        }
        return true;
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.dragon.getNavigator().clearPath();
        this.delay = 20;
    }

    @Override
    public void updateTask() {
        TameableDragonEntity dragon = this.dragon;
        dragon.getLookHelper().setLookPositionWithEntity(this.target, dragon.getHorizontalFaceSpeed() + 20, dragon.getVerticalFaceSpeed());
        if (dragon.getDistanceSq(this.target) < 6.25D) {
            dragon.getNavigator().clearPath();
        } else {
            dragon.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
        }
    }

    public static boolean shouldFollow(Entity target) {
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            return !player.isSpectator() && (
                    isDragonFood(player.getHeldItemMainhand()) || isDragonFood(player.getHeldItemOffhand())
            );
        }
        return false;
    }
}
