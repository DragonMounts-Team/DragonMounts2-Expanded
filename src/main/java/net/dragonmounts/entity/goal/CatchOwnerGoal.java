/*
 ** 2013 November 03
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.goal;

import net.dragonmounts.entity.ServerDragonEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class CatchOwnerGoal extends EntityAIBase {
    public final ServerDragonEntity dragon;
    protected EntityPlayer owner;

    public CatchOwnerGoal(ServerDragonEntity dragon) {
        this.dragon = dragon;
        this.setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity dragon = this.dragon;
        // don't catch if leashed, sitting or already being ridden
        if (dragon.isSitting()
                || dragon.getLeashed()
                || !dragon.isSaddled()
                || dragon.getControllingPlayer() != null
        ) return false;
        EntityLivingBase owner = dragon.getOwner();
        if (!(owner instanceof EntityPlayer) || owner.fallDistance < 4 || owner.isRiding()) return false;
        this.owner = (EntityPlayer) owner;
        // no point in catching players in creative mode
        if (this.owner.capabilities.isCreativeMode) return false;
        // don't catch if owner is too far away
        double range = dragon.getNavigator().getPathSearchRange();
        if (dragon.getDistanceSq(owner) > range * range) return false;
        // don't catch if owner has a working Elytra equipped
        ItemStack stack = owner.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        return stack.isEmpty() || stack.getItem() != Items.ELYTRA || !ItemElytra.isUsable(stack);
    }

    @Override
    public void updateTask() {
        EntityPlayer owner = this.owner;
        ServerDragonEntity dragon = this.dragon;
        // catch owner in flight if possible
        if (!dragon.isFlying()) {
            dragon.liftOff();
        }
        double dist = dragon.getDistanceSq(owner.posX, owner.getEntityBoundingBox().minY, owner.posZ);
        dragon.setBoosting(dist > 25.0);
        // mount owner if close enough, otherwise move to owner
        if (!owner.isSneaking() && dist < dragon.width * dragon.width * 4.0F + owner.width) {
            owner.startRiding(dragon);
        } else {
            dragon.getNavigator().tryMoveToEntityLiving(owner, 1);
        }
    }
}
