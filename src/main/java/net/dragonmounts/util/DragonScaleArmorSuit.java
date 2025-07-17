package net.dragonmounts.util;

import net.dragonmounts.api.IDescribableArmorEffect;
import net.dragonmounts.item.DragonScaleArmorItem;
import net.dragonmounts.registry.DragonType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

public class DragonScaleArmorSuit extends ArmorSuit<DragonScaleArmorItem> {
    public static final String TRANSLATION_KEY_HELMET = TRANSLATION_KEY_PREFIX + "dragon_scale_helmet";
    public static final String TRANSLATION_KEY_CHESTPLATE = TRANSLATION_KEY_PREFIX + "dragon_scale_chestplate";
    public static final String TRANSLATION_KEY_LEGGINGS = TRANSLATION_KEY_PREFIX + "dragon_scale_leggings";
    public static final String TRANSLATION_KEY_BOOTS = TRANSLATION_KEY_PREFIX + "dragon_scale_boots";
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
    }
}
