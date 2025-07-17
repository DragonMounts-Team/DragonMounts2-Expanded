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
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity dragon = this.dragon;
        // don't catch if leashed, sitting or already being ridden
        if (dragon.getLeashed() || dragon.isSitting() || !dragon.isSaddled() || dragon.getControllingPlayer() != null)
            return false;
        EntityLivingBase owner = dragon.getOwner();
        if (!(owner instanceof EntityPlayer)) return false;
        this.owner = (EntityPlayer) owner;
        // no point in catching players in creative mode
        if (this.owner.capabilities.isCreativeMode) return false;
        // don't catch if owner has a working Elytra equipped
        ItemStack stack = owner.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA && ItemElytra.isUsable(stack)) {
            return false;
        }
        return owner.fallDistance > 4;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

    @Override
    public void updateTask() {
        ServerDragonEntity dragon = this.dragon;
        EntityPlayer owner = this.owner;
        // catch owner in flight if possible
        if (!dragon.isFlying()) {
            dragon.liftOff();
        }
        // don't catch if owner is too far away
        double followRange = dragon.getNavigator().getPathSearchRange();
        dragon.setBoosting(dragon.getDistance(owner) < 1);
        if (dragon.getDistance(owner) < followRange) {
            // mount owner if close enough, otherwise move to owner
            if (dragon.getDistance(owner) <= dragon.width || dragon.getDistance(owner) <= dragon.height && !owner.isSneaking() && dragon.isFlying()) {
                owner.startRiding(dragon);
            } else {
                dragon.getNavigator().tryMoveToEntityLiving(owner, 1);
            }
        }
    }
}
