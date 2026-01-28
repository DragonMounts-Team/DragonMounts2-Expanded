package net.dragonmounts.food;

import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ContainerFood implements IDragonFoodCapable {
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
}
