package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.client.breath.impl.NetherBreathFX;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by TGG on 5/08/2015.
 */
public class NetherBreath extends FireBreath {
    public NetherBreath(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public BreathAffectedBlock affectBlock(World level, long location, BreathAffectedBlock hit) {
        // Flammable blocks: set fire to them once they have been exposed enough.  After sufficient exposure, destroy the
        //   block (otherwise -if it's raining, the burning block will keep going out)
        // Non-flammable blocks:
        // 1) liquids (except lava) evaporate
        // 2) If the block can be smelted (eg sand), then convert the block to the smelted version
        // 3) If the block can't be smelted then convert to lava
        if (DMConfig.IGNITING_BREATH.value || DMConfig.SMELTING_BREATH.value) {
            BlockPos pos = BlockPos.fromLong(location);
            IBlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            Random rand = level.rand;
            float max = 0.0F;
            for (EnumFacing facing : EnumFacing.values()) {
                float threshold = this.calcIgnitionThreshold(level, pos, block, facing);
                float density = hit.getHitDensity(facing);
                if (density > max) {
                    max = density;
                }
                if (density < threshold || rand.nextFloat() < 0.1875F) continue;
                BlockPos sideToIgnite = pos.offset(facing);
                if (level.isAirBlock(sideToIgnite)) {
                    this.burnBlock(sideToIgnite, rand, level);
                    //    if (densityOfThisFace >= thresholdForDestruction && state.getBlockHardness(level, pos) != -1 && DragonMountsConfig.canFireBreathAffectBlocks) {
                    //   level.setBlockToAir(pos);
                }
            }
            if (DMConfig.SMELTING_BREATH.value && max > 0.25F) {
                this.smeltBlock(level, pos, state);
            }
        }
        return new BreathAffectedBlock(); // reset to zero
    }

    @Override
    public void affectEntity(World world, EntityLivingBase target, BreathAffectedEntity hit) {
        float damage = this.damage * hit.getHitDensity();
        if (target.isWet()) damage *= 2.0F;
        target.attackEntityFrom(DamageSource.causeMobDamage(this.dragon), damage);
        target.setFire(8);
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new NetherBreathFX(world, position, direction, power, partialTicks));
    }
}
