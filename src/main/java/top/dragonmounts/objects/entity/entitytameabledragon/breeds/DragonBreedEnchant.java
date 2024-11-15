package top.dragonmounts.objects.entity.entitytameabledragon.breeds;

import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import top.dragonmounts.objects.items.EnumItemBreedTypes;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.Random;

public class DragonBreedEnchant extends DragonBreed {

	DragonBreedEnchant() {
		super("enchant", 0x8359ae);
		
        setImmunity(DamageSource.MAGIC);
        setImmunity(DamageSource.HOT_FLOOR);
        setImmunity(DamageSource.LIGHTNING_BOLT);
        setImmunity(DamageSource.WITHER);
        
        setHabitatBlock(Blocks.BOOKSHELF);
        setHabitatBlock(Blocks.ENCHANTING_TABLE);
	}
	
	@Override
	public void onLivingUpdate(EntityTameableDragon dragon) {
		if (dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
			World level = dragon.world;
			Random random = this.rand;
			float s = dragon.getScale() * 1.2f;
			float h = dragon.height * s;
			float f = (dragon.width - 0.65F) * s;
			for (int i = -25; i < s; ++i) {
				double x = dragon.posX + (random.nextDouble() - 0.5) * f;
				double y = dragon.posY + (random.nextDouble() - 0.5) * h;
				double z = dragon.posZ + (random.nextDouble() - 0.5) * f;
				level.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, x, y, z, 0, 0, 0);
			}
		}
	}

	@Override
	public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
		return EnumItemBreedTypes.ENCHANT;
	}
}
