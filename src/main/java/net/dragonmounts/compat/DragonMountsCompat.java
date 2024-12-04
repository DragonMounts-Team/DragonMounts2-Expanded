package net.dragonmounts.compat;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.compat.fixer.DMBlockEntityCompat;
import net.dragonmounts.compat.fixer.DragonEntityCompat;
import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.inits.ModItems;
import net.dragonmounts.util.DragonScaleArmorSuit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public abstract class DragonMountsCompat {
    public static final int VERSION = 0;
    public static final Object2ObjectOpenHashMap<String, Item> ITEM_MAPPINGS = new Object2ObjectOpenHashMap<>();

    public static void load(ModFixs fixer) {
        fixer.registerFix(FixTypes.ENTITY, new DragonEntityCompat());
        fixer.registerFix(FixTypes.BLOCK_ENTITY, new DMBlockEntityCompat());
    }

    @SubscribeEvent
    public static void remapItem(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings()) {
            Item item = ITEM_MAPPINGS.get(mapping.key.getPath());
            if (item != null) {
                mapping.remap(item);
            }
        }
    }

    @SubscribeEvent
    public static void remapBlock(RegistryEvent.MissingMappings<Block> event) {
        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getMappings()) {
            switch (mapping.key.getPath()) {
                case "pileofsticks":
                    mapping.remap(DMBlocks.DRAGON_NEST);
                    break;
                case "block_dragon_shulker":
                    mapping.remap(DMBlocks.DRAGON_CORE);
                    break;
            }
        }
    }

    static {
        //Blocks
        ITEM_MAPPINGS.put("pileofsticks", ModItems.DRAGON_NEST);
        ITEM_MAPPINGS.put("block_dragon_shulker", ModItems.DRAGON_CORE);
        //Misc
        ITEM_MAPPINGS.put("dragon_gender", DMItems.VARIANT_SWITCHER);
        //Spawn Eggs
        ITEM_MAPPINGS.put("summon_aether", DMItems.AETHER_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_enchant", DMItems.ENCHANT_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_end", DMItems.ENDER_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_fire", DMItems.FIRE_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_forest", DMItems.FOREST_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_ice", DMItems.ICE_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_moonlight", DMItems.MOONLIGHT_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_nether", DMItems.NETHER_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_skeleton", DMItems.SKELETON_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_storm", DMItems.STORM_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_sunlight", DMItems.SUNLIGHT_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_terra", DMItems.TERRA_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_water", DMItems.WATER_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_wither", DMItems.WITHER_DRAGON_SPAWN_EGG);
        ITEM_MAPPINGS.put("summon_zombie", DMItems.ZOMBIE_DRAGON_SPAWN_EGG);

        ITEM_MAPPINGS.put("aether_dragonscales", DMItems.AETHER_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_aether", DMItems.AETHER_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_aether", DMItems.AETHER_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("aether_dragon_sword", DMItems.AETHER_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("aether_dragon_axe", DMItems.AETHER_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("aether_dragon_pickaxe", DMItems.AETHER_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("aether_dragon_hoe", DMItems.AETHER_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("aether_dragon_shovel", DMItems.AETHER_DRAGON_SCALE_SHOVEL);
        DragonScaleArmorSuit suit = DMItems.AETHER_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("aether_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("aether_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("aether_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("aether_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("water_dragonscales", DMItems.WATER_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_water", DMItems.WATER_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_water", DMItems.WATER_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("water_dragon_sword", DMItems.WATER_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("water_dragon_axe", DMItems.WATER_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("water_dragon_pickaxe", DMItems.WATER_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("water_dragon_hoe", DMItems.WATER_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("water_dragon_shovel", DMItems.WATER_DRAGON_SCALE_SHOVEL);
        suit = DMItems.WATER_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("water_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("water_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("water_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("water_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("ice_dragonscales", DMItems.ICE_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_ice", DMItems.ICE_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_ice", DMItems.ICE_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("ice_dragon_sword", DMItems.ICE_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("ice_dragon_axe", DMItems.ICE_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("ice_dragon_pickaxe", DMItems.ICE_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("ice_dragon_hoe", DMItems.ICE_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("ice_dragon_shovel", DMItems.ICE_DRAGON_SCALE_SHOVEL);
        suit = DMItems.ICE_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("ice_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("ice_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("ice_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("ice_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("fire_dragonscales", DMItems.FIRE_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_fire", DMItems.FIRE_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_fire", DMItems.FIRE_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("fire_dragon_sword", DMItems.FIRE_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("fire_dragon_axe", DMItems.FIRE_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("fire_dragon_pickaxe", DMItems.FIRE_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("fire_dragon_hoe", DMItems.FIRE_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("fire_dragon_shovel", DMItems.FIRE_DRAGON_SCALE_SHOVEL);
        ITEM_MAPPINGS.put("fire2_dragonscales", DMItems.FIRE_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_fire2", DMItems.FIRE_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_fire2", DMItems.FIRE_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("fire2_dragon_sword", DMItems.FIRE_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("fire2_dragon_axe", DMItems.FIRE_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("fire2_dragon_pickaxe", DMItems.FIRE_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("fire2_dragon_hoe", DMItems.FIRE_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("fire2_dragon_shovel", DMItems.FIRE_DRAGON_SCALE_SHOVEL);
        suit = DMItems.FIRE_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("fire_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("fire_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("fire_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("fire_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("fire2_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("fire2_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("fire2_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("fire2_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("forest_dragonscales", DMItems.FOREST_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_forest", DMItems.FOREST_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_forest", DMItems.FOREST_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("forest_dragon_sword", DMItems.FOREST_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("forest_dragon_axe", DMItems.FOREST_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("forest_dragon_pickaxe", DMItems.FOREST_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("forest_dragon_hoe", DMItems.FOREST_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("forest_dragon_shovel", DMItems.FOREST_DRAGON_SCALE_SHOVEL);
        suit = DMItems.FOREST_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("forest_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("forest_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("forest_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("forest_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("nether_dragonscales", DMItems.NETHER_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_nether", DMItems.NETHER_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_nether", DMItems.NETHER_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("nether_dragon_sword", DMItems.NETHER_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("nether_dragon_axe", DMItems.NETHER_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("nether_dragon_pickaxe", DMItems.NETHER_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("nether_dragon_hoe", DMItems.NETHER_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("nether_dragon_shovel", DMItems.NETHER_DRAGON_SCALE_SHOVEL);
        ITEM_MAPPINGS.put("nether2_dragonscales", DMItems.NETHER_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_nether2", DMItems.NETHER_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_nether2", DMItems.NETHER_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("nether2_dragon_sword", DMItems.NETHER_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("nether2_dragon_axe", DMItems.NETHER_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("nether2_dragon_pickaxe", DMItems.NETHER_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("nether2_dragon_hoe", DMItems.NETHER_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("nether2_dragon_shovel", DMItems.NETHER_DRAGON_SCALE_SHOVEL);
        suit = DMItems.NETHER_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("nether_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("nether_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("nether_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("nether_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("nether2_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("nether2_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("nether2_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("nether2_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("ender_dragonscales", DMItems.ENDER_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_ender", DMItems.ENDER_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_ender", DMItems.ENDER_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("ender_dragon_sword", DMItems.ENDER_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("ender_dragon_axe", DMItems.ENDER_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("ender_dragon_pickaxe", DMItems.ENDER_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("ender_dragon_hoe", DMItems.ENDER_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("ender_dragon_shovel", DMItems.ENDER_DRAGON_SCALE_SHOVEL);
        suit = DMItems.ENDER_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("ender_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("ender_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("ender_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("ender_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("enchant_dragonscales", DMItems.ENCHANT_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_enchant", DMItems.ENCHANT_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_enchant", DMItems.ENCHANT_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("enchant_dragon_sword", DMItems.ENCHANT_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("enchant_dragon_axe", DMItems.ENCHANT_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("enchant_dragon_pickaxe", DMItems.ENCHANT_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("enchant_dragon_hoe", DMItems.ENCHANT_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("enchant_dragon_shovel", DMItems.ENCHANT_DRAGON_SCALE_SHOVEL);
        suit = DMItems.ENCHANT_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("enchant_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("enchant_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("enchant_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("enchant_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("sunlight_dragonscales", DMItems.SUNLIGHT_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_sunlight", DMItems.SUNLIGHT_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_sunlight", DMItems.SUNLIGHT_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("sunlight_dragon_sword", DMItems.SUNLIGHT_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("sunlight_dragon_axe", DMItems.SUNLIGHT_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("sunlight_dragon_pickaxe", DMItems.SUNLIGHT_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("sunlight_dragon_hoe", DMItems.SUNLIGHT_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("sunlight_dragon_shovel", DMItems.SUNLIGHT_DRAGON_SCALE_SHOVEL);
        ITEM_MAPPINGS.put("sunlight2_dragonscales", DMItems.SUNLIGHT_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_sunlight2", DMItems.SUNLIGHT_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_sunlight2", DMItems.SUNLIGHT_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("sunlight2_dragon_sword", DMItems.SUNLIGHT_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("sunlight2_dragon_axe", DMItems.SUNLIGHT_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("sunlight2_dragon_pickaxe", DMItems.SUNLIGHT_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("sunlight2_dragon_hoe", DMItems.SUNLIGHT_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("sunlight2_dragon_shovel", DMItems.SUNLIGHT_DRAGON_SCALE_SHOVEL);
        suit = DMItems.SUNLIGHT_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("sunlight_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("sunlight_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("sunlight_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("sunlight_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("sunlight2_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("sunlight2_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("sunlight2_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("sunlight2_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("moonlight_dragonscales", DMItems.MOONLIGHT_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_moonlight", DMItems.MOONLIGHT_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_moonlight", DMItems.MOONLIGHT_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("moonlight_dragon_sword", DMItems.MOONLIGHT_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("moonlight_dragon_axe", DMItems.MOONLIGHT_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("moonlight_dragon_pickaxe", DMItems.MOONLIGHT_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("moonlight_dragon_hoe", DMItems.MOONLIGHT_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("moonlight_dragon_shovel", DMItems.MOONLIGHT_DRAGON_SCALE_SHOVEL);
        ITEM_MAPPINGS.put("moonlight2_dragonscales", DMItems.MOONLIGHT_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_moonlight2", DMItems.MOONLIGHT_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_moonlight2", DMItems.MOONLIGHT_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("moonlight2_dragon_sword", DMItems.MOONLIGHT_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("moonlight2_dragon_axe", DMItems.MOONLIGHT_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("moonlight2_dragon_pickaxe", DMItems.MOONLIGHT_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("moonlight2_dragon_hoe", DMItems.MOONLIGHT_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("moonlight2_dragon_shovel", DMItems.MOONLIGHT_DRAGON_SCALE_SHOVEL);
        suit = DMItems.MOONLIGHT_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("moonlight_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("moonlight_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("moonlight_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("moonlight_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("moonlight2_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("moonlight2_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("moonlight2_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("moonlight2_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("storm_dragonscales", DMItems.STORM_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_storm", DMItems.STORM_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_storm", DMItems.STORM_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("storm_dragon_sword", DMItems.STORM_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("storm_dragon_axe", DMItems.STORM_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("storm_dragon_pickaxe", DMItems.STORM_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("storm_dragon_hoe", DMItems.STORM_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("storm_dragon_shovel", DMItems.STORM_DRAGON_SCALE_SHOVEL);
        suit = DMItems.STORM_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("storm_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("storm_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("storm_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("storm_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("terra_dragonscales", DMItems.TERRA_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_terra", DMItems.TERRA_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_terra", DMItems.TERRA_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("terra_dragon_sword", DMItems.TERRA_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("terra_dragon_axe", DMItems.TERRA_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("terra_dragon_pickaxe", DMItems.TERRA_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("terra_dragon_hoe", DMItems.TERRA_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("terra_dragon_shovel", DMItems.TERRA_DRAGON_SCALE_SHOVEL);
        ITEM_MAPPINGS.put("terra2_dragonscales", DMItems.TERRA_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_terra2", DMItems.TERRA_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_terra2", DMItems.TERRA_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("terra2_dragon_sword", DMItems.TERRA_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("terra2_dragon_axe", DMItems.TERRA_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("terra2_dragon_pickaxe", DMItems.TERRA_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("terra2_dragon_hoe", DMItems.TERRA_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("terra2_dragon_shovel", DMItems.TERRA_DRAGON_SCALE_SHOVEL);
        suit = DMItems.TERRA_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("terra_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("terra_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("terra_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("terra_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("terra2_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("terra2_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("terra2_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("terra2_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS.put("zombie_dragonscales", DMItems.ZOMBIE_DRAGON_SCALES);
        ITEM_MAPPINGS.put("dragon_bow_zombie", DMItems.ZOMBIE_DRAGON_SCALE_BOW);
        ITEM_MAPPINGS.put("dragon_shield_zombie", DMItems.ZOMBIE_DRAGON_SCALE_SHIELD);
        ITEM_MAPPINGS.put("zombie_dragon_sword", DMItems.ZOMBIE_DRAGON_SCALE_SWORD);
        ITEM_MAPPINGS.put("zombie_dragon_axe", DMItems.ZOMBIE_DRAGON_SCALE_AXE);
        ITEM_MAPPINGS.put("zombie_dragon_pickaxe", DMItems.ZOMBIE_DRAGON_SCALE_PICKAXE);
        ITEM_MAPPINGS.put("zombie_dragon_hoe", DMItems.ZOMBIE_DRAGON_SCALE_HOE);
        ITEM_MAPPINGS.put("zombie_dragon_shovel", DMItems.ZOMBIE_DRAGON_SCALE_SHOVEL);
        suit = DMItems.ZOMBIE_DRAGON_SCALE_ARMORS;
        ITEM_MAPPINGS.put("zombie_dragonscale_cap", suit.helmet);
        ITEM_MAPPINGS.put("zombie_dragonscale_tunic", suit.chestplate);
        ITEM_MAPPINGS.put("zombie_dragonscale_leggings", suit.leggings);
        ITEM_MAPPINGS.put("zombie_dragonscale_boots", suit.boots);
    }

    private DragonMountsCompat() {}
}
