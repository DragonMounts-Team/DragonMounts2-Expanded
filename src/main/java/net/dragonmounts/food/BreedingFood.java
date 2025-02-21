package net.dragonmounts.food;

import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class BreedingFood extends CommonFood {
    public BreedingFood(int level, int growth, float health, float taming) {
        super(level, growth, health, taming);
    }

    @Override
    public boolean isBreedingItem(TameableDragonEntity dragon, ItemStack stack) {
        return true;
    }

    @Override
    public boolean tryFeed(TameableDragonEntity dragon, EntityPlayer player, Relation relation, ItemStack stack, EnumHand hand) {
        if (super.tryFeed(dragon, player, relation, stack, hand)) {
            if (!dragon.world.isRemote && dragon.isTamed() && DragonLifeStage.ADULT == dragon.lifeStageHelper.getLifeStage() && !dragon.isInLove()) {
                dragon.setInLove(player);
            }
            return true;
        }
        return false;
    }
}
