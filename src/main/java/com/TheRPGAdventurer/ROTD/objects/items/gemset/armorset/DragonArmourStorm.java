package com.TheRPGAdventurer.ROTD.objects.items.gemset.armorset;

import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;
import net.minecraft.inventory.EntityEquipmentSlot;

import static com.TheRPGAdventurer.ROTD.inits.DMArmorEffects.STORM_EFFECT;

public class DragonArmourStorm extends DragonArmourBase {
	public DragonArmourStorm(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String unlocalizedName) {
		super(materialIn, renderIndexIn, equipmentSlotIn, unlocalizedName, EnumItemBreedTypes.STORM, STORM_EFFECT);
	}
}
