/*
 ** 2012 August 26
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.goal;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Derivative EntityAIMate class to deal with some special values that can't be
 * applied with an extension thanks to the visibility.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * @see net.minecraft.entity.ai.EntityAIMate
 */
public class BreedGoal extends EntityAIBase {
    public final ServerDragonEntity dragon;
    private final double speed;
    private int loveTime;
    protected @Nullable ServerDragonEntity partner;

    public BreedGoal(ServerDragonEntity dragon, double speed) {
        this.dragon = dragon;
        this.speed = speed;
        this.setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity self = this.dragon;
        if (self.isSitting() || !self.isInLove()) return false;
        this.partner = EntityUtil.findNearestEntityWithinAABB(
                self,
                ServerDragonEntity.class,
                self.getEntityBoundingBox().grow(16.0, 16.0, 16.0),
                self::canMateWith
        );
        return this.partner != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        ServerDragonEntity partner = this.partner;
        return partner != null && partner.isEntityAlive() && partner.isInLove() && this.loveTime < 60;
    }

    @Override
    public void resetTask() {
        this.partner = null;
        this.loveTime = 0;
    }

    @Override
    public void updateTask() {
        ServerDragonEntity partner = this.partner;
        assert partner != null;
        ServerDragonEntity self = this.dragon;
        self.getLookHelper().setLookPositionWithEntity(partner, 10.0F, (float) self.getVerticalFaceSpeed());
        self.getNavigator().tryMoveToEntityLiving(partner, this.speed);
        if (++this.loveTime >= 60 && self.getDistanceSq(partner) < 9.0) {
            this.breed();
        }
    }

    private void breed() {
        ServerDragonEntity partner = this.partner;
        assert partner != null;
        ServerDragonEntity self = this.dragon;
        ServerDragonEntity baby = self.createChild(partner);
        if (baby == null) return;
        World level = baby.world;
        baby.setLocationAndAngles(self.posX, self.posY, self.posZ, 0, 0);
        level.spawnEntity(baby);
        EntityPlayerMP player = self.getLoveCause();
        if (player == null) {
            player = partner.getLoveCause();
        }
        if (player != null) {
            player.addStat(StatList.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(player, self, partner, baby);
        }
        self.resetInLove();
        partner.resetInLove();
        level.setEntityState(self, (byte) 18);
        if (level.getGameRules().getBoolean("doMobLoot")) {
            level.spawnEntity(new EntityXPOrb(level, baby.posX, baby.posY, baby.posZ, baby.getRNG().nextInt(12) + 4));
        }
    }
}
