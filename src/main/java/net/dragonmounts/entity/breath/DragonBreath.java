package net.dragonmounts.entity.breath;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by TGG on 5/08/2015.
 */
public abstract class DragonBreath {
    protected final TameableDragonEntity dragon;

    public final float damage;

    public DragonBreath(TameableDragonEntity dragon, float damage) {
        this.dragon = dragon;
        this.damage = damage;
    }

    /**
     * if the hitDensity is high enough, manipulate the block (eg set fire to it)
     * @return the updated block hit density
     */
    public abstract BreathAffectedBlock affectBlock(World level, BlockPos pos, BreathAffectedBlock hit);

    public boolean canAffect(EntityLivingBase entity) {
        return this.dragon != entity && this.dragon.getRidingEntity() != entity && !this.dragon.isPassenger(entity);
    }

    public void affectEntity(World level, EntityLivingBase target, BreathAffectedEntity hit) {
        target.attackEntityFrom(DamageSource.causeMobDamage(this.dragon), this.damage * hit.getHitDensity());
    }
}
