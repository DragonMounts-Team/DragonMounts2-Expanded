/*
** 2016 MÃ¤rz 15
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.ai;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Random;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class EntityAIDragonBase extends EntityAIBase {
    protected TameableDragonEntity dragon;
    protected World world;
    protected Random random;
    protected EntityPlayer rider;

    public EntityAIDragonBase(TameableDragonEntity dragon) {
        this.dragon = dragon;
        this.world = dragon.world;
        this.random = dragon.getRNG();
    }
}
