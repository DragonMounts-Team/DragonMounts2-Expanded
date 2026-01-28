package net.dragonmounts.food;

import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class CommonFood implements IDragonFoodCapable {
    public final int level;
    public final int growth;
    public final float health;
    public final float taming;

    public CommonFood(int level, int growth, float health, float taming) {
        this.level = level;
        this.growth = growth;
        this.health = health;
        this.taming = taming;
    }

    @Override
    public boolean tryFeed(TameableDragonEntity dragon, EntityPlayer player, Relation relation, ItemStack stack, EnumHand hand) {
        if (dragon.world.isRemote) {
            if (dragon.isTamed()) {
                if (!relation.isTrusted) {
                    relation.onDeny(player);
                    return false;
                }
                if (IDragonFood.isSatiated(dragon)) return false;
            }
            dragon.consumeFood(stack, this.level, this.growth);
            return true;
        }
        if (Relation.STRANGER == relation) {
            dragon.tryTame(player, this.taming);
        } else if (relation.isTrusted) {
            if (IDragonFood.isSatiated(dragon)) return false;
        } else {
            relation.onDeny(player);
            return false;
        }
        dragon.consumeFood(stack, this.level, this.growth);
        dragon.heal(this.health);
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        player.swingArm(hand);
        return true;
    }
}
