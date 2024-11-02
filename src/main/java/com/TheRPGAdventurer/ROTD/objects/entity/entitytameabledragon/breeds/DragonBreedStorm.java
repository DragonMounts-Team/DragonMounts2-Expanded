package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathNode;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.DragonBreathHelper;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.effects.HydroBreathFX;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundEffectName;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundState;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class DragonBreedStorm extends DragonBreed {

	DragonBreedStorm() {
		super("storm", 0xf5f1e9);

	}

	@Override
	public void onEnable(EntityTameableDragon dragon) {

		
	}

	@Override
	public void onDisable(EntityTameableDragon dragon) {

		
	}

	@Override
	public void onDeath(EntityTameableDragon dragon) {

		
	}
	@Override
	public void continueAndUpdateBreathing(DragonBreathHelper helper, World world, Vec3d origin, Vec3d endOfLook, BreathNode.Power power) {
		helper.getbreathAffectedAreaHydro().continueBreathing(world, origin, endOfLook, power);
		helper.getbreathAffectedAreaHydro().updateTick(world);
    }

	@Override
	public void spawnClientNodeEntity(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
		world.spawnEntity(new HydroBreathFX(world, position, direction, power, partialTicks));
	}
	
//	@Override
//	public boolean isInfertile() {
//		return true;
//	}

	@Override
	public SoundEffectName getBreathWeaponSoundEffect(DragonLifeStage stage, SoundState state) {
		return state.ice;// why?
	}

	@Override
	public void onLivingUpdate(EntityTameableDragon dragon) {
		EntityLivingBase target = dragon.getAttackTarget();
		PotionEffect watereffect = new PotionEffect(MobEffects.WATER_BREATHING, 20*10);
    	if (!dragon.isPotionActive(watereffect.getPotion()) && dragon.isInWater()) { // If the Potion isn't currently active,
    		dragon.addPotionEffect(watereffect); // Apply a copy of the PotionEffect to the player
		}   
    	
    	Random random = new Random();
    	boolean shouldelectrecute = target != null && target.isEntityAlive();
    	
    	if(target instanceof EntityPlayer) {
    		EntityPlayer playertarget = (EntityPlayer) target;
    		if(playertarget.capabilities.isCreativeMode) {
    			shouldelectrecute = false;
    		}
    	}
    	
    	if(shouldelectrecute) {
    		  if(random.nextInt(70) == 1 && dragon.world.isRaining()) {
    		     dragon.world.addWeatherEffect(new EntityLightningBolt(target.world, target.posX, target.posY, target.posZ, false)); 		   
    		}
    	} 
    	doParticles(dragon);
	}
	
    private void doParticles(EntityTameableDragon dragon) {
        if (!dragon.isEgg() && !dragon.isBaby()) {
	        float s = dragon.getScale() * 1.2f;
	        for (double x1 = 0; x1 < s; ++x1) {
		        double x = dragon.posX + (rand.nextDouble() - 0.5) * (dragon.width - 0.65) * s;
		        double y = dragon.posY + (rand.nextDouble() - 0.5) * dragon.height * s;
		        double z = dragon.posZ + (rand.nextDouble() - 0.5) * (dragon.width - 0.65) * s;
		        
		        dragon.world.spawnParticle(EnumParticleTypes.DRIP_WATER, x, y - 1, z, 0, 0, 0);
	        }
        }
    }
	
	@Override
	public EnumParticleTypes getSneezeParticle() {
		return null;
	}
	
}
