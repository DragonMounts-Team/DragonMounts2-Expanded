package com.TheRPGAdventurer.ROTD.objects.items.gemset.armorset;

import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;
import net.minecraft.inventory.EntityEquipmentSlot;

import static com.TheRPGAdventurer.ROTD.inits.DMArmorEffects.FOREST_EFFECT;

public class DragonArmourForest extends DragonArmourBase {
    public DragonArmourForest(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String unlocalizedName, EnumItemBreedTypes type) {
        super(materialIn, renderIndexIn, equipmentSlotIn, unlocalizedName, type, FOREST_EFFECT);
    }
}
