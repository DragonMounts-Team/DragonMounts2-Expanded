package net.dragonmounts.entity.breath.weapons;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
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
public class BreathWeaponPoison extends BreathWeapon {

    private int poisonDuration = 10 * 10;

    public BreathWeaponPoison(TameableDragonEntity i_dragon) {
        super(i_dragon);
    }

    /**
     * if the hitDensity is high enough, manipulate the block (eg set fire to it)
     *
     * @param world
     * @param blockPosition     the world [x,y,z] of the block
     * @param currentHitDensity
     * @return the updated block hit density
     */
    public BreathAffectedBlock affectBlock(World world, Vec3i blockPosition, BreathAffectedBlock currentHitDensity) {
        checkNotNull(world);
        checkNotNull(blockPosition);
        checkNotNull(currentHitDensity);

        BlockPos blockPos = new BlockPos(blockPosition);
        IBlockState iBlockState = world.getBlockState(blockPos);
        Block block = iBlockState.getBlock();

        if (!world.isRemote && !block.isAir(iBlockState, world, blockPos) && world.rand.nextInt(500) == 1) {
            EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            cloud.setOwner(this.dragon);
            cloud.setRadius(1.3F);
            cloud.setDuration(600);
            cloud.setRadiusPerTick((1.0F - cloud.getRadius()) / (float) cloud.getDuration());
            cloud.addEffect(new PotionEffect(MobEffects.POISON, poisonDuration));
            cloud.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            world.spawnEntity(cloud);
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
    public BreathAffectedEntity affectEntity(World world, int entityID, BreathAffectedEntity currentHitDensity) {
        checkNotNull(world);
        checkNotNull(currentHitDensity);

        Entity entity = world.getEntityByID(entityID);
        if (!(entity instanceof EntityLivingBase) || entity.isDead) return null;
        float hitDensity = currentHitDensity.getHitDensity();
        final float DAMAGE_PER_HIT_DENSITY = POISON_DAMAGE * hitDensity;

        triggerDamageExceptions(entity, DAMAGE_PER_HIT_DENSITY, entityID, currentHitDensity);
        entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), DAMAGE_PER_HIT_DENSITY);

        return currentHitDensity;
    }

}
