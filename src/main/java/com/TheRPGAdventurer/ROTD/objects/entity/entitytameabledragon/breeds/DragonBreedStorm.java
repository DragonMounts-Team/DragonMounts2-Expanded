package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundEffectName;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundState;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;

public class DragonBreedStorm extends DragonBreedWater {

	DragonBreedStorm() {
		super("storm", 0xf5f1e9);
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
		super.onLivingUpdate(dragon);
		EntityLivingBase target = dragon.getAttackTarget();
		if (target != null && target.isEntityAlive() && (!(target instanceof EntityPlayer) || !((EntityPlayer) target).capabilities.isCreativeMode) && dragon.world.isRaining() && dragon.world.rand.nextInt(70) == 0) {
			target.world.addWeatherEffect(new EntityLightningBolt(target.world, target.posX, target.posY, target.posZ, false));
		}
	}

	@Override
	public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
		return dragon.isMale() ? EnumItemBreedTypes.STORM : EnumItemBreedTypes.STORM2;
	}
}
