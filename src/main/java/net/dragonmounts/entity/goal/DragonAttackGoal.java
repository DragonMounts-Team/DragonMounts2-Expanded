package net.dragonmounts.entity.goal;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.world.EnumDifficulty;

/**
 * @see net.minecraft.entity.ai.EntityAIAttackMelee
 */
public class DragonAttackGoal extends EntityAIBase {
    public static boolean shouldIgnoreTarget(EntityPlayer player) {
        return player.isSpectator() || player.isCreative() || player.world.getDifficulty() == EnumDifficulty.PEACEFUL;
    }

    public final ServerDragonEntity dragon;
    public final double speedModifier;
    protected int attackTick;
    private Path path;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int lastCheck = -20;

    public DragonAttackGoal(ServerDragonEntity dragon, double speed) {
        this.dragon = dragon;
        this.speedModifier = speed;
        this.setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity dragon = this.dragon;
        if (this.lastCheck + 20 > dragon.ticksExisted) return false;
        this.lastCheck = dragon.ticksExisted;
        EntityLivingBase target = dragon.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return false;
        if (target instanceof EntityPlayer && shouldIgnoreTarget((EntityPlayer) target)) return false;
        this.path = dragon.getNavigator().getPathToEntityLiving(target);
        return this.path != null || this.getAttackReachSqr(target) >= dragon.getDistanceSq(
                target.posX,
                target.getEntityBoundingBox().minY,
                target.posZ
        );
    }

    @Override
    public boolean shouldContinueExecuting() {
        TameableDragonEntity dragon = this.dragon;
        EntityLivingBase target = dragon.getAttackTarget();
        if (target instanceof EntityPlayer && shouldIgnoreTarget((EntityPlayer) target)) return false;
        return target != null && target.isEntityAlive() && dragon.getControllingPassenger() == null;
    }

    @Override
    public void startExecuting() {
        this.dragon.getNavigator().setPath(this.path, this.speedModifier);
        this.delayCounter = 0;
        this.attackTick = 0;
    }

    @Override
    public void resetTask() {
        EntityLivingBase target = this.dragon.getAttackTarget();
        if (target instanceof EntityPlayer && shouldIgnoreTarget((EntityPlayer) target)) {
            this.dragon.setAttackTarget(null);
        }
        this.dragon.setUsingBreathWeapon(false);
        this.dragon.getNavigator().clearPath();
        this.targetX = this.targetY = this.targetZ = 0;
    }

    private static int getPoints(EntityLivingBase target) {
        return target instanceof EntityAnimal ? 90 : 40;
    }

    @Override
    public void updateTask() {
        TameableDragonEntity dragon = this.dragon;
        EntityLivingBase target = dragon.getAttackTarget();
        //noinspection DataFlowIssue
        dragon.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        double targetDistSq = dragon.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
        --this.delayCounter;
        if (this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || target.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.dragon.getRNG().nextFloat() < 0.05F)) {
            this.targetX = target.posX;
            this.targetY = target.getEntityBoundingBox().minY;
            this.targetZ = target.posZ;
            this.delayCounter = 4 + dragon.getRNG().nextInt(7);

            if (targetDistSq > 1024.0D) {
                this.delayCounter += 10;
            } else if (targetDistSq > 256.0D) {
                this.delayCounter += 5;
            }
            if (!dragon.getNavigator().tryMoveToEntityLiving(target, this.speedModifier)) {
                this.delayCounter += 15;
            }
        }
        if (--this.attackTick < 0) {
            this.checkAndPerformAttack(target, targetDistSq);
        }
    }

    protected void checkAndPerformAttack(EntityLivingBase target, double targetDistSq) {
        TameableDragonEntity dragon = this.dragon;
        boolean shouldUseMelee = targetDistSq <= this.getAttackReachSqr(target);
        //shouldUseRange = (isWithinBreathRange(targetDistSq) || dragon.isFlying()) && dragon.getEntitySenses().canSee(target) && !(target instanceof EntityAnimal) && dragon.isFlying();// && lookingAtTarget(target);

        if (shouldUseMelee) {
            this.attackTick = 20;
            dragon.world.setEntityState(dragon, TameableDragonEntity.DO_ATTACK);
            float health = target.getHealth();
            dragon.attackEntityAsMob(target);
            float delta = target.getHealth() - health;
            if (delta > 0.0F) {
                dragon.setHunger(dragon.getHunger() + 1 + (int) (getPoints(target) * delta / target.getMaxHealth()));
            }
        }/* else if (shouldUseRange) {
            this.attackTick = 20;
            dragon.setUsingBreathWeapon(target.isEntityAlive());
            dragon.getLookHelper().setLookPositionWithEntity(target, 120, 90);
        }*/
    }

    protected double getAttackReachSqr(EntityLivingBase target) {
        return this.dragon.width * 2.0F * this.dragon.width * 2.0F + target.width;
    }
}