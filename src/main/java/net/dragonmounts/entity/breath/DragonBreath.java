package net.dragonmounts.entity.breath;

import net.dragonmounts.client.breath.impl.FlameBreathFX;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
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
    public abstract BreathAffectedBlock affectBlock(World level, long location, BreathAffectedBlock hit);

    public boolean canAffect(EntityLivingBase entity) {
        return this.dragon != entity && this.dragon.getRidingEntity() != entity && !this.dragon.isPassenger(entity);
    }

    public void affectEntity(World level, EntityLivingBase target, BreathAffectedEntity hit) {
        target.attackEntityFrom(DamageSource.causeMobDamage(this.dragon), this.damage * hit.getHitDensity());
    }

    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new FlameBreathFX(world, position, direction, power, partialTicks));
    }

    public SoundEvent getStartSound(DragonLifeStage stage) {
        switch (stage) {
            case ADULT:
                return DMSounds.DRAGON_BREATH_START_ADULT;
            case JUVENILE:
                return DMSounds.DRAGON_BREATH_START_JUVENILE;
            default:
                return DMSounds.DRAGON_BREATH_START_HATCHLING;
        }
    }

    public SoundEvent getLoopSound(DragonLifeStage stage) {
        switch (stage) {
            case ADULT:
                return DMSounds.DRAGON_BREATH_LOOP_ADULT;
            case JUVENILE:
                return DMSounds.DRAGON_BREATH_LOOP_JUVENILE;
            default:
                return DMSounds.DRAGON_BREATH_LOOP_HATCHLING;
        }
    }

    public SoundEvent getStopSound(DragonLifeStage stage) {
        switch (stage) {
            case ADULT:
                return DMSounds.DRAGON_BREATH_STOP_ADULT;
            case JUVENILE:
                return DMSounds.DRAGON_BREATH_STOP_JUVENILE;
            default:
                return DMSounds.DRAGON_BREATH_STOP_HATCHLING;
        }
    }
}
