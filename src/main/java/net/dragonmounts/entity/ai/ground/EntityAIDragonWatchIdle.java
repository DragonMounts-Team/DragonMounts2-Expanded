/*
 ** 2012 October 26
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.ai.ground;

import net.dragonmounts.entity.TameableDragonEntity;

import net.minecraft.entity.ai.EntityAILookIdle;
/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityAIDragonWatchIdle extends EntityAILookIdle {

    private TameableDragonEntity dragon;

    public EntityAIDragonWatchIdle(TameableDragonEntity par1EntityLiving) {
        super(par1EntityLiving);
        this.dragon = par1EntityLiving;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        if(dragon.getControllingPlayer()!=null) {
            return  false;
        }
        return super.shouldExecute();
    }
}
