package net.dragonmounts.entity.breath.impl;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.util.math.MathX;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by TGG on 7/12/2015.
 */
public class AetherBreath extends DragonBreath {
    public static final Reference2IntOpenHashMap<Material> MATERIAL_DESTROY_DENSITY;
    public static final Reference2IntOpenHashMap<Block> BLOCK_DESTROY_DENSITY;

    public AetherBreath(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public BreathAffectedBlock affectBlock(World level, BlockPos pos, BreathAffectedBlock hit) {
        if (!DMConfig.DESTRUCTIVE_BREATH.value) return new BreathAffectedBlock();
        IBlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        // effects- which occur after the block has been exposed for sufficient time
        // soft blocks such as sand, leaves, grass, flowers, plants, etc get blown away (destroyed)
        // blows away snow but not ice
        // shatters panes, but not glass
        // extinguish torches
        // causes fire to spread rapidly - NO, this looks stupid, so delete it
        int destroyDensity = BLOCK_DESTROY_DENSITY.getInt(block);
        if (destroyDensity == -1) {
            destroyDensity = MATERIAL_DESTROY_DENSITY.getInt(state.getMaterial());
            if (destroyDensity == -1) return hit;
        }
        if (hit.getMaxHitDensity() > destroyDensity) {
            level.destroyBlock(pos, true);
            return new BreathAffectedBlock();
        }
        /*if (material == Material.FIRE) {
            final float THRESHOLD_FIRE_SPREAD = 1;
            final float MAX_FIRE_DENSITY = 10;
            final int MAX_PATH_LENGTH = 4;
            double density = hit.getMaxHitDensity();
            if (density > THRESHOLD_FIRE_SPREAD) {
                int pathLength = MathHelper.floor(MAX_PATH_LENGTH / MAX_FIRE_DENSITY * density);
                if (pathLength > MAX_PATH_LENGTH) {
                    pathLength = MAX_PATH_LENGTH;
                }
                //        spreadFire(world, blockPos, pathLength);    // removed because it didn't work well
            }
            return hit;
        }*/
        return hit;
    }

    @Override
    public void affectEntity(World level, EntityLivingBase target, BreathAffectedEntity hit) {
        if (target.isBurning()) {
            target.extinguish();
        }
        TameableDragonEntity dragon = this.dragon;

        //    System.out.format("Old entity motion:[%.2f, %.2f, %.2f]\n", entity.motionX, entity.motionY, entity.motionZ);
        // push in the direction of the wind, but add a vertical upthrust as well
        final double FORCE_MULTIPLIER = 0.05;
        final double VERTICAL_FORCE_MULTIPLIER = 0.05;
        float density = hit.getHitDensity();
        Vec3d airForceDirection = hit.getHitDensityDirection();
        Vec3d airMotion = MathX.multiply(airForceDirection, FORCE_MULTIPLIER);

        final double WT_ENTITY = 0.05;
        final double WT_AIR = 1 - WT_ENTITY;
        target.attackEntityFrom(DamageSource.causeMobDamage(dragon), this.damage * density);
        target.knockBack(target, 0.8F, dragon.posX - target.posX, dragon.posZ - target.posZ);
        if (density > 1.0) {
            final double GRAVITY_OFFSET = -0.08;
            Vec3d up = new Vec3d(0, 1, 0);
            Vec3d upMotion = MathX.multiply(up, VERTICAL_FORCE_MULTIPLIER * density);
            //      System.out.format("upMotion:%s\n", upMotion);
            target.motionY = WT_ENTITY * (target.motionY - GRAVITY_OFFSET) + WT_AIR * upMotion.y;
        }

        //    System.out.format("airMotion:%s\n", airMotion);
        //    System.out.format("New entity motion:[%.2f, %.2f, %.2f]\n", entity.motionX, entity.motionY, entity.motionZ);

        // final int DELAY_UNTIL_DECAY=5;
        // final float DECAY_PERCENTAGE_PER_TICK=10.0F;
        //        currentHitDensity.setDecayParameters(DECAY_PERCENTAGE_PER_TICK, DELAY_UNTIL_DECAY);
    }

    static {
        Reference2IntOpenHashMap<Material> material = MATERIAL_DESTROY_DENSITY = new Reference2IntOpenHashMap<>();
        Reference2IntOpenHashMap<Block> block = BLOCK_DESTROY_DENSITY = new Reference2IntOpenHashMap<>();
        material.defaultReturnValue(-1);
        block.defaultReturnValue(-1);
        //instant
        material.put(Material.LEAVES, 0);
        material.put(Material.PLANTS, 0);
        material.put(Material.FIRE, 0);
        block.put(Blocks.TORCH, 0);
        //slow
        material.put(Material.VINE, 100);
        material.put(Material.WEB, 100);
        material.put(Material.GOURD, 100);
        material.put(Material.SPONGE, 100);
        material.put(Material.SAND, 100);
        material.put(Material.SNOW, 100);
        material.put(Material.CRAFTED_SNOW, 100);
        material.put(Material.CACTUS, 100);
        //moderate
        block.put(Blocks.GLASS_PANE, 10);
        block.put(Blocks.STAINED_GLASS_PANE, 10);
    }
}
