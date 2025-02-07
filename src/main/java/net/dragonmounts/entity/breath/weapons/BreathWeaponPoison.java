package net.dragonmounts.entity.breath.weapons;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by TGG on 5/08/2015.
 */
public class BreathWeaponPoison extends BreathWeapon {
    public BreathWeaponPoison(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public BreathAffectedBlock affectBlock(World level, BlockPos pos, BreathAffectedBlock hit) {
        IBlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (!block.isAir(state, level, pos) && level.rand.nextFloat() < 0.002F) {
            EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(level, pos.getX(), pos.getY(), pos.getZ());
            cloud.setOwner(this.dragon);
            cloud.setRadius(1.3F);
            cloud.setDuration(600);
            cloud.setRadiusPerTick((1.0F - cloud.getRadius()) / (float) cloud.getDuration());
            cloud.addEffect(new PotionEffect(MobEffects.POISON, 100));
            level.spawnEntity(cloud);
        }
        return new BreathAffectedBlock();
    }

    @Override
    public void affectEntity(World level, EntityLivingBase target, BreathAffectedEntity hit) {
        float density = hit.getHitDensity();
        target.attackEntityFrom(DamageSource.causeMobDamage(this.dragon), this.damage * density);
        EntityUtil.addOrMergeEffect(target, MobEffects.POISON, (int) (2 * hit.getHitDensity()), 0, false, true);
    }
}
