package top.dragonmounts.objects.entity.entitytameabledragon.breath.weapons;

import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.BreathAffectedBlock;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.BreathAffectedEntity;
import top.dragonmounts.util.math.MathX;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by TGG on 7/12/2015.
 */
public class BreathWeaponAether extends BreathWeapon {

    public BreathWeaponAether(EntityTameableDragon i_dragon) {
        super(i_dragon);
        initialiseStatics();
    }

    @Override
    public BreathAffectedBlock affectBlock(World world, Vec3i blockPosition, BreathAffectedBlock currentHitDensity) {
        checkNotNull(world);
        checkNotNull(blockPosition);
        checkNotNull(currentHitDensity);

        BlockPos blockPos=new BlockPos(blockPosition);
        IBlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();

        // effects- which occur after the block has been exposed for sufficient time
        // soft blocks such as sand, leaves, grass, flowers, plants, etc get blown away (destroyed)
        // blows away snow but not ice
        // shatters panes, but not glass
        // extinguish torches
        // causes fire to spread rapidly - NO, this looks stupid, so delete it

        Material material = state.getMaterial();

        int destroyDensity = DESTROY_DENSITY.getInt(material);
        if (destroyDensity != -1 && currentHitDensity.getMaxHitDensity() > destroyDensity) {
            world.destroyBlock(blockPos, true);
            return new BreathAffectedBlock();
        }

        if (material==Material.FIRE) {
            final float THRESHOLD_FIRE_SPREAD=1;
            final float MAX_FIRE_DENSITY=10;
            final int MAX_PATH_LENGTH=4;
            double density=currentHitDensity.getMaxHitDensity();
            if (density > THRESHOLD_FIRE_SPREAD) {
                int pathLength=MathHelper.floor(MAX_PATH_LENGTH / MAX_FIRE_DENSITY * density);
                if (pathLength > MAX_PATH_LENGTH) {
                    pathLength=MAX_PATH_LENGTH;
                }
                //        spreadFire(world, blockPos, pathLength);    // removed because it didn't work well
            }
            return currentHitDensity;
        }

        if (block==Blocks.TORCH) {
            final float THRESHOLD_FIRE_EXTINGUISH=1;
            if (currentHitDensity.getMaxHitDensity() > THRESHOLD_FIRE_EXTINGUISH) {
                final boolean DROP_BLOCK=true;
                world.destroyBlock(blockPos, DROP_BLOCK);
                return new BreathAffectedBlock();
            }
            return currentHitDensity;
        }

        if (block==Blocks.GLASS_PANE || block==Blocks.STAINED_GLASS_PANE) {
            final float THRESHOLD_SMASH_PANE=1;
            if (currentHitDensity.getMaxHitDensity() > THRESHOLD_SMASH_PANE) {
                final boolean DROP_BLOCK=true;
                world.destroyBlock(blockPos, DROP_BLOCK);
                return new BreathAffectedBlock();
            }
            return currentHitDensity;
        }

        return currentHitDensity;
    }

    @Override
    public BreathAffectedEntity affectEntity(World world, int entityID, BreathAffectedEntity currentHitDensity) {
        // 1) extinguish fire on entity
        // 2) pushes entity in the direction of the air, with upward thrust added
        checkNotNull(world);
        checkNotNull(currentHitDensity);
        float hitDensity=currentHitDensity.getHitDensity();
        if (entityID==dragon.getEntityId()) return null;

        Entity entity=world.getEntityByID(entityID);
        if (entity==null || !(entity instanceof EntityLivingBase) || entity.isDead) {
            return null;
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer=(EntityPlayer) entity;
            if (entityPlayer.getRidingEntity()==dragon) {
                return null;
            }
        }

        if (entity.isBurning()) {
            entity.extinguish();
        }


        final float DAMAGE_PER_HIT_DENSITY=FIRE_DAMAGE * hitDensity;

        //    System.out.format("Old entity motion:[%.2f, %.2f, %.2f]\n", entity.motionX, entity.motionY, entity.motionZ);
        // push in the direction of the wind, but add a vertical upthrust as well
        final double FORCE_MULTIPLIER=0.05;
        final double VERTICAL_FORCE_MULTIPLIER=0.05;
        float airForce=currentHitDensity.getHitDensity();
        Vec3d airForceDirection=currentHitDensity.getHitDensityDirection();
        Vec3d airMotion=MathX.multiply(airForceDirection, FORCE_MULTIPLIER);

        final double WT_ENTITY=0.05;
        final double WT_AIR=1 - WT_ENTITY;
        ((EntityLivingBase) entity).knockBack(entity, 0.8F, dragon.posX - entity.posX, dragon.posZ - entity.posZ);
        entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), DAMAGE_PER_HIT_DENSITY);
        triggerDamageExceptions(entity, DAMAGE_PER_HIT_DENSITY,entityID, currentHitDensity);
        if (airForce > 1.0) {
            final double GRAVITY_OFFSET=-0.08;
            Vec3d up=new Vec3d(0, 1, 0);
            Vec3d upMotion=MathX.multiply(up, VERTICAL_FORCE_MULTIPLIER * airForce);
            //      System.out.format("upMotion:%s\n", upMotion);
            entity.motionY=WT_ENTITY * (entity.motionY - GRAVITY_OFFSET) + WT_AIR * upMotion.y;
        }

        //    System.out.format("airMotion:%s\n", airMotion);
        //    System.out.format("New entity motion:[%.2f, %.2f, %.2f]\n", entity.motionX, entity.motionY, entity.motionZ);

        final int DELAY_UNTIL_DECAY=5;
        final float DECAY_PERCENTAGE_PER_TICK=10.0F;
        //        currentHitDensity.setDecayParameters(DECAY_PERCENTAGE_PER_TICK, DELAY_UNTIL_DECAY);

        return currentHitDensity;
    }

    private static final Reference2IntOpenHashMap<Material> DESTROY_DENSITY = new Reference2IntOpenHashMap<>();

    private void initialiseStatics() {
        if (!DESTROY_DENSITY.isEmpty()) return;
        DESTROY_DENSITY.defaultReturnValue(-1);
        //instant
        DESTROY_DENSITY.put(Material.LEAVES, 0);
        DESTROY_DENSITY.put(Material.PLANTS, 0);
        DESTROY_DENSITY.put(Material.FIRE, 0);
        //slow
        DESTROY_DENSITY.put(Material.VINE, 100);
        DESTROY_DENSITY.put(Material.WEB, 100);
        DESTROY_DENSITY.put(Material.GOURD, 100);
        DESTROY_DENSITY.put(Material.SPONGE, 100);
        DESTROY_DENSITY.put(Material.SAND, 100);
        DESTROY_DENSITY.put(Material.SNOW, 100);
        DESTROY_DENSITY.put(Material.CRAFTED_SNOW, 100);
        DESTROY_DENSITY.put(Material.CACTUS, 100);
        //moderate (10)
    }
}
