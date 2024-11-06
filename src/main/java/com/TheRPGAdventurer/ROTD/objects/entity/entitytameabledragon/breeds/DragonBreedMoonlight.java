package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathNode;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.effects.IceBreathFX;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundEffectName;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundState;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponAether;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

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
		World level = dragon.world;
		if (dragon.posY > level.getHeight() && !level.isDaytime() && dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
			Random random = this.rand;
			float s = dragon.getScale() * 1.2f;
			float f = (dragon.width - 0.65F) * s;
			level.spawnParticle(
					EnumParticleTypes.FIREWORKS_SPARK,
					dragon.posX + (random.nextDouble() - 0.5) * f,
					dragon.posY + (random.nextDouble() - 0.5) * dragon.height * s,
					dragon.posZ + (random.nextDouble() - 0.5) * f,
					0,
					0,
					0
			);
		}
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

	@Override
	public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
		return new BreathWeaponAether(dragon);
	}

	@Override
	public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
		return dragon.isMale() ? EnumItemBreedTypes.MOONLIGHT_MALE : EnumItemBreedTypes.MOONLIGHT;
	}
}
