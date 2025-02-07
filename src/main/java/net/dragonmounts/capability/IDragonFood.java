package net.dragonmounts.capability;

import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public interface IDragonFood {
    IDragonFood EMPTY = (
            dragon,
            player,
            relation,
            stack,
            hand
    ) -> false;

    default boolean isBreedingItem(TameableDragonEntity dragon, ItemStack stack) {
        return false;
    }

    boolean tryFeed(TameableDragonEntity dragon, EntityPlayer player, Relation relation, ItemStack stack, EnumHand hand);
}
