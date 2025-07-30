package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by TGG on 5/08/2015.
 */
public class WitherBreath extends DragonBreath {
    public WitherBreath(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public BreathAffectedBlock affectBlock(World level, long location, BreathAffectedBlock hit) {
        BlockPos pos = BlockPos.fromLong(location);
        IBlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (!block.isAir(state, level, pos) && level.rand.nextFloat() < 0.002F) {
            EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(level, pos.getX(), pos.getY(), pos.getZ());
            cloud.setOwner(this.dragon);
            cloud.setParticle(EnumParticleTypes.SMOKE_NORMAL);
            cloud.setRadius(1.4F);
            cloud.setDuration(600);
            cloud.setRadiusPerTick((1.0F - cloud.getRadius()) / (float) cloud.getDuration());
            cloud.addEffect(new PotionEffect(MobEffects.WITHER, 120));
            level.spawnEntity(cloud);
        }
        return new BreathAffectedBlock();
    }

    @Override
    public void affectEntity(World level, EntityLivingBase target, BreathAffectedEntity hit) {
        float density = hit.getHitDensity();
        target.attackEntityFrom(DamageSource.causeMobDamage(this.dragon), this.damage * density);
        EntityUtil.addOrMergeEffect(target, MobEffects.POISON, (int) (4 * hit.getHitDensity()), 0, false, true);
    }
}
