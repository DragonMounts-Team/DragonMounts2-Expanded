package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.client.breath.impl.HydroBreathFX;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.util.LevelUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


/**
 * Created by TGG on 5/08/2015.
 */
public class WaterBreath extends DragonBreath {
    public WaterBreath(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public BreathAffectedBlock affectBlock(World level, BlockPos pos, BreathAffectedBlock hit) {
        level.spawnParticle(EnumParticleTypes.WATER_SPLASH, pos.getX(), pos.getY(), pos.getZ(), 1.0D, 4.0D, 1.0D);
        if (!DMConfig.BREATH_EFFECTS.value) return new BreathAffectedBlock();
        IBlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
            if (!DMConfig.QUENCHING_BREATH.value) return new BreathAffectedBlock();
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
        } else if (block == Blocks.FARMLAND && state.getValue(BlockFarmland.MOISTURE) < 7) {
            level.setBlockState(pos, state.withProperty(BlockFarmland.MOISTURE, 7));
        }
        return new BreathAffectedBlock(); // reset to zero
    }

    @Override
    public void affectEntity(World level, EntityLivingBase target, BreathAffectedEntity hit) {
        TameableDragonEntity dragon = this.dragon;
        float damage = this.damage * hit.getHitDensity();
        if (target instanceof EntityWaterMob) damage += 4;
        if (target.isPotionActive(MobEffects.WATER_BREATHING)) damage *= 0.5F;
        if (target.isBurning()) {
            target.extinguish();
            target.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0f, 0.0f);
            damage *= 1.5F;
        } else {
            target.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 0.4f, 1.0f);
        }
        target.attackEntityFrom(DamageSource.causeMobDamage(dragon), damage);
        target.knockBack(dragon, 0.2F, dragon.posX - target.posX, dragon.posZ - target.posZ);
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new HydroBreathFX(world, position, direction, power, partialTicks));
    }

    @Override
    public SoundEvent getStartSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_START_WATER;
    }

    @Override
    public SoundEvent getLoopSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_LOOP_WATER;
    }

    @Override
    public SoundEvent getStopSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_STOP_WATER;
    }
}
