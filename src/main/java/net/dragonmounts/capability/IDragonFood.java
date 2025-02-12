package net.dragonmounts.capability;

import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
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

    static boolean isSatiated(TameableDragonEntity dragon) {
        return DragonLifeStage.ADULT == dragon.getLifeStageHelper().getLifeStage() && dragon.getHealth() >= dragon.getMaxHealth() && dragon.getHunger() >= 100;
    }
}
