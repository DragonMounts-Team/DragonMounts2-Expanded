package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FireBreath extends DragonBreath {
    public FireBreath(TameableDragonEntity dragon, float damage) {
        super(dragon, damage);
    }

    @Override
    public BreathAffectedBlock affectBlock(World level, BlockPos pos, BreathAffectedBlock hit) {
        // Flammable blocks: set fire to them once they have been exposed enough.  After sufficient exposure, destroy the
        //   block (otherwise -if it's raining, the burning block will keep going out)
        // Non-flammable blocks:
        // 1) liquids (except lava) evaporate
        // 2) If the block can be smelted (eg sand), then convert the block to the smelted version
        // 3) If the block can't be smelted then convert to lava
        if (DragonMountsConfig.canFireBreathAffectBlocks) {
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
                if (density < threshold || rand.nextFloat() < 0.0625F) continue;
                BlockPos sideToIgnite = pos.offset(facing);
                if (level.isAirBlock(sideToIgnite)) {
                    this.burnBlock(sideToIgnite, rand, level);
                    //    if (densityOfThisFace >= thresholdForDestruction && state.getBlockHardness(level, pos) != -1 && DragonMountsConfig.canFireBreathAffectBlocks) {
                    //   level.setBlockToAir(pos);
                }
            }
            if (max > 0.5F) {
                this.smeltBlock(level, pos, state);
            }
        }
        return new BreathAffectedBlock();  // reset to zero
    }

    @Override
    public void affectEntity(World level, EntityLivingBase target, BreathAffectedEntity hit) {
        float damage = this.damage * hit.getHitDensity();
        if (target.isWet()) damage *= 1.5F;
        target.attackEntityFrom(DamageSource.causeMobDamage(this.dragon), damage);
        target.setFire(4);
    }

    protected void smeltBlock(World level, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        Item item = Item.getItemFromBlock(block);
        if (item == Items.AIR) return;
        ItemStack stack = FurnaceRecipes.instance().getSmeltingResult(
                new ItemStack(item, 1, item.getHasSubtypes() ? block.getMetaFromState(state) : 0)
        );
        if (stack.isEmpty()) return;
        Block result = Block.getBlockFromItem(stack.getItem());
        if (result == Blocks.AIR) return;
        level.setBlockState(pos, result.getStateFromMeta(stack.getMetadata()));
    }

    protected void burnBlock(BlockPos sideToIgnite, Random rand, World world) {
        world.setBlockState(sideToIgnite, Blocks.FIRE.getDefaultState());
        world.playSound(
                sideToIgnite.getX() + 0.5,
                sideToIgnite.getY() + 0.5,
                sideToIgnite.getZ() + 0.5,
                SoundEvents.ITEM_FLINTANDSTEEL_USE,
                SoundCategory.BLOCKS,
                1.0F,
                0.8F + rand.nextFloat() * 0.4F,
                false
        );
    }

    protected float calcIgnitionThreshold(World world, BlockPos pos, Block block, EnumFacing side) {
        int flammability = block.getFlammability(world, pos, side);
        return flammability == 0 ? Float.MAX_VALUE : 15.0F / flammability;
    }
}
