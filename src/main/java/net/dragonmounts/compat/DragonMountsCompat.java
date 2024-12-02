package net.dragonmounts.compat;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.compat.fixer.DMBlockEntityCompat;
import net.dragonmounts.compat.fixer.DragonEntityCompat;
import net.dragonmounts.inits.DMArmors;
import net.dragonmounts.inits.DMBlocks;
import net.dragonmounts.inits.ModItems;
import net.dragonmounts.inits.ModTools;
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
        //Dragon Scales
        ITEM_MAPPINGS.put("fire2_dragonscales", ModItems.FireDragonScales);
        ITEM_MAPPINGS.put("moonlight2_dragonscales", ModItems.MoonlightDragonScales);
        ITEM_MAPPINGS.put("terra2_dragonscales", ModItems.TerraDragonScales);
        ITEM_MAPPINGS.put("storm2_dragonscales", ModItems.StormDragonScales);
        ITEM_MAPPINGS.put("nether2_dragonscales", ModItems.NetherDragonScales);
        ITEM_MAPPINGS.put("sunlight2_dragonscales", ModItems.SunlightDragonScales);
        //Dragon Scale Bow
        ITEM_MAPPINGS.put("dragon_bow_fire2", ModTools.fire_dragon_bow);
        ITEM_MAPPINGS.put("dragon_bow_moonlight2", ModTools.moonlight_dragon_bow);
        ITEM_MAPPINGS.put("dragon_bow_terra2", ModTools.terra_dragon_bow);
        ITEM_MAPPINGS.put("dragon_bow_storm2", ModTools.storm_dragon_bow);
        ITEM_MAPPINGS.put("dragon_bow_nether2", ModTools.nether_dragon_bow);
        ITEM_MAPPINGS.put("dragon_bow_sunlight2", ModTools.sunlight_dragon_bow);
        //Dragon Scale Shield
        ITEM_MAPPINGS.put("dragon_shield_fire2", ModItems.fire_dragon_shield);
        ITEM_MAPPINGS.put("dragon_shield_moonlight2", ModItems.moonlight_dragon_shield);
        ITEM_MAPPINGS.put("dragon_shield_terra2", ModItems.terra_dragon_shield);
        ITEM_MAPPINGS.put("dragon_shield_storm2", ModItems.storm_dragon_shield);
        ITEM_MAPPINGS.put("dragon_shield_nether2", ModItems.nether_dragon_shield);
        ITEM_MAPPINGS.put("dragon_shield_sunlight2", ModItems.sunlight_dragon_shield);
        //Dragon Scale Sword
        ITEM_MAPPINGS.put("fire2_dragon_sword", ModTools.fireDragonSword);
        ITEM_MAPPINGS.put("moonlight2_dragon_sword", ModTools.moonlightDragonSword);
        ITEM_MAPPINGS.put("terra2_dragon_sword", ModTools.terraDragonSword);
        ITEM_MAPPINGS.put("storm2_dragon_sword", ModTools.stormDragonSword);
        ITEM_MAPPINGS.put("nether2_dragon_sword", ModTools.netherDragonSword);
        ITEM_MAPPINGS.put("sunlight2_dragon_sword", ModTools.sunlightDragonSword);
        //Dragon Scale Axe
        ITEM_MAPPINGS.put("fire2_dragon_axe", ModTools.fireDragonAxe);
        ITEM_MAPPINGS.put("moonlight2_dragon_axe", ModTools.moonlightDragonAxe);
        ITEM_MAPPINGS.put("terra2_dragon_axe", ModTools.terraDragonAxe);
        ITEM_MAPPINGS.put("storm2_dragon_axe", ModTools.stormDragonAxe);
        ITEM_MAPPINGS.put("nether2_dragon_axe", ModTools.netherDragonAxe);
        ITEM_MAPPINGS.put("sunlight2_dragon_axe", ModTools.sunlightDragonAxe);
        //Dragon Scale Pickaxe
        ITEM_MAPPINGS.put("fire2_dragon_pickaxe", ModTools.fireDragonPickaxe);
        ITEM_MAPPINGS.put("moonlight2_dragon_pickaxe", ModTools.moonlightDragonPickaxe);
        ITEM_MAPPINGS.put("terra2_dragon_pickaxe", ModTools.terraDragonPickaxe);
        ITEM_MAPPINGS.put("storm2_dragon_pickaxe", ModTools.stormDragonPickaxe);
        ITEM_MAPPINGS.put("nether2_dragon_pickaxe", ModTools.netherDragonPickaxe);
        ITEM_MAPPINGS.put("sunlight2_dragon_pickaxe", ModTools.sunlightDragonPickaxe);
        //Dragon Scale Hoe
        ITEM_MAPPINGS.put("fire2_dragon_hoe", ModTools.fireDragonHoe);
        ITEM_MAPPINGS.put("moonlight2_dragon_hoe", ModTools.moonlightDragonHoe);
        ITEM_MAPPINGS.put("terra2_dragon_hoe", ModTools.terraDragonHoe);
        ITEM_MAPPINGS.put("storm2_dragon_hoe", ModTools.stormDragonHoe);
        ITEM_MAPPINGS.put("nether2_dragon_hoe", ModTools.netherDragonHoe);
        ITEM_MAPPINGS.put("sunlight2_dragon_hoe", ModTools.sunlightDragonHoe);
        //Dragon Scale Shovel
        ITEM_MAPPINGS.put("fire2_dragon_shovel", ModTools.fireDragonShovel);
        ITEM_MAPPINGS.put("moonlight2_dragon_shovel", ModTools.moonlightDragonShovel);
        ITEM_MAPPINGS.put("terra2_dragon_shovel", ModTools.terraDragonShovel);
        ITEM_MAPPINGS.put("storm2_dragon_shovel", ModTools.stormDragonShovel);
        ITEM_MAPPINGS.put("nether2_dragon_shovel", ModTools.netherDragonShovel);
        ITEM_MAPPINGS.put("sunlight2_dragon_shovel", ModTools.sunlightDragonShovel);
        //Dragon Scale Helmet
        ITEM_MAPPINGS.put("fire2_dragonscale_cap", DMArmors.FIRE_DRAGON_SCALE_HELMET);
        ITEM_MAPPINGS.put("moonlight2_dragonscale_cap", DMArmors.MOONLIGHT_DRAGON_SCALE_HELMET);
        ITEM_MAPPINGS.put("terra2_dragonscale_cap", DMArmors.TERRA_DRAGON_SCALE_HELMET);
        ITEM_MAPPINGS.put("storm2_dragonscale_cap", DMArmors.STORM_DRAGON_SCALE_HELMET);
        ITEM_MAPPINGS.put("nether2_dragonscale_cap", DMArmors.NETHER_DRAGON_SCALE_HELMET);
        ITEM_MAPPINGS.put("sunlight2_dragonscale_cap", DMArmors.SUNLIGHT_DRAGON_SCALE_HELMET);
        //Dragon Scale Chestplate
        ITEM_MAPPINGS.put("fire2_dragonscale_tunic", DMArmors.FIRE_DRAGON_SCALE_CHESTPLATE);
        ITEM_MAPPINGS.put("moonlight2_dragonscale_tunic", DMArmors.MOONLIGHT_DRAGON_SCALE_CHESTPLATE);
        ITEM_MAPPINGS.put("terra2_dragonscale_tunic", DMArmors.TERRA_DRAGON_SCALE_CHESTPLATE);
        ITEM_MAPPINGS.put("storm2_dragonscale_tunic", DMArmors.STORM_DRAGON_SCALE_CHESTPLATE);
        ITEM_MAPPINGS.put("nether2_dragonscale_tunic", DMArmors.NETHER_DRAGON_SCALE_CHESTPLATE);
        ITEM_MAPPINGS.put("sunlight2_dragonscale_tunic", DMArmors.SUNLIGHT_DRAGON_SCALE_CHESTPLATE);
        //Dragon Scale Leggings
        ITEM_MAPPINGS.put("fire2_dragonscale_leggings", DMArmors.FIRE_DRAGON_SCALE_LEGGINGS);
        ITEM_MAPPINGS.put("moonlight2_dragonscale_leggings", DMArmors.MOONLIGHT_DRAGON_SCALE_LEGGINGS);
        ITEM_MAPPINGS.put("terra2_dragonscale_leggings", DMArmors.TERRA_DRAGON_SCALE_LEGGINGS);
        ITEM_MAPPINGS.put("storm2_dragonscale_leggings", DMArmors.STORM_DRAGON_SCALE_LEGGINGS);
        ITEM_MAPPINGS.put("nether2_dragonscale_leggings", DMArmors.NETHER_DRAGON_SCALE_LEGGINGS);
        ITEM_MAPPINGS.put("sunlight2_dragonscale_leggings", DMArmors.SUNLIGHT_DRAGON_SCALE_LEGGINGS);
        //Dragon Scale Boots
        ITEM_MAPPINGS.put("fire2_dragonscale_boots", DMArmors.FIRE_DRAGON_SCALE_BOOTS);
        ITEM_MAPPINGS.put("moonlight2_dragonscale_boots", DMArmors.MOONLIGHT_DRAGON_SCALE_BOOTS);
        ITEM_MAPPINGS.put("terra2_dragonscale_boots", DMArmors.TERRA_DRAGON_SCALE_BOOTS);
        ITEM_MAPPINGS.put("storm2_dragonscale_boots", DMArmors.STORM_DRAGON_SCALE_BOOTS);
        ITEM_MAPPINGS.put("nether2_dragonscale_boots", DMArmors.NETHER_DRAGON_SCALE_BOOTS);
        ITEM_MAPPINGS.put("sunlight2_dragonscale_boots", DMArmors.SUNLIGHT_DRAGON_SCALE_BOOTS);
    }

    private DragonMountsCompat() {}
}
