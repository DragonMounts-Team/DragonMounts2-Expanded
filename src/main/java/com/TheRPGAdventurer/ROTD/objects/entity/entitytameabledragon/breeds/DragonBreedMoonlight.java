package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathNode;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.DragonBreathHelper;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.effects.IceBreathFX;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundEffectName;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundState;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DragonBreedMoonlight extends DragonBreed {

	DragonBreedMoonlight() {
		super("moonlight", 0x2c427c);
		
		setHabitatBlock(Blocks.DAYLIGHT_DETECTOR_INVERTED);
		setHabitatBlock(Blocks.BLUE_GLAZED_TERRACOTTA);
	}

	@Override
	public void onEnable(EntityTameableDragon dragon) {}

	@Override
	public void onDisable(EntityTameableDragon dragon) {}

	@Override
	public void onDeath(EntityTameableDragon dragon) {}
	
	@Override
	public void onLivingUpdate(EntityTameableDragon dragon) {
		if(dragon.posY > dragon.world.getHeight() && !dragon.world.isDaytime()) doParticles(dragon);
	}
	
    private void doParticles(EntityTameableDragon dragon) {
        if (!dragon.isEgg() && !dragon.isBaby()) {
	        float s = dragon.getScale() * 1.2f;
	        double x = dragon.posX + (rand.nextDouble() - 0.5) * (dragon.width - 0.65) * s;
	        double y = dragon.posY + (rand.nextDouble() - 0.5) * dragon.height * s;
	        double z = dragon.posZ + (rand.nextDouble() - 0.5) * (dragon.width - 0.65) * s;
		        
	        dragon.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x, y, z, 0, 0, 0);
        }
    }

	@Override
	public void continueAndUpdateBreathing(DragonBreathHelper helper, World world, Vec3d origin, Vec3d endOfLook, BreathNode.Power power) {
		helper.getBreathAffectedAreaIce().continueBreathing(world, origin, endOfLook, power);
		helper.getBreathAffectedAreaIce().updateTick(world);
	}

	@Override
	public void spawnClientNodeEntity(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
		world.spawnEntity(new IceBreathFX(world, position, direction, power, partialTicks));
	}

	@Override
	public SoundEffectName getBreathWeaponSoundEffect(DragonLifeStage stage, SoundState state) {
		return state.ice;// why?
	}

	@Override
	public EnumParticleTypes getSneezeParticle() {
		return null;
	}
}
