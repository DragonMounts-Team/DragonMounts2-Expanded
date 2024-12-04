package net.dragonmounts.block;

import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.registry.DragonType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

public class DragonEggBlockCompat extends HatchableDragonEggBlock {
    private static final PropertyEnum<Type> TYPE = PropertyEnum.create("breed", Type.class);

    public DragonEggBlockCompat() {
        super(DragonTypes.ENDER);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        Type[] types = Type.values();
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
        Type[] types = Type.values();
        return meta < 0 || meta >= types.length ? this.type : types[meta].type;
    }

    private enum Type implements IStringSerializable {
        AETHER(DragonTypes.AETHER),
        FIRE(DragonTypes.FIRE),
        FOREST(DragonTypes.FOREST),
        SYLPHID(DragonTypes.WATER),
        ICE(DragonTypes.ICE),
        END(DragonTypes.ENDER),
        NETHER(DragonTypes.NETHER),
        SKELETON(DragonTypes.SKELETON),
        WITHER(DragonTypes.WITHER),
        ENCHANT(DragonTypes.ENCHANT),
        SUNLIGHT(DragonTypes.SUNLIGHT),
        STORM(DragonTypes.STORM),
        ZOMBIE(DragonTypes.ZOMBIE),
        TERRA(DragonTypes.TERRA),
        MOONLIGHT(DragonTypes.MOONLIGHT);

        public final DragonType type;
        public final String identifier;

        Type(DragonType type) {
            this.type = type;
            this.identifier = this.name().toLowerCase();
        }

        @Override
        public String getName() {
            return this.identifier;
        }
    }
}
