package net.dragonmounts.block;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.compat.DragonTypeCompat;
import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.BlockProperties;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class DragonEggCompatBlock extends HatchableDragonEggBlock {
    private static final PropertyEnum<DragonTypeCompat> TYPE = PropertyEnum.create("breed", DragonTypeCompat.class);
    public static final String IDENTIFIER = DragonMountsTags.MOD_ID + ":dragon_egg";
    public static final DragonEggCompatBlock INSTANCE = new DragonEggCompatBlock();

    private DragonEggCompatBlock() {
        super(DragonTypes.ENDER, new BlockProperties()
                .setSoundType(SoundType.STONE)
                .setHardness(0)
                .setResistance(30)
                .setLightLevel(0.125F)
                .setCreativeTab(DMItemGroups.BLOCKS)
        );
        this.setRegistryName(IDENTIFIER).setTranslationKey(HatchableDragonEggBlock.TRANSLATION_KEY);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public void updateTick(World level, BlockPos pos, IBlockState state, Random rand) {
        level.setBlockState(pos, this.getStateFromMeta(this.getMetaFromState(state)), 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDragonType(meta).getInstance(HatchableDragonEggBlock.class, DMBlocks.ENDER_DRAGON_EGG).getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs item, NonNullList<ItemStack> items) {}

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public DragonType getDragonType(int meta) {
        return DragonTypeCompat.byId(meta);
    }
}
