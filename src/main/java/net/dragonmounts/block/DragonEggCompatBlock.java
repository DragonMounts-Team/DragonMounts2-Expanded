package net.dragonmounts.block;

import net.dragonmounts.compat.DragonTypeCompat;
import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.registry.DragonType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class DragonEggCompatBlock extends HatchableDragonEggBlock {
    private static final PropertyEnum<DragonTypeCompat> TYPE = PropertyEnum.create("breed", DragonTypeCompat.class);

    public DragonEggCompatBlock() {
        super(DragonTypes.ENDER);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        DragonTypeCompat[] types = DragonTypeCompat.values();
        return meta < 0 || meta >= types.length
                ? DMBlocks.ENDER_DRAGON_EGG.getDefaultState()
                : types[meta].type.getInstance(HatchableDragonEggBlock.class, DMBlocks.ENDER_DRAGON_EGG).getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {}

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public DragonType getDragonType(int meta) {
        DragonTypeCompat[] types = DragonTypeCompat.values();
        return meta < 0 || meta >= types.length ? this.type : types[meta].type;
    }

}
