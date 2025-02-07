package net.dragonmounts.entity.breath.weapons;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


/**
 * Created by TGG on 5/08/2015.
 */
public class BreathWeaponHydro extends BreathWeapon {
    public BreathWeaponHydro(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public BreathAffectedBlock affectBlock(World level, BlockPos pos, BreathAffectedBlock hit) {
        IBlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        level.spawnParticle(EnumParticleTypes.WATER_SPLASH, pos.getX(), pos.getY(), pos.getZ(), 1.0D, 4.0D, 1.0D);
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
}
