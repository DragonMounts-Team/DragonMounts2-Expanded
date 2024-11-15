package com.TheRPGAdventurer.ROTD.util;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by TGG on 8/07/2015.
 * Performs a ray trace of the player's line of sight to see what the player is looking at.
 * Similar to the vanilla getMouseOver, which is client side only.
 */
public class RayTraceServer {
    /**
     * Find what the player is looking at (block or entity), up to a maximum range
     * based on code from EntityRenderer.getMouseOver
     * Will not target entities which are tamed by the player
     *
     * @return the block or entity that the player is looking at / targeting with their cursor.  null if no collision
     */
    public static RayTraceResult getMouseOver(World world, EntityPlayer player, double distance) { // int range
        Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d view = player.getLookVec().scale(distance);
        RayTraceResult hitBlock = world.rayTraceBlocks(start, start.add(view), true, false, false);
        Entity vehicle = player.getLowestRidingEntity();
        Entity hitEntity = rayTraceEntity(
                world,
                player,
                hitBlock == null ? distance : hitBlock.hitVec.distanceTo(start),
                entity -> entity.canBeCollidedWith() && !(entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()) && entity.getLowestRidingEntity() != vehicle
        );
        return hitEntity == null ? hitBlock : new RayTraceResult(hitEntity, hitEntity.getPositionVector());
    }

    public static Entity rayTraceEntity(World level, Entity entity, double distance, Predicate<? super Entity> filter) {
        Vec3d start = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d view = entity.getLookVec().scale(distance);
        Vec3d end = start.add(view);
        Entity hit = null;
        for (Entity candidate : level.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(view.x, view.y, view.z).grow(1), filter)) {
            AxisAlignedBB border = candidate.getEntityBoundingBox().grow(candidate.getCollisionBorderSize());
            RayTraceResult clip = border.calculateIntercept(start, end);
            if (clip != null) {
                double distanceSQ = start.squareDistanceTo(clip.hitVec);
                if (distanceSQ <= distance) {
                    distance = distanceSQ;
                    hit = candidate;
                }
            } else if (border.contains(end)) {
                double distanceSQ = start.squareDistanceTo(end);
                if (distanceSQ < distance) {
                    distance = distanceSQ;
                    hit = candidate;
                }
            }
        }
        return hit;
    }
}
