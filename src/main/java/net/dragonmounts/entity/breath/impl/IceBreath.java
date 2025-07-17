package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.LevelUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by TGG on 5/08/2015.
 */
public class IceBreath extends DragonBreath {
    public IceBreath(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public BreathAffectedBlock affectBlock(World level, BlockPos pos, BreathAffectedBlock hit) {
        IBlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        BlockPos upper;
        if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
            int value = state.getValue(BlockLiquid.LEVEL);
            if (value == 0) {
                level.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
            } else if (value <= 4) {
                level.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
            }
            LevelUtil.playExtinguishEffect(level, pos);
        } else if (block == Blocks.FIRE) {
            level.setBlockToAir(pos);
            LevelUtil.playExtinguishEffect(level, pos);
        } else if (LevelUtil.isAir(level, upper = pos.offset(EnumFacing.UP))) {
            if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
                level.setBlockState(pos, (DragonMountsConfig.canIceBreathBePermanent ? Blocks.ICE : Blocks.FROSTED_ICE).getDefaultState());
            } else if (DragonMountsConfig.canIceBreathBePermanent && (
                    block.isLeaves(state, level, pos) || state.getBlockFaceShape(level, pos, EnumFacing.UP) == BlockFaceShape.SOLID
            )) {
                level.setBlockState(upper, Blocks.SNOW_LAYER.getDefaultState());
            }
        }
        return new BreathAffectedBlock(); // reset to zero
    }

    @Override
    public void affectEntity(World level, EntityLivingBase target, BreathAffectedEntity hit) {
        TameableDragonEntity dragon = this.dragon;
        float damage = this.damage * hit.getHitDensity();
        if (target.isBurning()) {
            target.extinguish();
            target.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0f, 0.0f);
            damage *= 2;
        }
        target.attackEntityFrom(DamageSource.causeMobDamage(dragon), damage);
        EntityUtil.addOrMergeEffect(target, MobEffects.SLOWNESS, (int) (2 * hit.getHitDensity()), 0, false, true);
        target.knockBack(dragon, 0.1F, dragon.posX - target.posX, dragon.posZ - target.posZ);
    }
}
