package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.effects.EnderBreathFX;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by TGG on 5/08/2015.
 */
public class EnderBreath extends DragonBreath {
    public EnderBreath(TameableDragonEntity dragon, float damage) {
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
            cloud.setRadius(1.6F);
            cloud.setDuration(750);
            cloud.setRadiusPerTick((1.0F - cloud.getRadius()) / (float) cloud.getDuration());
            cloud.addEffect(new PotionEffect(MobEffects.WITHER, 120));
            level.spawnEntity(cloud);
        }
        return new BreathAffectedBlock(); // reset to zero
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new EnderBreathFX(world, position, direction, power, partialTicks));
    }
}
