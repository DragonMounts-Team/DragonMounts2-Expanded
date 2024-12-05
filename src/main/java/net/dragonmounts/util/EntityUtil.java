package net.dragonmounts.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.util.math.MathX;
import net.dragonmounts.util.math.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityUtil {
    public static boolean addOrMergeEffect(EntityLivingBase entity, Potion effect, int duration, int amplifier, boolean ambient, boolean visible) {
        PotionEffect instance = entity.getActivePotionEffect(effect);
        if (instance == null) {
            entity.addPotionEffect(new PotionEffect(effect, duration, amplifier, ambient, visible));
            return true;
        }
        int oldAmplifier = instance.getAmplifier();
        if (oldAmplifier < amplifier) return false;
        entity.addPotionEffect(new PotionEffect(effect, oldAmplifier == amplifier ? duration + instance.getDuration() : duration, amplifier, ambient, visible));
        return true;
    }

    public static boolean addOrResetEffect(EntityLivingBase entity, Potion effect, int duration, int amplifier, boolean ambient, boolean visible, int threshold) {
        PotionEffect instance = entity.getActivePotionEffect(effect);
        if (instance != null && instance.getAmplifier() == amplifier && instance.getDuration() > threshold)
            return false;
        entity.addPotionEffect(new PotionEffect(effect, duration, amplifier, ambient, visible));
        return true;
    }

    public static void finalizeSpawn(World level, Entity entity, BlockPos pos, boolean yOffset, IEntityLivingData data) {
        float x = pos.getX() + 0.5F, y = yOffset ? getSpawnHeight(level, pos) : pos.getY(), z = pos.getZ() + 0.5F;
        entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(level.rand.nextFloat() * 360.0F), 0.0F);
        if (entity instanceof EntityLiving) {
            EntityLiving $entity = (EntityLiving) entity;
            $entity.rotationYawHead = $entity.rotationYaw;
            $entity.renderYawOffset = $entity.rotationYaw;
            if (ForgeEventFactory.doSpecialSpawn($entity, level, x, y, z, null)) return;
            $entity.onInitialSpawn(level.getDifficultyForLocation(new BlockPos($entity)), data);
            level.spawnEntity(entity);
            $entity.playLivingSound();
        }
    }

    public static boolean notOwner(NBTTagCompound data, @Nullable EntityPlayer player, @Nullable String feedback) {
        if (player == null) return false;
        String owner = data.getString("OwnerUUID");
        if (owner.isEmpty() || player.getUniqueID().toString().equals(owner)) return false;
        if (feedback != null) {
            player.sendStatusMessage(new TextComponentTranslation(feedback), true);
        }
        return true;
    }

    public static float getSpawnHeight(World level, BlockPos pos) {
        AxisAlignedBB box = new AxisAlignedBB(pos).expand(0.0D, -1.0D, 0.0D);
        List<AxisAlignedBB> list = level.getCollisionBoxes(null, box);
        if (list.isEmpty()) return (float) box.minY;
        double height = box.minY;
        for (AxisAlignedBB $box : list) {
            height = Math.max($box.maxY, height);
        }
        return (float) height;
    }

    public static void replaceAttributeModifier(@Nullable IAttributeInstance attribute, UUID uuid, String name, double amount, int operator, boolean serializable) {
        if (attribute == null) return;
        attribute.removeModifier(uuid);
        attribute.applyModifier(new AttributeModifier(uuid, name, amount, operator).setSaved(serializable));
    }

    /**
     * 1) resizes the entity around its centre
     * 2) takes into account any nearby objects that the entity might collide with
     * Tries to move the entity by the passed in displacement. Args: dx, dy, dz
     * Copied from vanilla; irrelevant parts deleted; modify to accommodate a change in size
     * expands the entity around the centre position:
     * if the expansion causes it to bump against another collision box, temporarily ignore the expansion on
     * that side.  bumping into x also constrains z because width is common to both.
     *
     * @param dx        dx, dy, dz are the desired movement/displacement of the entity
     * @param newHeight the new entity height
     * @param newWidth  the new entity width
     * @return returns a collection showing which parts of the entity collided with an object- eg
     * (WEST, [3,2,6]-->[3.5, 2, 6] means the west face of the entity collided; the entity tried to move to
     * x = 3, but got pushed back to x=3.5
     */
    public static ObjectArrayList<Pair<EnumFacing, AxisAlignedBB>> moveAndResize(Entity entity, double dx, double dy, double dz, float newWidth, float newHeight) {
        entity.world.profiler.startSection("move and resize (dm)");
        AxisAlignedBB entityAABB = entity.getEntityBoundingBox();
        double wDXplus = (newWidth - entity.width) / 2.0;
        double wDYplus = (newHeight - entity.height) / 2.0;
        double wDZplus = (newWidth - entity.width) / 2.0;
        double wDXneg = -wDXplus;
        double wDYneg = -wDYplus;
        double wDZneg = -wDZplus;
        List<AxisAlignedBB> collidingAABB = entity.world.getCollisionBoxes(entity, entityAABB.grow(dx, dy, dz));
        if (MathX.isSignificantlyDifferent(newHeight, entity.height)) {
            for (AxisAlignedBB aabb : collidingAABB) {
                wDYplus = aabb.calculateYOffset(entityAABB, wDYplus);
                wDYneg = aabb.calculateYOffset(entityAABB, wDYneg);
            }
            entity.height += (float) (wDYplus - wDYneg);
        } else {
            wDYplus = 0;
            wDYneg = 0;
        }

        if (MathX.isSignificantlyDifferent(newWidth, entity.width)) {
            for (AxisAlignedBB aabb : collidingAABB) {
                wDXplus = aabb.calculateXOffset(entityAABB, wDXplus);
                wDXneg = aabb.calculateXOffset(entityAABB, wDXneg);
                wDZplus = aabb.calculateZOffset(entityAABB, wDZplus);
                wDZneg = aabb.calculateZOffset(entityAABB, wDZneg);
            }
            // constrain width based on both x and z collisions to make sure width remains equal for x and z
            wDXplus = Math.min(wDXplus, wDZplus);
            wDXneg = Math.max(wDXneg, wDZneg);
            wDZplus = wDXplus;
            wDZneg = wDXneg;
            entity.width += (float) (wDXplus - wDXneg);
        } else {
            wDXplus = 0;
            wDXneg = 0;
            wDZplus = 0;
            wDZneg = 0;
        }

        entityAABB = new AxisAlignedBB(entityAABB.minX + wDXneg, entityAABB.minY + wDYneg, entityAABB.minZ + wDZneg, entityAABB.maxX + wDXplus, entityAABB.maxY + wDYplus, entityAABB.maxZ + wDZplus);

        double desiredDX = dx;
        double desiredDY = dy;
        double desiredDZ = dz;

        for (AxisAlignedBB aabb : collidingAABB) {
            dy = aabb.calculateYOffset(entityAABB, dy);
        }
        entityAABB = entityAABB.offset(0, dy, 0);

        for (AxisAlignedBB aabb : collidingAABB) {
            dx = aabb.calculateXOffset(entityAABB, dx);
        }
        entityAABB = entityAABB.offset(dx, 0, 0);

        for (AxisAlignedBB aabb : collidingAABB) {
            dz = aabb.calculateZOffset(entityAABB, dz);
        }
        entityAABB = entityAABB.offset(0, 0, dz);
        entity.setEntityBoundingBox(entityAABB);

        entity.posX = (entityAABB.minX + entityAABB.maxX) / 2.0;
        entity.posY = entityAABB.minY;
        entity.posZ = (entityAABB.minZ + entityAABB.maxZ) / 2.0;

        entity.collidedHorizontally = desiredDX != dx || desiredDZ != dz;
        entity.collidedVertically = desiredDY != dy;
        entity.onGround = entity.collidedVertically && desiredDY < 0.0;
        entity.collided = entity.collidedHorizontally || entity.collidedVertically;

        // if we collided in any direction, stop the entity's motion in that direction, and mark the truncated zone
        //   as a collision zone - i.e if we wanted to move to dx += 0.5, but actually could only move +0.2, then the
        //   collision zone is the region from +0.2 to +0.5
        ObjectArrayList<Pair<EnumFacing, AxisAlignedBB>> collisions = new ObjectArrayList<>(3);
        if (desiredDX != dx) {
            entity.motionX = 0.0D;
            if (desiredDX < 0) {
                collisions.add(new Pair<>(EnumFacing.WEST, new AxisAlignedBB(entityAABB.minX + (desiredDX - dx), entityAABB.minY, entityAABB.minZ, entityAABB.minX, entityAABB.maxY, entityAABB.maxZ)));
            } else {
                collisions.add(new Pair<>(EnumFacing.EAST, new AxisAlignedBB(entityAABB.maxX, entityAABB.minY, entityAABB.minZ, entityAABB.maxX + (desiredDX - dx), entityAABB.maxY, entityAABB.maxZ)));
            }
        }

        if (desiredDY != dy) {
            entity.motionY = 0.0D;
            if (desiredDY < 0) {
                collisions.add(new Pair<>(EnumFacing.DOWN, new AxisAlignedBB(entityAABB.minX, entityAABB.minY + (desiredDY - dy), entityAABB.minZ, entityAABB.maxX, entityAABB.minY, entityAABB.maxZ)));
            } else {
                collisions.add(new Pair<>(EnumFacing.UP, new AxisAlignedBB(entityAABB.minX, entityAABB.maxY, entityAABB.minZ, entityAABB.maxX, entityAABB.maxY + (desiredDY - dy), entityAABB.maxZ)));
            }
        }

        if (desiredDZ != dz) {
            entity.motionZ = 0.0D;
            if (desiredDZ < 0) {
                collisions.add(new Pair<>(EnumFacing.NORTH, new AxisAlignedBB(entityAABB.minX, entityAABB.minY, entityAABB.minZ + (desiredDZ - dz), entityAABB.maxX, entityAABB.maxY, entityAABB.minZ)));
            } else {
                collisions.add(new Pair<>(EnumFacing.SOUTH, new AxisAlignedBB(entityAABB.minX, entityAABB.minY, entityAABB.maxZ, entityAABB.maxX, entityAABB.maxY, entityAABB.maxZ + (desiredDZ - dz))));
            }
        }
        entity.world.profiler.endSection();
        return collisions;
    }
}
