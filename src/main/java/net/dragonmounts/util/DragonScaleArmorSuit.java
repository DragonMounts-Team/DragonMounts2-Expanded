package net.dragonmounts.util;

import net.dragonmounts.api.IDescribableArmorEffect;
import net.dragonmounts.item.DragonScaleArmorItem;
import net.dragonmounts.registry.DragonType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class DragonScaleArmorSuit extends ArmorSuit<DragonScaleArmorItem> {
    public final ItemArmor.ArmorMaterial material;
    public final IDescribableArmorEffect effect;
    public final DragonType type;

    public DragonScaleArmorSuit(ItemArmor.ArmorMaterial material, DragonType type, IDescribableArmorEffect effect) {
        super(
                new DragonScaleArmorItem(material, 1, EntityEquipmentSlot.HEAD, type, effect),
                new DragonScaleArmorItem(material, 1, EntityEquipmentSlot.CHEST, type, effect),
                new DragonScaleArmorItem(material, 2, EntityEquipmentSlot.LEGS, type, effect),
                new DragonScaleArmorItem(material, 1, EntityEquipmentSlot.FEET, type, effect)
        );
        this.type = type;
        this.material = material;
        this.effect = effect;
        this.helmet.setTranslationKey("dragon_scale_helmet");
        this.chestplate.setTranslationKey("dragon_scale_chestplate");
        this.leggings.setTranslationKey("dragon_scale_leggings");
        this.boots.setTranslationKey("dragon_scale_boots");
    }
}
