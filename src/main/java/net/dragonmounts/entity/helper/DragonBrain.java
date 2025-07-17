/*
 ** 2016 March 12
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.helper;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.ai.*;
import net.dragonmounts.entity.ai.air.EntityAIDragonFlight;
import net.dragonmounts.entity.ai.air.EntityAIDragonFollowOwnerElytraFlying;
import net.dragonmounts.entity.ai.ground.EntityAIDragonFollowOwner;
import net.dragonmounts.entity.ai.ground.EntityAIDragonHunt;
import net.dragonmounts.entity.ai.ground.EntityAIDragonWatchIdle;
import net.dragonmounts.entity.ai.ground.EntityAIDragonWatchLiving;
import net.dragonmounts.util.ClassPredicate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonBrain extends DragonHelper {
    public DragonBrain(TameableDragonEntity dragon) {
        super(dragon);
    }

    public void setAvoidsWater(boolean avoidWater) {
        PathNavigate navigator = dragon.getNavigator();
        if (navigator instanceof PathNavigateGround) {
            ((PathNavigateGround) navigator).setCanSwim(!avoidWater); // originally setAvoidsWater()
        }
    }

    public void updateAITasks() {
        TameableDragonEntity dragon = this.dragon;
        PathNavigate navigator = dragon.getNavigator();
        // clear current navigation target
        navigator.clearPath();
        // only hatchlings are small enough for doors
        // (eggs don't move on their own anyway and are ignored)
        // guessed, based on EntityAIRestrictOpenDoor - break the door down, don't open it
        if (navigator instanceof PathNavigateGround) {
            ((PathNavigateGround) navigator).setEnterDoors(dragon.isBaby());
        }
        EntityAITasks tasks = dragon.tasks;

        // eggs don't have any tasks
        if (dragon.isEgg()) {
            Iterator<EntityAITasks.EntityAITaskEntry> iterator = tasks.taskEntries.iterator();
            while (iterator.hasNext()) {
                EntityAITasks.EntityAITaskEntry entry = iterator.next();
                if (entry.using) {
                    entry.using = false;
                    entry.action.resetTask();
                    tasks.executingTaskEntries.remove(entry);
                }
                iterator.remove();
            }
            return;
        }

        // mutex 1: movement
        // mutex 2: looking
        // mutex 4: special state
        tasks.addTask(0, new EntityAIDragonCatchOwner(dragon)); // mutex all
        tasks.addTask(1, new EntityAIDragonPlayerControl(dragon)); // mutex all
        tasks.addTask(2, dragon.getAISit()); // mutex 4+1
        tasks.addTask(4, new EntityAIMoveTowardsRestriction(dragon, 1)); // mutex 1
        tasks.addTask(6, new EntityAIDragonFlight(dragon, 1)); // mutex 1
        tasks.addTask(2, new EntityAISwimming(dragon)); // mutex 4
        tasks.addTask(7, new EntityAIAttackMelee(dragon, 1, true)); // mutex 2+1
        tasks.addTask(9, new EntityAIDragonFollowOwner(dragon, 1.25, 16, 12)); // mutex 2+1
        tasks.addTask(9, new EntityAIDragonFollowOwnerElytraFlying(dragon)); // mutex 2+1
        tasks.addTask(10, new EntityAIWander(dragon, 1)); // mutex 1
        tasks.addTask(11, new EntityAIDragonWatchIdle(dragon)); // mutex 2
        tasks.addTask(11, new EntityAIDragonWatchLiving(dragon, 16, 0.05f)); // mutex 2
        if (dragon.isBaby()) {
            tasks.addTask(5, new EntityAILeapAtTarget(dragon, 0.7F)); // mutex 1
            tasks.addTask(12, new EntityAIDragonFollowParent(dragon, 1.4f));
            tasks.addTask(6, new EntityAITempt(dragon, 0.75, false, OreDictionary.getOres("listAllfishraw").stream().map(ItemStack::getItem).collect(Collectors.toSet()))); // mutex 2+1
        }
        if (dragon.isAdult()) {
            tasks.addTask(5, new EntityAIDragonMate(dragon, 0.6)); // mutex 2+1
        }
        // mutex 1: generic targeting
        EntityAITasks targets = dragon.targetTasks;
        targets.addTask(5, new EntityAINearestAttackableTarget<>(dragon, EntityLiving.class, 10, false, true, IMob.VISIBLE_MOB_SELECTOR));
        targets.addTask(5, new EntityAIDragonHunt(dragon, false, new ClassPredicate<>(
                EntitySheep.class,
                EntityPig.class,
                EntityChicken.class,
                EntityRabbit.class,
                EntityLlama.class
        ))); // mutex 1
        targets.addTask(2, new EntityAIOwnerHurtByTarget(dragon)); // mutex 1
        targets.addTask(3, new EntityAIOwnerHurtTarget(dragon)); // mutex 1
        targets.addTask(4, new EntityAIDragonHurtByTarget(dragon)); // mutex 1
    }
}

