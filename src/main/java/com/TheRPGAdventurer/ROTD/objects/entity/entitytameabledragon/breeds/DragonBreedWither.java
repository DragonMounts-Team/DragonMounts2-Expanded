package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds;

import com.TheRPGAdventurer.ROTD.inits.ModSounds;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathNode;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.DragonBreathHelper;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.effects.WitherBreathFX;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;


public class DragonBreedWither extends DragonBreed {

    DragonBreedWither() {
        super("wither", 0x50260a);
        
        setImmunity(DamageSource.MAGIC);
        setImmunity(DamageSource.HOT_FLOOR);
        setImmunity(DamageSource.LIGHTNING_BOLT);
        setImmunity(DamageSource.WITHER);
        
    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {}

    @Override
    public void onDisable(EntityTameableDragon dragon) {}

    @Override
    public void onDeath(EntityTameableDragon dragon) {}

    public SoundEvent getLivingSound(EntityTameableDragon dragon) {
        if (dragon.isBaby()) {
            return ModSounds.ENTITY_DRAGON_HATCHLING_GROWL;
        } else {
            return ModSounds.ENTITY_NETHER_DRAGON_GROWL;
        }
    }
    
	@Override
	public boolean canChangeBreed() {
		return false;
	}
	
	@Override
    public void continueAndUpdateBreathing(DragonBreathHelper helper, World world, Vec3d origin, Vec3d endOfLook, BreathNode.Power power) {
        helper.getbreathAffectedAreaWither().continueBreathing(world, origin, endOfLook, power);
        helper.getbreathAffectedAreaWither().updateTick(world);
    }

    @Override
    public void spawnClientNodeEntity(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new WitherBreathFX(world, position, direction, power, partialTicks));
    }

	@Override
	public void onLivingUpdate(EntityTameableDragon dragon) {
		World world = dragon.world;
		if(dragon.isUsingBreathWeapon()) {
			if (world instanceof WorldServer && !dragon.isDead && !dragon.isEgg()) {
				((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, dragon.posX,
						dragon.posY + dragon.getEyeHeight(), dragon.posZ, 1, 0.5D, 0.25D, 0.5D, 0.0D);
			}
		}
	}
	
//	@Override
//	public boolean isInfertile() {
//		return true;
//	}
	
	@Override
	public EnumParticleTypes getSneezeParticle() {
		return null;
	}

}
	
