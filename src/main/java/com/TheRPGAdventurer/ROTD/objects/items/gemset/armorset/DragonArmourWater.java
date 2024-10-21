package com.TheRPGAdventurer.ROTD.objects.items.gemset.armorset;

import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;
import net.minecraft.inventory.EntityEquipmentSlot;

import static com.TheRPGAdventurer.ROTD.inits.DMArmorEffects.WATER_EFFECT;

public class DragonArmourWater extends DragonArmourBase {
	public DragonArmourWater(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String unlocalizedName) {
		super(materialIn, renderIndexIn, equipmentSlotIn, unlocalizedName, EnumItemBreedTypes.WATER, WATER_EFFECT);
	}
}
