package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.effects.DarkBreathFX;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.DMSounds;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DarkBreath extends DragonBreath {
    public DarkBreath(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public BreathAffectedBlock affectBlock(World level, BlockPos pos, BreathAffectedBlock hit) {
        IBlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (!block.isAir(state, level, pos) && level.rand.nextFloat() < 0.002F) {
            EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(level, pos.getX(), pos.getY(), pos.getZ());
            cloud.setOwner(this.dragon);
            cloud.setParticle(EnumParticleTypes.SMOKE_NORMAL);
            cloud.setRadius(1.4F);
            cloud.setDuration(600);
            cloud.setRadiusPerTick((1.0F - cloud.getRadius()) / (float) cloud.getDuration());
            cloud.addEffect(new PotionEffect(MobEffects.BLINDNESS, 120));
            level.spawnEntity(cloud);
        }
        return new BreathAffectedBlock(); // reset to zero
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new DarkBreathFX(world, position, direction, power, partialTicks));
    }

    @Override
    public SoundEvent getStartSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_START_AIRFLOW;
    }

    @Override
    public SoundEvent getLoopSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_LOOP_AIRFLOW;
    }

    @Override
    public SoundEvent getStopSound(DragonLifeStage stage) {
        return DMSounds.DRAGON_BREATH_STOP_AIRFLOW;
    }
}
