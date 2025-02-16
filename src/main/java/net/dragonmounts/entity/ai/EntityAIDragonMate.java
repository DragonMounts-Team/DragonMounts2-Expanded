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
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Derivative EntityAIMate class to deal with some special values that can't be
 * applied with an extension thanks to the visibility.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * @see net.minecraft.entity.ai.EntityAIMate
 */
public class EntityAIDragonMate extends EntityAIDragonBase {
    private final double speed;
    private TameableDragonEntity dragonMate;
    private int spawnBabyDelay = 0;

    public EntityAIDragonMate(TameableDragonEntity dragon, double speed) {
        super(dragon);
        this.speed = speed;
        this.setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        if (dragon.isSitting() || !dragon.isInLove()) return false;
        dragonMate = getNearbyMate();
        return dragonMate != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return dragonMate.isEntityAlive() && dragonMate.isInLove() && spawnBabyDelay < 60;
    }

    @Override
    public void resetTask() {
        dragonMate = null;
        spawnBabyDelay = 0;
    }

    @Override
    public void updateTask() {
        dragon.getLookHelper().setLookPositionWithEntity(dragonMate, 10.0F, (float) dragon.getVerticalFaceSpeed());
        dragon.getNavigator().tryMoveToEntityLiving(dragonMate, speed);
        ++spawnBabyDelay;
        if (spawnBabyDelay == 60) {
            spawnBaby();
        }
    }

    private TameableDragonEntity getNearbyMate() {
        TameableDragonEntity self = this.dragon;
        TameableDragonEntity mate = null;
        double min = Double.MAX_VALUE;
        for (TameableDragonEntity candidate : world.getEntitiesWithinAABB(
                TameableDragonEntity.class,
                self.getEntityBoundingBox().grow(16.0, 16.0, 16.0),
                self::canMateWith
        )) {
            double distance = self.getDistance(candidate);
            if (distance < min) {
                min = distance;
                mate = candidate;
            }
        }
        return mate;
    }

    private void spawnBaby() {
        TameableDragonEntity self = this.dragon;
        TameableDragonEntity baby = self.createChild(dragonMate);
        if (baby != null) {
            World level = this.world;
            TameableDragonEntity mate = this.dragonMate;
            self.resetInLove();
            mate.resetInLove();
            baby.setLocationAndAngles(self.posX, self.posY, self.posZ, 0, 0);
            level.spawnEntity(baby);
            EntityPlayerMP player = self.getLoveCause();
            if (player == null && mate.getLoveCause() != null) {
                player = mate.getLoveCause();
            }
            if (player != null) {
                player.addStat(StatList.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(player, self, mate, baby);
            }
            Random random = baby.getRNG();
            for (int i = 0; i < 7; ++i) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextDouble() * baby.width * 2.0D - baby.width;
                double d4 = 0.5D + random.nextDouble() * baby.height;
                double d5 = random.nextDouble() * baby.width * 2.0D - baby.width;
                level.spawnParticle(EnumParticleTypes.HEART, baby.posX + d3, baby.posY + d4, baby.posZ + d5, d0, d1, d2);
            }
            if (level.getGameRules().getBoolean("doMobLoot")) {
                level.spawnEntity(new EntityXPOrb(level, baby.posX, baby.posY, baby.posZ, random.nextInt(12) + 4));
            }
        }
    }
}
