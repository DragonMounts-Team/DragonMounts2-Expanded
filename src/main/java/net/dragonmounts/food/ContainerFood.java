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

public class ContainerFood implements ICapabilityProvider, IDragonFood {
    public final IDragonFood food;
    public final Item container;
    public final int meta;

    public ContainerFood(IDragonFood food, Item container, int meta) {
        this.container = container;
        this.food = food;
        this.meta = meta;
    }

    @Override
    public boolean tryFeed(TameableDragonEntity dragon, EntityPlayer player, Relation relation, ItemStack stack, EnumHand hand) {
        if (this.food.tryFeed(dragon, player, relation, stack, hand)) {
            if (dragon.world.isRemote) return true;
            ItemStack container = new ItemStack(this.container, 1, this.meta);
            if (!player.inventory.addItemStackToInventory(container) && !player.capabilities.isCreativeMode) {
                player.dropItem(container, false);
            }
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
