/*
 ** 2013 July 20
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.goal.target;

import com.google.common.base.Predicate;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.passive.EntityAnimal;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonHuntTargetGoal extends EntityAITargetNonTamed<EntityAnimal> {
    private final TameableDragonEntity dragon;

    public DragonHuntTargetGoal(TameableDragonEntity dragon, boolean mustSee, Predicate<? super EntityAnimal> filter) {
        super(dragon, EntityAnimal.class, mustSee, filter);
        this.dragon = dragon;
    }

    @Override
    public boolean shouldExecute() {
        return this.dragon.lifeStageHelper.isOldEnough(DragonLifeStage.FLEDGLING) && this.dragon.getHunger() < 50 && super.shouldExecute();
    }
}
