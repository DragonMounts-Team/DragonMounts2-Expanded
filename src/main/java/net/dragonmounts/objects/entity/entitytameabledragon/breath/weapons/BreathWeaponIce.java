package net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons;

import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.BreathAffectedBlock;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.BreathAffectedEntity;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by TGG on 5/08/2015.
 * <p>
 * Models the effects of a breathweapon on blocks and entities
 * Usage:
 * 1) Construct with a parent dragon
 * 2) affectBlock() to apply an area of effect to the given block (eg set fire to it)
 * 3) affectEntity() to apply an area of effect to the given entity (eg damage it)
 */
public class BreathWeaponIce extends BreathWeapon {

    public BreathWeaponIce(EntityTameableDragon dragon) {
        super(dragon);
    }

    /**
     * if the hitDensity is high enough, manipulate the block (eg set fire to it)
     *
     * @param world
     * @param blockPosition     the world [x,y,z] of the block
     * @param currentHitDensity
     * @return the updated block hit density
     */
    @Override
    public BreathAffectedBlock affectBlock(World world, Vec3i blockPosition, BreathAffectedBlock currentHitDensity) {
        checkNotNull(world);
        checkNotNull(blockPosition);
        checkNotNull(currentHitDensity);

        BlockPos hitPos = new BlockPos(blockPosition);
        IBlockState hitState = world.getBlockState(hitPos);
        Block block = hitState.getBlock();
        BlockPos upperPos = hitPos.offset(EnumFacing.UP);
        if (block == Blocks.LAVA) {
            world.setBlockState(hitPos, Blocks.OBSIDIAN.getDefaultState());
        } else if (block == Blocks.FLOWING_LAVA) {
            world.setBlockState(hitPos, Blocks.COBBLESTONE.getDefaultState());
        } else if (block == Blocks.FIRE) {
            world.setBlockState(hitPos, Blocks.AIR.getDefaultState());
        } else if (DMUtils.isAir(world, upperPos)) {
            if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
                world.setBlockState(hitPos, (DragonMountsConfig.canIceBreathBePermanent ? Blocks.ICE : Blocks.FROSTED_ICE).getDefaultState());
            } else if (DragonMountsConfig.canIceBreathBePermanent && (
                    block.isLeaves(hitState, world, hitPos) || hitState.getBlockFaceShape(world, hitPos, EnumFacing.UP) == BlockFaceShape.SOLID
            )) {
                world.setBlockState(upperPos, Blocks.SNOW_LAYER.getDefaultState());
            }
        }
        return new BreathAffectedBlock();  // reset to zero
    }

    /**
     * if the hitDensity is high enough, manipulate the entity (eg set fire to it, damage it)
     * A dragon can't be damaged by its own breathweapon;
     *
     * @param world
     * @param entityID          the ID of the affected entity
     * @param currentHitDensity the hit density
     * @return the updated hit density; null if the entity is dead, doesn't exist, or otherwise not affected
     */
    @Override
    public BreathAffectedEntity affectEntity(World world, int entityID, BreathAffectedEntity currentHitDensity) {
        checkNotNull(world);
        checkNotNull(currentHitDensity);

        Entity entity = world.getEntityByID(entityID);
        if (!(entity instanceof EntityLivingBase) || entity.isDead) return null;
        float hitDensity = currentHitDensity.getHitDensity();
        float damage = ICE_DAMAGE * hitDensity;
        if (entity.isBurning()) {
            entity.extinguish();
            entity.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0f, 0.0f);
            damage *= 2;
        }
        triggerDamageExceptions(entity, damage, entityID, currentHitDensity);
        entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), damage);
        ((EntityLivingBase) entity).knockBack(entity, 0.1F, dragon.posX - entity.posX, dragon.posZ - entity.posZ);

        if (dragon.getRidingEntity() != entity && !dragon.isPassenger(entity)) {
            EntityUtil.addOrMergeEffect((EntityLivingBase) entity, MobEffects.SLOWNESS, 100, 0, false, true);
        }

        return currentHitDensity;
    }
}
