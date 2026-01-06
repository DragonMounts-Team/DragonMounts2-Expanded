package net.dragonmounts.util;

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
     * @see net.minecraft.client.renderer.EntityRenderer#getMouseOver(float)
     */
    public static RayTraceResult getMouseOver(World world, EntityPlayer player, double distance) {
        Vec3d view = player.getLookVec();
        Vec3d eyes = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        RayTraceResult hitBlock = world.rayTraceBlocks(eyes, eyes.add(view.x * distance, view.y * distance, view.z * distance), false, false, true);
        Entity hitEntity = rayTraceEntity(world, player, hitBlock == null ? distance : hitBlock.hitVec.distanceTo(eyes), RayTraceServer::isSelectable);
        return hitEntity == null ? hitBlock : new RayTraceResult(hitEntity, hitEntity.getPositionVector());
    }

    public static Entity rayTraceEntity(World level, Entity entity, double distance, Predicate<? super Entity> filter) {
        Vec3d eyes = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d view = entity.getLookVec();
        double viewX = view.x * distance, viewY = view.y * distance, viewZ = view.z * distance;
        Vec3d dest = eyes.add(viewX, viewY, viewZ);
        Entity vehicle = entity.getLowestRidingEntity();
        Entity hit = null;
        for (Entity candidate : level.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(viewX, viewY, viewZ).grow(1), filter)) {
            AxisAlignedBB border = candidate.getEntityBoundingBox().grow(candidate.getCollisionBorderSize());
            if (border.contains(eyes)) {
                if (0.0 < distance) {
                    distance = 0.0;
                    hit = candidate;
                }
            } else {
                RayTraceResult clip = border.calculateIntercept(eyes, dest);
                if (clip != null) {
                    double dist = eyes.squareDistanceTo(clip.hitVec);
                    if (dist < distance || distance == 0.0) {
                        if (!candidate.canRiderInteract() && candidate.getLowestRidingEntity() == vehicle) {
                            if (distance == 0.0D) {
                                hit = candidate;
                            }
                        } else {
                            distance = dist;
                            hit = candidate;
                        }
                    }
                }
            }
        }
        return hit;
    }

    public static boolean isSelectable(Entity entity) {
        return entity != null && entity.canBeCollidedWith() && !EntityUtil.isSpectator(entity);
    }
}
