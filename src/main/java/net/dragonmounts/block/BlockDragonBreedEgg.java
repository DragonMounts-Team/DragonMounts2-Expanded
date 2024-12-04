/*
** 2016 March 09
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package net.dragonmounts.block;

import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import net.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * Modified by WolfShotz <p>
 * 
 */
public class BlockDragonBreedEgg extends BlockDragonEgg {

    public static final PropertyEnum<EnumDragonBreed> BREED = PropertyEnum.create("breed", EnumDragonBreed.class);
    public static BlockDragonBreedEgg DRAGON_BREED_EGG = new BlockDragonBreedEgg();
    
    public BlockDragonBreedEgg() {
        setTranslationKey("dragonEgg");
        setHardness(0);
        setResistance(30);
        setSoundType(SoundType.WOOD);
        setLightLevel(0.125f);
        setCreativeTab(DMItemGroups.MAIN);

    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BREED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(BREED, EnumDragonBreed.byMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BREED).meta;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        Arrays.stream(EnumDragonBreed.values()).forEach(breed -> items.add(new ItemStack(this, 1, breed.meta)));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }
    
    @Override 
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.checkFall(worldIn, pos, state);
    }
    
    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer player) {
    	return;
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (DragonMountsConfig.isDisableBlockOverride()) return false;
    	if (worldIn.provider.getDimensionType() == DimensionType.THE_END) {
            if (!worldIn.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation("egg.cantHatchEnd.DragonMounts"), true);
            }
    		return false;
        } else if (worldIn.isRemote) {
            worldIn.playSound(player, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.PLAYERS, 1, 1);
            return true;
        }

    	EntityTameableDragon dragon = new EntityTameableDragon(worldIn);
        //dragon.setBreedType(state.getValue(BlockDragonBreedEgg.BREED));TODO: use setVariant
    	worldIn.setBlockToAir(pos); // Set to air AFTER setting breed type
    	dragon.getLifeStageHelper().setLifeStage(DragonLifeStage.EGG);
    	dragon.getReproductionHelper().setBreeder(player);
        dragon.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        worldIn.spawnEntity(dragon);
        return true;
    }

    private void checkFall(World worldIn, BlockPos pos, IBlockState state) {
        if (pos.getY() < 0) return;
        BlockPos bottom = pos.down();
        if (worldIn.isAirBlock(bottom) && BlockFalling.canFallThrough(worldIn.getBlockState(bottom))) {
            if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                worldIn.spawnEntity(new EntityFallingBlock(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, state));
            } else {
                worldIn.setBlockToAir(pos);
                BlockPos blockpos = pos;
                while (worldIn.isAirBlock(blockpos) && BlockFalling.canFallThrough(worldIn.getBlockState(blockpos))) {
                    if (blockpos.getY() <= 0) return;
                    blockpos = blockpos.down();
                }
                worldIn.setBlockState(blockpos, state, 2);
            }
        }
    }
}