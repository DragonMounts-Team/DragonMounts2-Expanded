package com.TheRPGAdventurer.ROTD.objects.items.gemset.armorset;

import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;
import net.minecraft.inventory.EntityEquipmentSlot;

import static com.TheRPGAdventurer.ROTD.inits.DMArmorEffects.ENDER_EFFECT;

public class DragonArmourEnder extends DragonArmourBase {
    public DragonArmourEnder(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String unlocalizedName) {
        super(materialIn, renderIndexIn, equipmentSlotIn, unlocalizedName, EnumItemBreedTypes.END, ENDER_EFFECT);
    }
}
