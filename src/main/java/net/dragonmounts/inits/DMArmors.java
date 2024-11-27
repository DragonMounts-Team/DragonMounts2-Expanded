package net.dragonmounts.inits;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.items.DragonScaleArmorItem;
import net.dragonmounts.objects.items.EnumItemBreedTypes;
import net.dragonmounts.objects.items.ItemDragonArmor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;

public class DMArmors {
    public static final Reference2IntOpenHashMap<Item> DRAGON_ARMORS = new Reference2IntOpenHashMap<>();
    public static final ObjectArrayList<Item> ARMOR = new ObjectArrayList<>();
    public static final ArmorMaterial AETHER_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial WATER_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial ICE_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial FIRE_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial FIRE2_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial FOREST_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial NETHER_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial NETHER2_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial ENDER_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial ENCHANT_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial SUNLIGHT_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial SUNLIGHT2_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial MOONLIGHT_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial MOONLIGHT_FEMALE_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial STORM_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial STORM2_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial TERRA_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial TERRA2_DRAGON_SCALE_MATERIAL;
    public static final ArmorMaterial ZOMBIE_DRAGON_SCALE_MATERIAL;

    static {
        int[] defence = new int[]{3, 7, 8, 3};
        String prefix = DragonMountsTags.MOD_ID;
        SoundEvent sound = SoundEvents.ITEM_ARMOR_EQUIP_GOLD;
        TERRA_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("TERRA_DRAGON_SCALE", prefix + ":terra", 50, defence, 11, sound, 7.0F);
        TERRA2_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("TERRA2_DRAGON_SCALE", prefix + ":terra2", 50, defence, 11, sound, 7.0F);
        MOONLIGHT_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("MOONLIGHT_DRAGON_SCALE", prefix + ":moonlight", 50, defence, 11, sound, 7.0F);
        MOONLIGHT_FEMALE_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("MOONLIGHT_FEMALE_DRAGON_SCALE", prefix + ":moonlight2", 50, defence, 11, sound, 7.0F);
        ZOMBIE_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("ZOMBIE_DRAGON_SCALE", prefix + ":zombie", 50, defence, 11, sound, 7.0F);
        defence = new int[]{4, 7, 8, 4};
        AETHER_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("AETHER_DRAGON_SCALE", prefix + ":aether", 50, defence, 11, sound, 7.0F);
        WATER_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("WATER_DRAGON_SCALE", prefix + ":water", 50, defence, 11, sound, 7.0F);
        ICE_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("ICE_DRAGON_SCALE", prefix + ":ice", 50, defence, 11, sound, 7.0F);
        FIRE_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("FIRE_DRAGON_SCALE", prefix + ":fire", 50, defence, 11, sound, 7.0F);
        FIRE2_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("FIRE2_DRAGON_SCALE", prefix + ":fire2", 50, defence, 11, sound, 7.0F);
        FOREST_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("FOREST_DRAGON_SCALE", prefix + ":forest", 50, defence, 11, sound, 7.0F);
        SUNLIGHT_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("SUNLIGHT_DRAGON_SCALE", prefix + ":sunlight", 50, defence, 11, sound, 7.0F);
        SUNLIGHT2_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("SUNLIGHT2_DRAGON_SCALE", prefix + ":sunlight2", 50, defence, 11, sound, 7.0F);
        STORM_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("STORM_DRAGON_SCALE", prefix + ":storm", 50, defence, 11, sound, 7.0F);
        STORM2_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("STORM2_DRAGON_SCALE", prefix + ":storm2", 50, defence, 11, sound, 7.0F);
        ENCHANT_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("ENCHANT_DRAGON_SCALE", prefix + ":enchant", 50, defence, 30, sound, 7.0F);
        defence = new int[]{4, 7, 9, 4};
        NETHER_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("NETHER_DRAGON_SCALE", prefix + ":nether", 55, defence, 11, sound, 8.0F);
        NETHER2_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("NETHER2_DRAGON_SCALE", prefix + ":nether2", 55, defence, 11, sound, 8.0F);
        ENDER_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("ENDER_DRAGON_SCALE", prefix + ":ender", 70, defence, 11, sound, 9.0F);
    }

    public static final Item IRON_DRAGON_ARMOR = new ItemDragonArmor("dragonarmor_iron");
    public static final Item GOLD_DRAGON_ARMOR = new ItemDragonArmor("dragonarmor_gold");
    public static final Item DIAMOND_DRAGON_ARMOR = new ItemDragonArmor("dragonarmor_diamond");
    public static final Item EMERALD_DRAGON_ARMOR = new ItemDragonArmor("dragonarmor_emerald");

    public static final DragonScaleArmorItem AETHER_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(AETHER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "aether_dragonscale_cap", EnumItemBreedTypes.AETHER, DMArmorEffects.AETHER_EFFECT);
    public static final DragonScaleArmorItem AETHER_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(AETHER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "aether_dragonscale_tunic", EnumItemBreedTypes.AETHER, DMArmorEffects.AETHER_EFFECT);
    public static final DragonScaleArmorItem AETHER_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(AETHER_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "aether_dragonscale_leggings", EnumItemBreedTypes.AETHER, DMArmorEffects.AETHER_EFFECT);
    public static final DragonScaleArmorItem AETHER_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(AETHER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "aether_dragonscale_boots", EnumItemBreedTypes.AETHER, DMArmorEffects.AETHER_EFFECT);

    public static final DragonScaleArmorItem WATER_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(WATER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "water_dragonscale_cap", EnumItemBreedTypes.WATER, DMArmorEffects.WATER_EFFECT);
    public static final DragonScaleArmorItem WATER_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(WATER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "water_dragonscale_tunic", EnumItemBreedTypes.WATER, DMArmorEffects.WATER_EFFECT);
    public static final DragonScaleArmorItem WATER_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(WATER_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "water_dragonscale_leggings", EnumItemBreedTypes.WATER, DMArmorEffects.WATER_EFFECT);
    public static final DragonScaleArmorItem WATER_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(WATER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "water_dragonscale_boots", EnumItemBreedTypes.WATER, DMArmorEffects.WATER_EFFECT);

    public static final DragonScaleArmorItem ICE_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(ICE_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "ice_dragonscale_cap", EnumItemBreedTypes.ICE, DMArmorEffects.ICE_EFFECT);
    public static final DragonScaleArmorItem ICE_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(ICE_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "ice_dragonscale_tunic", EnumItemBreedTypes.ICE, DMArmorEffects.ICE_EFFECT);
    public static final DragonScaleArmorItem ICE_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(ICE_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "ice_dragonscale_leggings", EnumItemBreedTypes.ICE, DMArmorEffects.ICE_EFFECT);
    public static final DragonScaleArmorItem ICE_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(ICE_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "ice_dragonscale_boots", EnumItemBreedTypes.ICE, DMArmorEffects.ICE_EFFECT);

    public static final DragonScaleArmorItem FIRE_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(FIRE_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "fire_dragonscale_cap", EnumItemBreedTypes.FIRE, DMArmorEffects.FIRE_EFFECT);
    public static final DragonScaleArmorItem FIRE_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(FIRE_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "fire_dragonscale_tunic", EnumItemBreedTypes.FIRE, DMArmorEffects.FIRE_EFFECT);
    public static final DragonScaleArmorItem FIRE_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(FIRE_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "fire_dragonscale_leggings", EnumItemBreedTypes.FIRE, DMArmorEffects.FIRE_EFFECT);
    public static final DragonScaleArmorItem FIRE_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(FIRE_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "fire_dragonscale_boots", EnumItemBreedTypes.FIRE, DMArmorEffects.FIRE_EFFECT);

    public static final DragonScaleArmorItem FOREST_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(FOREST_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "forest_dragonscale_cap", EnumItemBreedTypes.FOREST, DMArmorEffects.FOREST_EFFECT);
    public static final DragonScaleArmorItem FOREST_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(FOREST_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "forest_dragonscale_tunic", EnumItemBreedTypes.FOREST, DMArmorEffects.FOREST_EFFECT);
    public static final DragonScaleArmorItem FOREST_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(FOREST_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "forest_dragonscale_leggings", EnumItemBreedTypes.FOREST, DMArmorEffects.FOREST_EFFECT);
    public static final DragonScaleArmorItem FOREST_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(FOREST_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "forest_dragonscale_boots", EnumItemBreedTypes.FOREST, DMArmorEffects.FOREST_EFFECT);

    public static final DragonScaleArmorItem NETHER_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(NETHER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "nether_dragonscale_cap", EnumItemBreedTypes.NETHER, DMArmorEffects.NETHER_EFFECT);
    public static final DragonScaleArmorItem NETHER_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(NETHER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "nether_dragonscale_tunic", EnumItemBreedTypes.NETHER, DMArmorEffects.NETHER_EFFECT);
    public static final DragonScaleArmorItem NETHER_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(NETHER_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "nether_dragonscale_leggings", EnumItemBreedTypes.NETHER, DMArmorEffects.NETHER_EFFECT);
    public static final DragonScaleArmorItem NETHER_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(NETHER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "nether_dragonscale_boots", EnumItemBreedTypes.NETHER, DMArmorEffects.NETHER_EFFECT);

    public static final DragonScaleArmorItem ENDER_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(ENDER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "ender_dragonscale_cap", EnumItemBreedTypes.END, DMArmorEffects.ENDER_EFFECT);
    public static final DragonScaleArmorItem ENDER_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(ENDER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "ender_dragonscale_tunic", EnumItemBreedTypes.END, DMArmorEffects.ENDER_EFFECT);
    public static final DragonScaleArmorItem ENDER_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(ENDER_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "ender_dragonscale_leggings", EnumItemBreedTypes.END, DMArmorEffects.ENDER_EFFECT);
    public static final DragonScaleArmorItem ENDER_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(ENDER_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "ender_dragonscale_boots", EnumItemBreedTypes.END, DMArmorEffects.ENDER_EFFECT);

    public static final DragonScaleArmorItem ENCHANT_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(ENCHANT_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "enchant_dragonscale_cap", EnumItemBreedTypes.ENCHANT, DMArmorEffects.ENCHANT_EFFECT);
    public static final DragonScaleArmorItem ENCHANT_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(ENCHANT_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "enchant_dragonscale_tunic", EnumItemBreedTypes.ENCHANT, DMArmorEffects.ENCHANT_EFFECT);
    public static final DragonScaleArmorItem ENCHANT_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(ENCHANT_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "enchant_dragonscale_leggings", EnumItemBreedTypes.ENCHANT, DMArmorEffects.ENCHANT_EFFECT);
    public static final DragonScaleArmorItem ENCHANT_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(ENCHANT_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "enchant_dragonscale_boots", EnumItemBreedTypes.ENCHANT, DMArmorEffects.ENCHANT_EFFECT);

    public static final DragonScaleArmorItem SUNLIGHT_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(SUNLIGHT_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "sunlight_dragonscale_cap", EnumItemBreedTypes.SUNLIGHT, DMArmorEffects.SUNLIGHT_EFFECT);
    public static final DragonScaleArmorItem SUNLIGHT_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(SUNLIGHT_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "sunlight_dragonscale_tunic", EnumItemBreedTypes.SUNLIGHT, DMArmorEffects.SUNLIGHT_EFFECT);
    public static final DragonScaleArmorItem SUNLIGHT_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(SUNLIGHT_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "sunlight_dragonscale_leggings", EnumItemBreedTypes.SUNLIGHT, DMArmorEffects.SUNLIGHT_EFFECT);
    public static final DragonScaleArmorItem SUNLIGHT_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(SUNLIGHT_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "sunlight_dragonscale_boots", EnumItemBreedTypes.SUNLIGHT, DMArmorEffects.SUNLIGHT_EFFECT);

    public static final DragonScaleArmorItem MOONLIGHT_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(MOONLIGHT_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "moonlight_dragonscale_cap", EnumItemBreedTypes.MOONLIGHT, DMArmorEffects.MOONLIGHT_EFFECT);
    public static final DragonScaleArmorItem MOONLIGHT_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(MOONLIGHT_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "moonlight_dragonscale_tunic", EnumItemBreedTypes.MOONLIGHT, DMArmorEffects.MOONLIGHT_EFFECT);
    public static final DragonScaleArmorItem MOONLIGHT_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(MOONLIGHT_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "moonlight_dragonscale_leggings", EnumItemBreedTypes.MOONLIGHT, DMArmorEffects.MOONLIGHT_EFFECT);
    public static final DragonScaleArmorItem MOONLIGHT_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(MOONLIGHT_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "moonlight_dragonscale_boots", EnumItemBreedTypes.MOONLIGHT, DMArmorEffects.MOONLIGHT_EFFECT);

    public static final DragonScaleArmorItem STORM_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(STORM_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "storm_dragonscale_cap", EnumItemBreedTypes.STORM, DMArmorEffects.STORM_EFFECT);
    public static final DragonScaleArmorItem STORM_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(STORM_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "storm_dragonscale_tunic", EnumItemBreedTypes.STORM, DMArmorEffects.STORM_EFFECT);
    public static final DragonScaleArmorItem STORM_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(STORM_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "storm_dragonscale_leggings", EnumItemBreedTypes.STORM, DMArmorEffects.STORM_EFFECT);
    public static final DragonScaleArmorItem STORM_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(STORM_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "storm_dragonscale_boots", EnumItemBreedTypes.STORM, DMArmorEffects.STORM_EFFECT);

    public static final DragonScaleArmorItem TERRA_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(TERRA_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "terra_dragonscale_cap", EnumItemBreedTypes.TERRA, DMArmorEffects.TERRA_EFFECT);
    public static final DragonScaleArmorItem TERRA_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(TERRA_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "terra_dragonscale_tunic", EnumItemBreedTypes.TERRA, DMArmorEffects.TERRA_EFFECT);
    public static final DragonScaleArmorItem TERRA_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(TERRA_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "terra_dragonscale_leggings", EnumItemBreedTypes.TERRA, DMArmorEffects.TERRA_EFFECT);
    public static final DragonScaleArmorItem TERRA_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(TERRA_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "terra_dragonscale_boots", EnumItemBreedTypes.TERRA, DMArmorEffects.TERRA_EFFECT);

    public static final DragonScaleArmorItem ZOMBIE_DRAGON_SCALE_HELMET = new DragonScaleArmorItem(ZOMBIE_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.HEAD, "zombie_dragonscale_cap", EnumItemBreedTypes.ZOMBIE, DMArmorEffects.ZOMBIE_EFFECT);
    public static final DragonScaleArmorItem ZOMBIE_DRAGON_SCALE_CHESTPLATE = new DragonScaleArmorItem(ZOMBIE_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.CHEST, "zombie_dragonscale_tunic", EnumItemBreedTypes.ZOMBIE, DMArmorEffects.ZOMBIE_EFFECT);
    public static final DragonScaleArmorItem ZOMBIE_DRAGON_SCALE_LEGGINGS = new DragonScaleArmorItem(ZOMBIE_DRAGON_SCALE_MATERIAL, 2, EntityEquipmentSlot.LEGS, "zombie_dragonscale_leggings", EnumItemBreedTypes.ZOMBIE, DMArmorEffects.ZOMBIE_EFFECT);
    public static final DragonScaleArmorItem ZOMBIE_DRAGON_SCALE_BOOTS = new DragonScaleArmorItem(ZOMBIE_DRAGON_SCALE_MATERIAL, 1, EntityEquipmentSlot.FEET, "zombie_dragonscale_boots", EnumItemBreedTypes.ZOMBIE, DMArmorEffects.ZOMBIE_EFFECT);

    public static void InitializaRepairs() {
        WATER_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.WaterDragonScales));
        AETHER_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.AetherDragonScales));
        FOREST_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.ForestDragonScales));
        FIRE_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.FireDragonScales));
        ICE_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.IceDragonScales));
        NETHER_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.NetherDragonScales));
        ENDER_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.EnderDragonScales));
        ENCHANT_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.EnchantDragonScales));
        ZOMBIE_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.ZombieDragonScales));
        TERRA_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.TerraDragonScales));
        SUNLIGHT_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.SunlightDragonScales));
        STORM_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.StormDragonScales));
        MOONLIGHT_DRAGON_SCALE_MATERIAL.setRepairItem(new ItemStack(ModItems.MoonlightDragonScales));
    }

    static {
        DRAGON_ARMORS.put(DMArmors.IRON_DRAGON_ARMOR, 1);
        DRAGON_ARMORS.put(DMArmors.GOLD_DRAGON_ARMOR, 2);
        DRAGON_ARMORS.put(DMArmors.DIAMOND_DRAGON_ARMOR, 3);
        DRAGON_ARMORS.put(DMArmors.EMERALD_DRAGON_ARMOR, 4);
    }
}