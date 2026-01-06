package net.dragonmounts.util;

import com.google.common.base.Predicate;
import net.dragonmounts.compat.FixerCompat;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.math.MathX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class EntityUtil {
    public static boolean isSpectator(Entity entity) {
        return entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator();
    }

    public static boolean addOrMergeEffect(EntityLivingBase entity, Potion effect, int duration, int amplifier, boolean ambient, boolean visible) {
        PotionEffect instance = entity.getActivePotionEffect(effect);
        if (instance == null) {
            entity.addPotionEffect(new PotionEffect(effect, duration, amplifier, ambient, visible));
            return true;
        }
        int oldAmplifier = instance.getAmplifier();
        if (oldAmplifier > amplifier) return false;
        entity.addPotionEffect(new PotionEffect(effect, oldAmplifier == amplifier ? duration + instance.getDuration() : duration, amplifier, ambient, visible));
        return true;
    }

    public static boolean addOrResetEffect(EntityLivingBase entity, Potion effect, int duration, int amplifier, boolean ambient, boolean visible, int threshold) {
        PotionEffect instance = entity.getActivePotionEffect(effect);
        if (instance != null) {
            int oldAmplifier = instance.getAmplifier();
            if (oldAmplifier > amplifier || (
                    oldAmplifier == amplifier && instance.getDuration() > threshold
            )) return false;
        }
        entity.addPotionEffect(new PotionEffect(effect, duration, amplifier, ambient, visible));
        return true;
    }

    public static void ensureUUIDUnique(WorldServer level, Entity entity) {
        UUID uuid = entity.getUniqueID();
        while (level.getEntityFromUuid(uuid) != null) {
            uuid = MathHelper.getRandomUUID(level.rand);
        }
        entity.setUniqueId(uuid);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static <T extends Entity> boolean finalizeSpawn(
            WorldServer level,
            T entity,
            BlockPos pos,
            boolean yOffset,
            IEntityLivingData data,
            BiConsumer<? super WorldServer, ? super T> modifier
    ) {
        float x = pos.getX() + 0.5F, y = yOffset ? getSpawnHeight(level, pos) : pos.getY(), z = pos.getZ() + 0.5F;
        entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(level.rand.nextFloat() * 360.0F), 0.0F);
        if (entity instanceof EntityLiving) {
            EntityLiving $entity = (EntityLiving) entity;
            $entity.rotationYawHead = $entity.rotationYaw;
            $entity.renderYawOffset = $entity.rotationYaw;
            if (ForgeEventFactory.doSpecialSpawn($entity, level, x, y, z, null)) return false;
            $entity.onInitialSpawn(level.getDifficultyForLocation(new BlockPos($entity)), data);
            modifier.accept(level, entity);
            boolean result = level.spawnEntity($entity);
            $entity.playLivingSound();
            return result;
        }
        return false;
    }

    public static ServerDragonEntity spawnDragonFormStack(
            WorldServer level,
            ItemStack stack,
            @Nullable EntityPlayer player,
            BlockPos pos,
            DragonType fallback,
            BiConsumer<WorldServer, ServerDragonEntity> modifier
    ) {
        ServerDragonEntity dragon = new ServerDragonEntity(level);
        NBTTagCompound root = stack.getTagCompound();
        if (root != null) {
            NBTTagCompound data = root.getCompoundTag("EntityTag");
            if (!data.isEmpty()) {
                if (Relation.denyIfNotOwner(data, player)) return null;
                if (!data.hasKey(DragonVariant.DATA_PARAMETER_KEY)) {
                    dragon.overrideType(fallback);
                }
                FixerCompat.disableEntityFixers(data);
            }
        }
        return EntityUtil.finalizeSpawn(level, dragon, pos, true, null, (world, entity) -> {
            if (stack.hasDisplayName()) {
                entity.setCustomNameTag(stack.getDisplayName());
            }
            ItemMonsterPlacer.applyItemEntityDataToEntity(world, player, stack, entity);
            modifier.accept(world, entity);
            ensureUUIDUnique(world, entity);
        }) ? dragon : null;
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

    public static void replaceAttributeModifier(@Nullable IAttributeInstance attribute, AttributeModifier modifier) {
        if (attribute == null) return;
        attribute.removeModifier(modifier.getID());
        attribute.applyModifier(modifier);
    }

    public static boolean isMoving(Entity dragon) {
        double deltaX = dragon.posX - dragon.prevPosX, deltaZ = dragon.posZ - dragon.prevPosZ;
        return deltaX * deltaX + deltaZ * deltaZ > 2.5E-7F;
    }

    public static void clampYaw(Entity entity, float yaw, float limit) {
        entity.setRenderYawOffset(yaw);
        float delta = MathHelper.wrapDegrees(entity.rotationYaw - yaw);
        float limited = MathHelper.clamp(delta, -limit, limit);
        entity.prevRotationYaw += limited - delta;
        entity.rotationYaw += limited - delta;
        entity.setRotationYawHead(entity.rotationYaw);
    }

    public static <T extends Entity> @Nullable T findNearestEntityWithinAABB(
            Entity self,
            Class<? extends T> target,
            AxisAlignedBB aabb,
            @Nullable Predicate<? super T> filter
    ) {
        T closest = null;
        double min = Double.MAX_VALUE;
        for (T candidate : self.world.getEntitiesWithinAABB(target, aabb, filter)) {
            if (candidate == self) continue;
            double dist = candidate.getDistanceSq(self);
            if (dist < min) {
                min = dist;
                closest = candidate;
            }
        }
        return closest;
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
    public static void resizeAndMove(Entity entity, double dx, double dy, double dz, float newWidth, float newHeight, ICollisionObserver observer) {
        AxisAlignedBB entityAABB = entity.getEntityBoundingBox();
        double wDXplus = (newWidth - entity.width) * 0.5;
        double wDYplus = (newHeight - entity.height) * 0.5;
        double wDZplus = (newWidth - entity.width) * 0.5;
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
        double desiredDX = dx, desiredDY = dy, desiredDZ = dz;

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

        entity.posX = (entityAABB.minX + entityAABB.maxX) * 0.5;
        entity.posY = entityAABB.minY;
        entity.posZ = (entityAABB.minZ + entityAABB.maxZ) * 0.5;

        observer.handleMovement(desiredDX, desiredDY, desiredDZ, dx, dy, dz);
    }

    public static void dropItems(Entity entity, ItemStack[] stacks) {
        for (int i = 0, j = stacks.length; i < j; ++i) {
            if (entity.entityDropItem(stacks[i], 0.5F) != null) {
                stacks[i] = ItemStack.EMPTY;
            }
        }
    }
}
