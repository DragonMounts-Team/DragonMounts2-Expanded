package top.dragonmounts.objects.entity.entitytameabledragon.breeds;

import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import top.dragonmounts.objects.items.EnumItemBreedTypes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class DragonBreedSunlight extends DragonBreed {

	DragonBreedSunlight() {
		super("sunlight", 0Xffde00);
		
		setHabitatBlock(Blocks.DAYLIGHT_DETECTOR);
		setHabitatBlock(Blocks.GLOWSTONE);
		setHabitatBlock(Blocks.YELLOW_GLAZED_TERRACOTTA);
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
		if (dragon.posY > level.getHeight() + 8 && dragon.world.isDaytime() && dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
			float s = dragon.getScale() * 1.2f;
			float h = dragon.height * s;
			float f = (dragon.width - 0.65F) * s;
			for (int i = -2; i < s; ++i) {
				level.spawnParticle(
						EnumParticleTypes.CRIT,
						dragon.posX + (rand.nextDouble() - 0.5) * f,
						dragon.posY + (rand.nextDouble() - 0.5) * h,
						dragon.posZ + (rand.nextDouble() - 0.5) * f,
						0,
						0,
						0
				);
			}
		}
	}

	@Override
	public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
		return dragon.isMale() ? EnumItemBreedTypes.SUNLIGHT : EnumItemBreedTypes.SUNLIGHT2;
	}
}
