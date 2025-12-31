package net.dragonmounts.compat;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.block.DragonEggCompatBlock;
import net.dragonmounts.compat.data.DMBlockEntityFixer;
import net.dragonmounts.compat.data.DataWalkers;
import net.dragonmounts.compat.data.DragonEntityFixer;
import net.dragonmounts.compat.data.DragonNestFixer;
import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.init.DragonVariants;
import net.dragonmounts.item.DragonEggCompatItem;
import net.dragonmounts.registry.CooldownCategory;
import net.dragonmounts.util.DragonScaleArmorSuit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

@Mod.EventBusSubscriber
public abstract class DragonMountsCompat {
    public static final String BAUBLES = "baubles";
    public static final String PATCHOULI = "patchouli";

    public static final int VERSION = 1;
    public static final Object2ObjectOpenHashMap<String, Item> ITEM_MAPPINGS;
    public static final Block DRAGON_EGG_BLOCK = DragonEggCompatBlock.INSTANCE;
    public static final Item DRAGON_EGG_ITEM = DragonEggCompatItem.INSTANCE;

    public static void init(CompoundDataFixer fixer) {
        ModFixs mod = fixer.init(DragonMountsTags.MOD_ID, DragonMountsCompat.VERSION);
        mod.registerFix(FixTypes.ENTITY, new DragonEntityFixer());
        mod.registerFix(FixTypes.BLOCK_ENTITY, new DMBlockEntityFixer());
        mod.registerFix(FixTypes.STRUCTURE, new DragonNestFixer());
        fixer.registerVanillaWalker(FixTypes.BLOCK_ENTITY, DataWalkers::fixDragonCore);
        fixer.registerVanillaWalker(FixTypes.ENTITY, DataWalkers::fixDragonInventory);
        fixer.registerVanillaWalker(FixTypes.ITEM_INSTANCE, DataWalkers::fixEntityContainers);
    }

    @SubscribeEvent
    public static void remapEntity(RegistryEvent.MissingMappings<EntityEntry> event) {
        for (RegistryEvent.MissingMappings.Mapping<EntityEntry> mapping : event.getMappings()) {
            if (mapping.key.getPath().equals("indestructible")) {
                mapping.remap(event.getRegistry().getValue(new ResourceLocation("item")));
            }
        }
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
                case "skeleton_female_dragon_head":
                case "skeleton_male_dragon_head":
                    mapping.remap(DragonVariants.SKELETON.head.standing);
                    break;
                case "wither_female_dragon_head":
                case "wither_male_dragon_head":
                    mapping.remap(DragonVariants.WITHER.head.standing);
                    break;
                case "zombie_female_dragon_head":
                case "zombie_male_dragon_head":
                    mapping.remap(DragonVariants.ZOMBIE.head.standing);
                    break;
                case "skeleton_female_dragon_head_wall":
                case "skeleton_male_dragon_head_wall":
                    mapping.remap(DragonVariants.SKELETON.head.wall);
                    break;
                case "wither_female_dragon_head_wall":
                case "wither_male_dragon_head_wall":
                    mapping.remap(DragonVariants.WITHER.head.wall);
                    break;
                case "zombie_female_dragon_head_wall":
                case "zombie_male_dragon_head_wall":
                    mapping.remap(DragonVariants.ZOMBIE.head.wall);
                    break;
            }
        }
    }

    @SubscribeEvent
    public static void remapCategory(RegistryEvent.MissingMappings<CooldownCategory> event) {
        event.getMappings().forEach(RegistryEvent.MissingMappings.Mapping::ignore);
    }

    @SubscribeEvent
    public static void ignoreSound(RegistryEvent.MissingMappings<SoundEvent> event) {
        event.getMappings().forEach(RegistryEvent.MissingMappings.Mapping::ignore);
    }

    static {
        Object2ObjectOpenHashMap<String, Item> mappings = new Object2ObjectOpenHashMap<>();
        //Blocks
        mappings.put("pileofsticks", DMItems.DRAGON_NEST);
        mappings.put("block_dragon_shulker", DMItems.DRAGON_CORE);
        //Misc
        mappings.put("dragon_amulet", DMItems.AMULET);
        mappings.put("dragon_gender", DMItems.VARIATION_ORB);
        mappings.put("dragon_whistle", DMItems.FLUTE);
        mappings.put("variant_switcher", DMItems.VARIATION_ORB);
        mappings.put("end_dragon_amulet", DMItems.ENDER_DRAGON_AMULET);
        mappings.put("end_dragon_essence", DMItems.ENDER_DRAGON_ESSENCE);
        //Carriage
        mappings.put("carriage_acacia", DMItems.ACACIA_CARRIAGE);
        mappings.put("carriage_birch", DMItems.BIRCH_CARRIAGE);
        mappings.put("carriage_dark_oak", DMItems.DARK_OAK_CARRIAGE);
        mappings.put("carriage_jungle", DMItems.JUNGLE_CARRIAGE);
        mappings.put("carriage_oak", DMItems.OAK_CARRIAGE);
        mappings.put("carriage_spruce", DMItems.SPRUCE_CARRIAGE);
        //Dragon Armor
        mappings.put("dragonarmor_iron", DMItems.IRON_DRAGON_ARMOR);
        mappings.put("dragonarmor_gold", DMItems.GOLDEN_DRAGON_ARMOR);
        mappings.put("dragonarmor_emerald", DMItems.EMERALD_DRAGON_ARMOR);
        mappings.put("dragonarmor_diamond", DMItems.DIAMOND_DRAGON_ARMOR);
        //Spawn Eggs
        mappings.put("summon_aether", DMItems.AETHER_DRAGON_SPAWN_EGG);
        mappings.put("summon_enchant", DMItems.ENCHANTED_DRAGON_SPAWN_EGG);
        mappings.put("summon_end", DMItems.ENDER_DRAGON_SPAWN_EGG);
        mappings.put("summon_fire", DMItems.FIRE_DRAGON_SPAWN_EGG);
        mappings.put("summon_forest", DMItems.FOREST_DRAGON_SPAWN_EGG);
        mappings.put("summon_ice", DMItems.ICE_DRAGON_SPAWN_EGG);
        mappings.put("summon_moonlight", DMItems.MOONLIGHT_DRAGON_SPAWN_EGG);
        mappings.put("summon_nether", DMItems.NETHER_DRAGON_SPAWN_EGG);
        mappings.put("summon_skeleton", DMItems.SKELETON_DRAGON_SPAWN_EGG);
        mappings.put("summon_storm", DMItems.STORM_DRAGON_SPAWN_EGG);
        mappings.put("summon_sunlight", DMItems.SUNLIGHT_DRAGON_SPAWN_EGG);
        mappings.put("summon_terra", DMItems.TERRA_DRAGON_SPAWN_EGG);
        mappings.put("summon_water", DMItems.WATER_DRAGON_SPAWN_EGG);
        mappings.put("summon_wither", DMItems.WITHER_DRAGON_SPAWN_EGG);
        mappings.put("summon_zombie", DMItems.ZOMBIE_DRAGON_SPAWN_EGG);
        //Items
        mappings.put("skeleton_female_dragon_head", DragonVariants.SKELETON.head.item);
        mappings.put("skeleton_male_dragon_head", DragonVariants.SKELETON.head.item);
        mappings.put("wither_female_dragon_head", DragonVariants.WITHER.head.item);
        mappings.put("wither_male_dragon_head", DragonVariants.WITHER.head.item);
        mappings.put("zombie_female_dragon_head", DragonVariants.ZOMBIE.head.item);
        mappings.put("zombie_male_dragon_head", DragonVariants.ZOMBIE.head.item);
        mappings.put("aether_dragonscales", DMItems.AETHER_DRAGON_SCALES);
        mappings.put("dragon_bow_aether", DMItems.AETHER_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_aether", DMItems.AETHER_DRAGON_SCALE_SHIELD);
        mappings.put("aether_dragon_sword", DMItems.AETHER_DRAGON_SCALE_SWORD);
        mappings.put("aether_dragon_axe", DMItems.AETHER_DRAGON_SCALE_AXE);
        mappings.put("aether_dragon_pickaxe", DMItems.AETHER_DRAGON_SCALE_PICKAXE);
        mappings.put("aether_dragon_hoe", DMItems.AETHER_DRAGON_SCALE_HOE);
        mappings.put("aether_dragon_shovel", DMItems.AETHER_DRAGON_SCALE_SHOVEL);
        DragonScaleArmorSuit suit = DMItems.AETHER_DRAGON_SCALE_ARMORS;
        mappings.put("aether_dragonscale_cap", suit.helmet);
        mappings.put("aether_dragonscale_tunic", suit.chestplate);
        mappings.put("aether_dragonscale_leggings", suit.leggings);
        mappings.put("aether_dragonscale_boots", suit.boots);
        mappings.put("water_dragonscales", DMItems.WATER_DRAGON_SCALES);
        mappings.put("dragon_bow_water", DMItems.WATER_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_water", DMItems.WATER_DRAGON_SCALE_SHIELD);
        mappings.put("water_dragon_sword", DMItems.WATER_DRAGON_SCALE_SWORD);
        mappings.put("water_dragon_axe", DMItems.WATER_DRAGON_SCALE_AXE);
        mappings.put("water_dragon_pickaxe", DMItems.WATER_DRAGON_SCALE_PICKAXE);
        mappings.put("water_dragon_hoe", DMItems.WATER_DRAGON_SCALE_HOE);
        mappings.put("water_dragon_shovel", DMItems.WATER_DRAGON_SCALE_SHOVEL);
        suit = DMItems.WATER_DRAGON_SCALE_ARMORS;
        mappings.put("water_dragonscale_cap", suit.helmet);
        mappings.put("water_dragonscale_tunic", suit.chestplate);
        mappings.put("water_dragonscale_leggings", suit.leggings);
        mappings.put("water_dragonscale_boots", suit.boots);
        mappings.put("ice_dragonscales", DMItems.ICE_DRAGON_SCALES);
        mappings.put("dragon_bow_ice", DMItems.ICE_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_ice", DMItems.ICE_DRAGON_SCALE_SHIELD);
        mappings.put("ice_dragon_sword", DMItems.ICE_DRAGON_SCALE_SWORD);
        mappings.put("ice_dragon_axe", DMItems.ICE_DRAGON_SCALE_AXE);
        mappings.put("ice_dragon_pickaxe", DMItems.ICE_DRAGON_SCALE_PICKAXE);
        mappings.put("ice_dragon_hoe", DMItems.ICE_DRAGON_SCALE_HOE);
        mappings.put("ice_dragon_shovel", DMItems.ICE_DRAGON_SCALE_SHOVEL);
        suit = DMItems.ICE_DRAGON_SCALE_ARMORS;
        mappings.put("ice_dragonscale_cap", suit.helmet);
        mappings.put("ice_dragonscale_tunic", suit.chestplate);
        mappings.put("ice_dragonscale_leggings", suit.leggings);
        mappings.put("ice_dragonscale_boots", suit.boots);
        mappings.put("fire_dragonscales", DMItems.FIRE_DRAGON_SCALES);
        mappings.put("dragon_bow_fire", DMItems.FIRE_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_fire", DMItems.FIRE_DRAGON_SCALE_SHIELD);
        mappings.put("fire_dragon_sword", DMItems.FIRE_DRAGON_SCALE_SWORD);
        mappings.put("fire_dragon_axe", DMItems.FIRE_DRAGON_SCALE_AXE);
        mappings.put("fire_dragon_pickaxe", DMItems.FIRE_DRAGON_SCALE_PICKAXE);
        mappings.put("fire_dragon_hoe", DMItems.FIRE_DRAGON_SCALE_HOE);
        mappings.put("fire_dragon_shovel", DMItems.FIRE_DRAGON_SCALE_SHOVEL);
        mappings.put("fire2_dragonscales", DMItems.FIRE_DRAGON_SCALES);
        mappings.put("dragon_bow_fire2", DMItems.FIRE_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_fire2", DMItems.FIRE_DRAGON_SCALE_SHIELD);
        mappings.put("fire2_dragon_sword", DMItems.FIRE_DRAGON_SCALE_SWORD);
        mappings.put("fire2_dragon_axe", DMItems.FIRE_DRAGON_SCALE_AXE);
        mappings.put("fire2_dragon_pickaxe", DMItems.FIRE_DRAGON_SCALE_PICKAXE);
        mappings.put("fire2_dragon_hoe", DMItems.FIRE_DRAGON_SCALE_HOE);
        mappings.put("fire2_dragon_shovel", DMItems.FIRE_DRAGON_SCALE_SHOVEL);
        suit = DMItems.FIRE_DRAGON_SCALE_ARMORS;
        mappings.put("fire_dragonscale_cap", suit.helmet);
        mappings.put("fire_dragonscale_tunic", suit.chestplate);
        mappings.put("fire_dragonscale_leggings", suit.leggings);
        mappings.put("fire_dragonscale_boots", suit.boots);
        mappings.put("fire2_dragonscale_cap", suit.helmet);
        mappings.put("fire2_dragonscale_tunic", suit.chestplate);
        mappings.put("fire2_dragonscale_leggings", suit.leggings);
        mappings.put("fire2_dragonscale_boots", suit.boots);
        mappings.put("forest_dragonscales", DMItems.FOREST_DRAGON_SCALES);
        mappings.put("dragon_bow_forest", DMItems.FOREST_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_forest", DMItems.FOREST_DRAGON_SCALE_SHIELD);
        mappings.put("forest_dragon_sword", DMItems.FOREST_DRAGON_SCALE_SWORD);
        mappings.put("forest_dragon_axe", DMItems.FOREST_DRAGON_SCALE_AXE);
        mappings.put("forest_dragon_pickaxe", DMItems.FOREST_DRAGON_SCALE_PICKAXE);
        mappings.put("forest_dragon_hoe", DMItems.FOREST_DRAGON_SCALE_HOE);
        mappings.put("forest_dragon_shovel", DMItems.FOREST_DRAGON_SCALE_SHOVEL);
        suit = DMItems.FOREST_DRAGON_SCALE_ARMORS;
        mappings.put("forest_dragonscale_cap", suit.helmet);
        mappings.put("forest_dragonscale_tunic", suit.chestplate);
        mappings.put("forest_dragonscale_leggings", suit.leggings);
        mappings.put("forest_dragonscale_boots", suit.boots);
        mappings.put("nether_dragonscales", DMItems.NETHER_DRAGON_SCALES);
        mappings.put("dragon_bow_nether", DMItems.NETHER_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_nether", DMItems.NETHER_DRAGON_SCALE_SHIELD);
        mappings.put("nether_dragon_sword", DMItems.NETHER_DRAGON_SCALE_SWORD);
        mappings.put("nether_dragon_axe", DMItems.NETHER_DRAGON_SCALE_AXE);
        mappings.put("nether_dragon_pickaxe", DMItems.NETHER_DRAGON_SCALE_PICKAXE);
        mappings.put("nether_dragon_hoe", DMItems.NETHER_DRAGON_SCALE_HOE);
        mappings.put("nether_dragon_shovel", DMItems.NETHER_DRAGON_SCALE_SHOVEL);
        mappings.put("nether2_dragonscales", DMItems.NETHER_DRAGON_SCALES);
        mappings.put("dragon_bow_nether2", DMItems.NETHER_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_nether2", DMItems.NETHER_DRAGON_SCALE_SHIELD);
        mappings.put("nether2_dragon_sword", DMItems.NETHER_DRAGON_SCALE_SWORD);
        mappings.put("nether2_dragon_axe", DMItems.NETHER_DRAGON_SCALE_AXE);
        mappings.put("nether2_dragon_pickaxe", DMItems.NETHER_DRAGON_SCALE_PICKAXE);
        mappings.put("nether2_dragon_hoe", DMItems.NETHER_DRAGON_SCALE_HOE);
        mappings.put("nether2_dragon_shovel", DMItems.NETHER_DRAGON_SCALE_SHOVEL);
        suit = DMItems.NETHER_DRAGON_SCALE_ARMORS;
        mappings.put("nether_dragonscale_cap", suit.helmet);
        mappings.put("nether_dragonscale_tunic", suit.chestplate);
        mappings.put("nether_dragonscale_leggings", suit.leggings);
        mappings.put("nether_dragonscale_boots", suit.boots);
        mappings.put("nether2_dragonscale_cap", suit.helmet);
        mappings.put("nether2_dragonscale_tunic", suit.chestplate);
        mappings.put("nether2_dragonscale_leggings", suit.leggings);
        mappings.put("nether2_dragonscale_boots", suit.boots);
        mappings.put("ender_dragonscales", DMItems.ENDER_DRAGON_SCALES);
        mappings.put("dragon_bow_end", DMItems.ENDER_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_end", DMItems.ENDER_DRAGON_SCALE_SHIELD);
        mappings.put("ender_dragon_sword", DMItems.ENDER_DRAGON_SCALE_SWORD);
        mappings.put("ender_dragon_axe", DMItems.ENDER_DRAGON_SCALE_AXE);
        mappings.put("ender_dragon_pickaxe", DMItems.ENDER_DRAGON_SCALE_PICKAXE);
        mappings.put("ender_dragon_hoe", DMItems.ENDER_DRAGON_SCALE_HOE);
        mappings.put("ender_dragon_shovel", DMItems.ENDER_DRAGON_SCALE_SHOVEL);
        suit = DMItems.ENDER_DRAGON_SCALE_ARMORS;
        mappings.put("ender_dragonscale_cap", suit.helmet);
        mappings.put("ender_dragonscale_tunic", suit.chestplate);
        mappings.put("ender_dragonscale_leggings", suit.leggings);
        mappings.put("ender_dragonscale_boots", suit.boots);
        mappings.put("enchant_dragonscales", DMItems.ENCHANTED_DRAGON_SCALES);
        mappings.put("dragon_bow_enchant", DMItems.ENCHANTED_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_enchant", DMItems.ENCHANTED_DRAGON_SCALE_SHIELD);
        mappings.put("enchant_dragon_sword", DMItems.ENCHANTED_DRAGON_SCALE_SWORD);
        mappings.put("enchant_dragon_axe", DMItems.ENCHANTED_DRAGON_SCALE_AXE);
        mappings.put("enchant_dragon_pickaxe", DMItems.ENCHANTED_DRAGON_SCALE_PICKAXE);
        mappings.put("enchant_dragon_hoe", DMItems.ENCHANTED_DRAGON_SCALE_HOE);
        mappings.put("enchant_dragon_shovel", DMItems.ENCHANTED_DRAGON_SCALE_SHOVEL);
        mappings.put("enchant_dragon_amulet", DMItems.ENCHANTED_DRAGON_AMULET);
        mappings.put("enchant_dragon_essence", DMItems.ENCHANTED_DRAGON_ESSENCE);
        suit = DMItems.ENCHANTED_DRAGON_SCALE_ARMORS;
        mappings.put("enchant_dragonscale_cap", suit.helmet);
        mappings.put("enchant_dragonscale_tunic", suit.chestplate);
        mappings.put("enchant_dragonscale_leggings", suit.leggings);
        mappings.put("enchant_dragonscale_boots", suit.boots);
        mappings.put("sunlight_dragonscales", DMItems.SUNLIGHT_DRAGON_SCALES);
        mappings.put("dragon_bow_sunlight", DMItems.SUNLIGHT_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_sunlight", DMItems.SUNLIGHT_DRAGON_SCALE_SHIELD);
        mappings.put("sunlight_dragon_sword", DMItems.SUNLIGHT_DRAGON_SCALE_SWORD);
        mappings.put("sunlight_dragon_axe", DMItems.SUNLIGHT_DRAGON_SCALE_AXE);
        mappings.put("sunlight_dragon_pickaxe", DMItems.SUNLIGHT_DRAGON_SCALE_PICKAXE);
        mappings.put("sunlight_dragon_hoe", DMItems.SUNLIGHT_DRAGON_SCALE_HOE);
        mappings.put("sunlight_dragon_shovel", DMItems.SUNLIGHT_DRAGON_SCALE_SHOVEL);
        mappings.put("sunlight2_dragonscales", DMItems.SUNLIGHT_DRAGON_SCALES);
        mappings.put("dragon_bow_sunlight2", DMItems.SUNLIGHT_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_sunlight2", DMItems.SUNLIGHT_DRAGON_SCALE_SHIELD);
        mappings.put("sunlight2_dragon_sword", DMItems.SUNLIGHT_DRAGON_SCALE_SWORD);
        mappings.put("sunlight2_dragon_axe", DMItems.SUNLIGHT_DRAGON_SCALE_AXE);
        mappings.put("sunlight2_dragon_pickaxe", DMItems.SUNLIGHT_DRAGON_SCALE_PICKAXE);
        mappings.put("sunlight2_dragon_hoe", DMItems.SUNLIGHT_DRAGON_SCALE_HOE);
        mappings.put("sunlight2_dragon_shovel", DMItems.SUNLIGHT_DRAGON_SCALE_SHOVEL);
        suit = DMItems.SUNLIGHT_DRAGON_SCALE_ARMORS;
        mappings.put("sunlight_dragonscale_cap", suit.helmet);
        mappings.put("sunlight_dragonscale_tunic", suit.chestplate);
        mappings.put("sunlight_dragonscale_leggings", suit.leggings);
        mappings.put("sunlight_dragonscale_boots", suit.boots);
        mappings.put("sunlight2_dragonscale_cap", suit.helmet);
        mappings.put("sunlight2_dragonscale_tunic", suit.chestplate);
        mappings.put("sunlight2_dragonscale_leggings", suit.leggings);
        mappings.put("sunlight2_dragonscale_boots", suit.boots);
        mappings.put("moonlight_dragonscales", DMItems.MOONLIGHT_DRAGON_SCALES);
        mappings.put("dragon_bow_moonlight", DMItems.MOONLIGHT_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_moonlight", DMItems.MOONLIGHT_DRAGON_SCALE_SHIELD);
        mappings.put("moonlight_dragon_sword", DMItems.MOONLIGHT_DRAGON_SCALE_SWORD);
        mappings.put("moonlight_dragon_axe", DMItems.MOONLIGHT_DRAGON_SCALE_AXE);
        mappings.put("moonlight_dragon_pickaxe", DMItems.MOONLIGHT_DRAGON_SCALE_PICKAXE);
        mappings.put("moonlight_dragon_hoe", DMItems.MOONLIGHT_DRAGON_SCALE_HOE);
        mappings.put("moonlight_dragon_shovel", DMItems.MOONLIGHT_DRAGON_SCALE_SHOVEL);
        mappings.put("moonlight2_dragonscales", DMItems.MOONLIGHT_DRAGON_SCALES);
        mappings.put("dragon_bow_moonlight2", DMItems.MOONLIGHT_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_moonlight2", DMItems.MOONLIGHT_DRAGON_SCALE_SHIELD);
        mappings.put("moonlight2_dragon_sword", DMItems.MOONLIGHT_DRAGON_SCALE_SWORD);
        mappings.put("moonlight2_dragon_axe", DMItems.MOONLIGHT_DRAGON_SCALE_AXE);
        mappings.put("moonlight2_dragon_pickaxe", DMItems.MOONLIGHT_DRAGON_SCALE_PICKAXE);
        mappings.put("moonlight2_dragon_hoe", DMItems.MOONLIGHT_DRAGON_SCALE_HOE);
        mappings.put("moonlight2_dragon_shovel", DMItems.MOONLIGHT_DRAGON_SCALE_SHOVEL);
        suit = DMItems.MOONLIGHT_DRAGON_SCALE_ARMORS;
        mappings.put("moonlight_dragonscale_cap", suit.helmet);
        mappings.put("moonlight_dragonscale_tunic", suit.chestplate);
        mappings.put("moonlight_dragonscale_leggings", suit.leggings);
        mappings.put("moonlight_dragonscale_boots", suit.boots);
        mappings.put("moonlight2_dragonscale_cap", suit.helmet);
        mappings.put("moonlight2_dragonscale_tunic", suit.chestplate);
        mappings.put("moonlight2_dragonscale_leggings", suit.leggings);
        mappings.put("moonlight2_dragonscale_boots", suit.boots);
        mappings.put("storm_dragonscales", DMItems.STORM_DRAGON_SCALES);
        mappings.put("dragon_bow_storm", DMItems.STORM_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_storm", DMItems.STORM_DRAGON_SCALE_SHIELD);
        mappings.put("storm_dragon_sword", DMItems.STORM_DRAGON_SCALE_SWORD);
        mappings.put("storm_dragon_axe", DMItems.STORM_DRAGON_SCALE_AXE);
        mappings.put("storm_dragon_pickaxe", DMItems.STORM_DRAGON_SCALE_PICKAXE);
        mappings.put("storm_dragon_hoe", DMItems.STORM_DRAGON_SCALE_HOE);
        mappings.put("storm_dragon_shovel", DMItems.STORM_DRAGON_SCALE_SHOVEL);
        mappings.put("storm2_dragonscales", DMItems.STORM_DRAGON_SCALES);
        mappings.put("dragon_bow_storm2", DMItems.STORM_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_storm2", DMItems.STORM_DRAGON_SCALE_SHIELD);
        mappings.put("storm2_dragon_sword", DMItems.STORM_DRAGON_SCALE_SWORD);
        mappings.put("storm2_dragon_axe", DMItems.STORM_DRAGON_SCALE_AXE);
        mappings.put("storm2_dragon_pickaxe", DMItems.STORM_DRAGON_SCALE_PICKAXE);
        mappings.put("storm2_dragon_hoe", DMItems.STORM_DRAGON_SCALE_HOE);
        mappings.put("storm2_dragon_shovel", DMItems.STORM_DRAGON_SCALE_SHOVEL);
        suit = DMItems.STORM_DRAGON_SCALE_ARMORS;
        mappings.put("storm_dragonscale_cap", suit.helmet);
        mappings.put("storm_dragonscale_tunic", suit.chestplate);
        mappings.put("storm_dragonscale_leggings", suit.leggings);
        mappings.put("storm_dragonscale_boots", suit.boots);
        mappings.put("storm2_dragonscale_cap", suit.helmet);
        mappings.put("storm2_dragonscale_tunic", suit.chestplate);
        mappings.put("storm2_dragonscale_leggings", suit.leggings);
        mappings.put("storm2_dragonscale_boots", suit.boots);
        mappings.put("terra_dragonscales", DMItems.TERRA_DRAGON_SCALES);
        mappings.put("dragon_bow_terra", DMItems.TERRA_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_terra", DMItems.TERRA_DRAGON_SCALE_SHIELD);
        mappings.put("terra_dragon_sword", DMItems.TERRA_DRAGON_SCALE_SWORD);
        mappings.put("terra_dragon_axe", DMItems.TERRA_DRAGON_SCALE_AXE);
        mappings.put("terra_dragon_pickaxe", DMItems.TERRA_DRAGON_SCALE_PICKAXE);
        mappings.put("terra_dragon_hoe", DMItems.TERRA_DRAGON_SCALE_HOE);
        mappings.put("terra_dragon_shovel", DMItems.TERRA_DRAGON_SCALE_SHOVEL);
        mappings.put("terra2_dragonscales", DMItems.TERRA_DRAGON_SCALES);
        mappings.put("dragon_bow_terra2", DMItems.TERRA_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_terra2", DMItems.TERRA_DRAGON_SCALE_SHIELD);
        mappings.put("terra2_dragon_sword", DMItems.TERRA_DRAGON_SCALE_SWORD);
        mappings.put("terra2_dragon_axe", DMItems.TERRA_DRAGON_SCALE_AXE);
        mappings.put("terra2_dragon_pickaxe", DMItems.TERRA_DRAGON_SCALE_PICKAXE);
        mappings.put("terra2_dragon_hoe", DMItems.TERRA_DRAGON_SCALE_HOE);
        mappings.put("terra2_dragon_shovel", DMItems.TERRA_DRAGON_SCALE_SHOVEL);
        suit = DMItems.TERRA_DRAGON_SCALE_ARMORS;
        mappings.put("terra_dragonscale_cap", suit.helmet);
        mappings.put("terra_dragonscale_tunic", suit.chestplate);
        mappings.put("terra_dragonscale_leggings", suit.leggings);
        mappings.put("terra_dragonscale_boots", suit.boots);
        mappings.put("terra2_dragonscale_cap", suit.helmet);
        mappings.put("terra2_dragonscale_tunic", suit.chestplate);
        mappings.put("terra2_dragonscale_leggings", suit.leggings);
        mappings.put("terra2_dragonscale_boots", suit.boots);
        mappings.put("zombie_dragonscales", DMItems.ZOMBIE_DRAGON_SCALES);
        mappings.put("dragon_bow_zombie", DMItems.ZOMBIE_DRAGON_SCALE_BOW);
        mappings.put("dragon_shield_zombie", DMItems.ZOMBIE_DRAGON_SCALE_SHIELD);
        mappings.put("zombie_dragon_sword", DMItems.ZOMBIE_DRAGON_SCALE_SWORD);
        mappings.put("zombie_dragon_axe", DMItems.ZOMBIE_DRAGON_SCALE_AXE);
        mappings.put("zombie_dragon_pickaxe", DMItems.ZOMBIE_DRAGON_SCALE_PICKAXE);
        mappings.put("zombie_dragon_hoe", DMItems.ZOMBIE_DRAGON_SCALE_HOE);
        mappings.put("zombie_dragon_shovel", DMItems.ZOMBIE_DRAGON_SCALE_SHOVEL);
        suit = DMItems.ZOMBIE_DRAGON_SCALE_ARMORS;
        mappings.put("zombie_dragonscale_cap", suit.helmet);
        mappings.put("zombie_dragonscale_tunic", suit.chestplate);
        mappings.put("zombie_dragonscale_leggings", suit.leggings);
        mappings.put("zombie_dragonscale_boots", suit.boots);
        ITEM_MAPPINGS = mappings;
    }

    private DragonMountsCompat() {}
}
