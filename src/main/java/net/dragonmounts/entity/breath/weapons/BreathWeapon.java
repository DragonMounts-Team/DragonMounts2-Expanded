package net.dragonmounts.entity.breath.weapons;

import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathAffectedBlock;
import net.dragonmounts.entity.breath.BreathAffectedEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by TGG on 5/08/2015.
 * <p>
 * Models the effects of a breathweapon on blocks and entities
 * Usage:
 * 1) Construct with a parent dragon
 * 2) affectBlock() to apply an area of effect to the given block (eg set fire to it)
 * 3) affectEntity() to apply an area of effect to the given entity (eg damage it)
 * <p>
 * Currently does fire only.  Intended to be subclassed later on for different weapon types.
 */
public class BreathWeapon {

    protected TameableDragonEntity dragon;

    protected float FIRE_DAMAGE = 0.7F;
    protected float ENDER_DAMAGE = 0.9F;
    protected float HYDRO_DAMAGE = 0.7F;
    protected float ICE_DAMAGE = 0.7F;
    protected float NETHER_DAMAGE = 0.9F;
    protected float POISON_DAMAGE = 0.6F;
    protected float WITHER_DAMAGE = 0.6F;

    public BreathWeapon(TameableDragonEntity dragon) {
        this.dragon = dragon;
    }

    /**
     * Used this to be compatible for Biomes O Plenty, BOP Author made a switch statement on his/her blocks
     * Instead of programming the blocks one by one. I dunno if that was allowed
     */
    public int getFlammabilityCompat(Block block, World world, BlockPos pos, EnumFacing facing) {
        try {
            return block.getFlammability(world, pos, facing);
        } catch (IllegalArgumentException e) {
            return 3;
        }
    }

    /**
     * if the hitDensity is high enough, manipulate the block (eg set fire to it)
     *
     * @param world
     * @param blockPosition     the world [x,y,z] of the block
     * @param currentHitDensity
     * @return the updated block hit density
     */
    public BreathAffectedBlock affectBlock(World world, Vec3i blockPosition,
                                           BreathAffectedBlock currentHitDensity) {
        checkNotNull(world);
        checkNotNull(blockPosition);
        checkNotNull(currentHitDensity);

        BlockPos pos = new BlockPos(blockPosition);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        Random rand = new Random();

        // Flammable blocks: set fire to them once they have been exposed enough.  After sufficient exposure, destroy the
        //   block (otherwise -if it's raining, the burning block will keep going out)
        // Non-flammable blocks:
        // 1) liquids (except lava) evaporate
        // 2) If the block can be smelted (eg sand), then convert the block to the smelted version
        // 3) If the block can't be smelted then convert to lava

        if (DragonMountsConfig.canFireBreathAffectBlocks) {
            float max = 0.0F;
            for (EnumFacing facing : EnumFacing.values()) {
                float density = currentHitDensity.getHitDensity(facing);
                if (density > max) {
                    max = density;
                }
                int flammability = getFlammabilityCompat(block, world, pos, facing);
                if (flammability > 0) {
                    float thresholdForIgnition = convertFlammabilityToHitDensityThreshold(flammability);
                    //float thresholdForDestruction = thresholdForIgnition * 10;
                    BlockPos sideToIgnite = pos.offset(facing);
                    if (currentHitDensity.getHitDensity(facing) >= thresholdForIgnition && world.isAirBlock(sideToIgnite)) {
                        burnBlocks(sideToIgnite, rand, 22, world);
                        //    if (densityOfThisFace >= thresholdForDestruction && state.getBlockHardness(world, pos) != -1 && DragonMountsConfig.canFireBreathAffectBlocks) {
                        //   world.setBlockToAir(pos);
                    }
                }
            }
            if (max > 0.5F) {
                this.smeltBlock(world, pos, state);
            }
        }
        return new BreathAffectedBlock();  // reset to zero
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

    protected void burnBlocks(BlockPos sideToIgnite, Random rand, int factor, World world) {
        if (rand.nextInt(2500) < factor) {
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
    }

    protected static class BlockBurnProperties {
        public IBlockState burnResult = null;  // null if no effect
        public float threshold;
    }

    /**
     * if sourceBlock can be smelted, return the smelting result as a block
     *
     * @param sourceBlock
     * @return the smelting result, or null if none
     */
    private static boolean getSmeltingResult(IBlockState sourceBlock, World world, BlockPos pos) {
        Block block = sourceBlock.getBlock();
        Item itemFromBlock = Item.getItemFromBlock(block);
        ItemStack itemStack;
        if (itemFromBlock != null && itemFromBlock.getHasSubtypes()) {
            int metadata = block.getMetaFromState(sourceBlock);
            itemStack = new ItemStack(itemFromBlock, 1, metadata);
        } else {
            itemStack = new ItemStack(itemFromBlock);
        }

        ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(itemStack);
        if (smeltingResult != null) {
            Block smeltedResultBlock = Block.getBlockFromItem(smeltingResult.getItem());
            if (smeltedResultBlock != null) {
                IBlockState iBlockStateSmelted = world.getBlockState(pos);
                return iBlockStateSmelted == smeltedResultBlock.getStateFromMeta(smeltingResult.getMetadata());
            }
        }
        return false;
    }

    /**
     * if sourceBlock is a liquid or snow that can be molten or vaporised, return the result as a block
     *
     * @param sourceBlock
     * @return the vaporised result, or null if none
     */
    private static boolean getVaporisedLiquidResult(IBlockState sourceBlock, World world, BlockPos pos) {
        Block block = sourceBlock.getBlock();
        Material material = block.getMaterial(sourceBlock);

        if (material == Material.WATER) {
            return world.setBlockState(pos, Blocks.AIR.getDefaultState());
        } else if (material == Material.SNOW || material == Material.ICE) {
            final int SMALL_LIQUID_AMOUNT = 4;
            return world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, SMALL_LIQUID_AMOUNT));
        } else if (material == Material.PACKED_ICE || material == Material.CRAFTED_SNOW) {
            final int LARGE_LIQUID_AMOUNT = 1;
            return world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, LARGE_LIQUID_AMOUNT));
        }
        return false;
    }

    /**
     * if sourceBlock is a block that can be melted to lave, return the result as a block
     *
     * @param sourceBlock
     * @return the molten lava result, or null if none
     */
    private static boolean getMoltenLavaResult(IBlockState sourceBlock, World world, BlockPos pos) {
        Block block = sourceBlock.getBlock();
        Material material = block.getMaterial(sourceBlock);

        if (material == Material.SAND || material == Material.CLAY
                || material == Material.GLASS || material == Material.IRON
                || material == Material.GROUND || material == Material.ROCK) {
            final int LARGE_LIQUID_AMOUNT = 1;
            return world.setBlockState(pos, Blocks.LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL, LARGE_LIQUID_AMOUNT));
        }
        return false;
    }

    /**
     * if sourceBlock is a block that isn't flammable but can be scorched / changed, return the result as a block
     *
     * @param sourceBlock
     * @return the scorched result, or null if none
     */
    private static boolean getScorchedResult(IBlockState sourceBlock, World world, BlockPos pos) {
        Block block = sourceBlock.getBlock();
        Material material = block.getMaterial(sourceBlock);

        if (material == Material.GRASS) {
            return world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        }
        return false;
    }

    protected BreathAffectedEntity triggerExceptions(Entity entity, BreathAffectedEntity currentHitDensity) {
        if (entity == dragon.getRidingCarriage() && dragon.getRidingCarriage() != null) {
            if (dragon.getRidingCarriage().getRidingEntity() != null
                    && dragon.getRidingCarriage().getRidingEntity() == entity) {
                return null;
            }
        }
        return currentHitDensity;
    }

    protected BreathAffectedEntity triggerDamageExceptions(Entity entity, float DAMAGE_PER_HIT_DENSITY, Integer entityID, BreathAffectedEntity currentHitDensity) {
        if (entityID == dragon.getEntityId()) return null;

        if (entity == dragon.getRidingCarriage() && dragon.getRidingCarriage() != null) {
            if (dragon.getRidingCarriage().getRidingEntity() != null
                    && entity == dragon.getRidingCarriage().getRidingEntity()) {
                return null;
            }
        }

        if (entity instanceof EntityTameable) {
            if (((EntityTameable) entity).isTamed()) {
                return null;
            } else {
                entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), DAMAGE_PER_HIT_DENSITY);
            }
        }

        return currentHitDensity;

    }

    protected BreathAffectedEntity triggerDamageExceptionsForFire(Entity entity, Integer entityID, float DAMAGE_PER_HIT_DENSITY, BreathAffectedEntity currentHitDensity) {
        triggerDamageExceptions(entity, DAMAGE_PER_HIT_DENSITY, entityID, currentHitDensity);
        if (dragon.getRidingEntity() != entity && !dragon.isPassenger(entity)) {
            entity.setFire((4));
        } else if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed()) {
            entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), 0);
        } else if (entity instanceof EntityLivingBase) {
            if (((EntityLivingBase) entity).isPotionActive(MobEffects.FIRE_RESISTANCE)) {
                return null;
            } else {
                entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), DAMAGE_PER_HIT_DENSITY);
            }
        } else if (dragon.isBeingRidden()) {
            if (dragon.isPassenger(entity)) return null;
        }

        return currentHitDensity;

    }

    private HashMap<Block, BlockBurnProperties> blockBurnPropertiesCache = new HashMap<Block, BlockBurnProperties>();

    /**
     * if the hitDensity is high enough, manipulate the entity (eg set fire to it, damage it)
     * A dragon can't be damaged by its own breathweapon;
     * If the "orbholder immune" option is on, and the entity is a player holding a dragon orb, ignore damage.
     *
     * @param world
     * @param entityID          the ID of the affected entity
     * @param currentHitDensity the hit density
     * @return the updated hit density; null if entity dead, doesn't exist, or otherwise not affected
     */
    public BreathAffectedEntity affectEntity(World world, int entityID, BreathAffectedEntity currentHitDensity) {
        checkNotNull(world);
        checkNotNull(currentHitDensity);

        Entity entity = world.getEntityByID(entityID);
        if (!(entity instanceof EntityLivingBase) || entity.isDead) return null;

        float hitDensity = currentHitDensity.getHitDensity();
        final float DAMAGE_PER_HIT_DENSITY = FIRE_DAMAGE * hitDensity;

        triggerDamageExceptionsForFire(entity, entityID, DAMAGE_PER_HIT_DENSITY, currentHitDensity);
        entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), DAMAGE_PER_HIT_DENSITY);

        return currentHitDensity;
    }

    /**
     * returns the hitDensity threshold for the given block flammability (0 - 300 as per Block.getFlammability)
     *
     * @param flammability
     * @return the hit density threshold above which the block catches fire
     */
    protected float convertFlammabilityToHitDensityThreshold(int flammability) {
        checkArgument(flammability >= 0 && flammability <= 300);
        if (flammability == 0) return Float.MAX_VALUE;
        // typical values for items are 5 (coal, logs), 20 (gates etc), 60 - 100 for leaves & flowers & grass
        // want: leaves & flowers to burn instantly; gates to take ~1 second at full power, coal / logs to take ~3 seconds
        // hitDensity of 1 is approximately 1-2 ticks of full exposure from a single beam, so 3 seconds is ~30
        return 15.0F / flammability;
    }

}
