package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.ai;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;

public class EntityAIDragonEgg extends EntityAIDragonBase {
    public EntityAIDragonEgg(EntityTameableDragon dragon) {
        super(dragon);
        setMutexBits(0xffffffff);
    }

    @Override
    public boolean shouldExecute() {
        return this.dragon.isEgg();
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }
}
