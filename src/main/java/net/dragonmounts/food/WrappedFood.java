package net.dragonmounts.food;

import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static net.dragonmounts.capability.DMCapabilities.DRAGON_FOOD;

public class WrappedFood implements ICapabilityProvider, IDragonFood {
    public final IDragonFood food;
    public final Item wrapper;
    public final int meta;

    public WrappedFood(IDragonFood food, Item wrapper, int meta) {
        this.wrapper = wrapper;
        this.food = food;
        this.meta = meta;
    }

    @Override
    public boolean tryFeed(TameableDragonEntity dragon, EntityPlayer player, Relation relation, ItemStack stack, EnumHand hand) {
        if (this.food.tryFeed(dragon, player, relation, stack, hand)) {
            if (dragon.world.isRemote || player.capabilities.isCreativeMode) return true;
            player.addItemStackToInventory(new ItemStack(this.wrapper, 1, this.meta));
            return true;
        }
        return false;
    }

    @Override
    public boolean hasCapability(@Nullable Capability<?> capability, @Nullable EnumFacing facing) {
        return DRAGON_FOOD == capability;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nullable Capability<T> capability, @Nullable EnumFacing facing) {
        //noinspection DataFlowIssue
        return DRAGON_FOOD == capability ? DRAGON_FOOD.cast(this) : null;
    }
}
