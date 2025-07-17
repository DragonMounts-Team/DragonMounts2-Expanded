package net.dragonmounts.init;

import net.dragonmounts.DragonMountsTags;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.SoundEvent;

import static net.minecraftforge.common.util.EnumHelper.addArmorMaterial;

public class DMMaterials {
    public static final ArmorMaterial AETHER_DRAGON_SCALE;
    public static final ArmorMaterial WATER_DRAGON_SCALE;
    public static final ArmorMaterial ICE_DRAGON_SCALE;
    public static final ArmorMaterial FIRE_DRAGON_SCALE;
    public static final ArmorMaterial FOREST_DRAGON_SCALE;
    public static final ArmorMaterial NETHER_DRAGON_SCALE;
    public static final ArmorMaterial ENDER_DRAGON_SCALE;
    public static final ArmorMaterial ENCHANT_DRAGON_SCALE;
    public static final ArmorMaterial SUNLIGHT_DRAGON_SCALE;
    public static final ArmorMaterial MOONLIGHT_DRAGON_SCALE;
    public static final ArmorMaterial STORM_DRAGON_SCALE;
    public static final ArmorMaterial TERRA_DRAGON_SCALE;
    public static final ArmorMaterial ZOMBIE_DRAGON_SCALE;
    public static final ArmorMaterial DARK_DRAGON_SCALE;

    static {
        int[] defence = new int[]{3, 7, 8, 3};
        String prefix = DragonMountsTags.MOD_ID;
        SoundEvent sound = SoundEvents.ITEM_ARMOR_EQUIP_GOLD;
        TERRA_DRAGON_SCALE = addArmorMaterial("DM2C_TERRA_DRAGON_SCALE", prefix + ":terra", 50, defence, 11, sound, 7.0F);
        MOONLIGHT_DRAGON_SCALE = addArmorMaterial("DM2C_MOONLIGHT_DRAGON_SCALE", prefix + ":moonlight", 50, defence, 11, sound, 7.0F);
        ZOMBIE_DRAGON_SCALE = addArmorMaterial("DM2C_ZOMBIE_DRAGON_SCALE", prefix + ":zombie", 50, defence, 11, sound, 7.0F);
        defence = new int[]{4, 7, 8, 4};
        AETHER_DRAGON_SCALE = addArmorMaterial("DM2C_AETHER_DRAGON_SCALE", prefix + ":aether", 50, defence, 11, sound, 7.0F);
        WATER_DRAGON_SCALE = addArmorMaterial("DM2C_WATER_DRAGON_SCALE", prefix + ":water", 50, defence, 11, sound, 7.0F);
        ICE_DRAGON_SCALE = addArmorMaterial("DM2C_ICE_DRAGON_SCALE", prefix + ":ice", 50, defence, 11, sound, 7.0F);
        FIRE_DRAGON_SCALE = addArmorMaterial("DM2C_FIRE_DRAGON_SCALE", prefix + ":fire", 50, defence, 11, sound, 7.0F);
        FOREST_DRAGON_SCALE = addArmorMaterial("DM2C_FOREST_DRAGON_SCALE", prefix + ":forest", 50, defence, 11, sound, 7.0F);
        SUNLIGHT_DRAGON_SCALE = addArmorMaterial("DM2C_SUNLIGHT_DRAGON_SCALE", prefix + ":sunlight", 50, defence, 11, sound, 7.0F);
        STORM_DRAGON_SCALE = addArmorMaterial("DM2C_STORM_DRAGON_SCALE", prefix + ":storm", 50, defence, 11, sound, 7.0F);
        ENCHANT_DRAGON_SCALE = addArmorMaterial("DM2C_ENCHANT_DRAGON_SCALE", prefix + ":enchant", 50, defence, 30, sound, 7.0F);
        defence = new int[]{4, 7, 9, 4};
        NETHER_DRAGON_SCALE = addArmorMaterial("DM2C_NETHER_DRAGON_SCALE", prefix + ":nether", 55, defence, 11, sound, 8.0F);
        ENDER_DRAGON_SCALE = addArmorMaterial("DM2C_ENDER_DRAGON_SCALE", prefix + ":ender", 70, defence, 11, sound, 9.0F);
        defence = new int[]{3, 7, 8, 5};
        DARK_DRAGON_SCALE= addArmorMaterial("DM2C_DARK_DRAGON_SCALE", prefix + ":dark", 70, defence, 13, sound, 8.0F);
    }
}
