package net.dragonmounts.init;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.block.*;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.BlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.item.ItemBlock;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

public class DMBlocks {
    public static final ObjectArrayList<Block> BLOCKS = new ObjectArrayList<>();
    public static final DragonNestBlock DRAGON_NEST = register("dragon_nest", new DragonNestBlock());
    public static final DragonCoreBlock DRAGON_CORE = register("dragon_core", new DragonCoreBlock());

    public static final Block AETHER_DRAGON_SCALES_BLOCK = register("aether_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.AETHER_DRAGON_SCALES));
    public static final Block WATER_DRAGON_SCALES_BLOCK = register("water_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.WATER_DRAGON_SCALES));
    public static final Block ICE_DRAGON_SCALES_BLOCK = register("ice_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.ICE_DRAGON_SCALES));
    public static final Block FIRE_DRAGON_SCALES_BLOCK = register("fire_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.FIRE_DRAGON_SCALES));
    public static final Block FOREST_DRAGON_SCALES_BLOCK = register("forest_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.FOREST_DRAGON_SCALES));
    public static final Block NETHER_DRAGON_SCALES_BLOCK = register("nether_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.NETHER_DRAGON_SCALES));
    public static final Block ENDER_DRAGON_SCALES_BLOCK = register("ender_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.ENDER_DRAGON_SCALES));
    public static final Block ENCHANT_DRAGON_SCALES_BLOCK = register("enchant_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.ENCHANT_DRAGON_SCALES));
    public static final Block SUNLIGHT_DRAGON_SCALES_BLOCK = register("sunlight_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.SUNLIGHT_DRAGON_SCALES));
    public static final Block MOONLIGHT_DRAGON_SCALES_BLOCK = register("moonlight_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.MOONLIGHT_DRAGON_SCALES));
    public static final Block STORM_DRAGON_SCALES_BLOCK = register("storm_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.STORM_DRAGON_SCALES));
    public static final Block TERRA_DRAGON_SCALES_BLOCK = register("terra_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.TERRA_DRAGON_SCALES));
    public static final Block ZOMBIE_DRAGON_SCALES_BLOCK = register("zombie_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.ZOMBIE_DRAGON_SCALES));
    public static final Block DARK_DRAGON_SCALES_BLOCK = register("dark_dragon_scales_block", new DragonScalesBlock(DragonMountMaterial.DARK_DRAGON_SCALES));



    public static final HatchableDragonEggBlock AETHER_DRAGON_EGG;
    public static final HatchableDragonEggBlock ENCHANT_DRAGON_EGG;
    public static final HatchableDragonEggBlock ENDER_DRAGON_EGG;
    public static final HatchableDragonEggBlock FIRE_DRAGON_EGG;
    public static final HatchableDragonEggBlock FOREST_DRAGON_EGG;
    public static final HatchableDragonEggBlock ICE_DRAGON_EGG;
    public static final HatchableDragonEggBlock MOONLIGHT_DRAGON_EGG;
    public static final HatchableDragonEggBlock NETHER_DRAGON_EGG;
    public static final HatchableDragonEggBlock SKELETON_DRAGON_EGG;
    public static final HatchableDragonEggBlock STORM_DRAGON_EGG;
    public static final HatchableDragonEggBlock SUNLIGHT_DRAGON_EGG;
    public static final HatchableDragonEggBlock TERRA_DRAGON_EGG;
    public static final HatchableDragonEggBlock WATER_DRAGON_EGG;
    public static final HatchableDragonEggBlock WITHER_DRAGON_EGG;
    public static final HatchableDragonEggBlock ZOMBIE_DRAGON_EGG;
    public static final HatchableDragonEggBlock DARK_DRAGON_EGG;

    static <T extends Block> T register(String name, T block) {
        BLOCKS.add(block.setTranslationKey(TRANSLATION_KEY_PREFIX + name).setRegistryName(name));
        return block;
    }

    static HatchableDragonEggBlock registerDragonEgg(String name, DragonType type, BlockProperties props) {
        HatchableDragonEggBlock block = new HatchableDragonEggBlock(type, props);
        type.bindInstance(HatchableDragonEggBlock.class, block);
        BLOCKS.add(block.setTranslationKey(HatchableDragonEggBlock.TRANSLATION_KEY).setRegistryName(name));
        DMItems.ITEMS.add(new ItemBlock(block).setRegistryName(name));
        return block;
    }

    static {
        BlockProperties props = new BlockProperties()
                .setSoundType(SoundType.STONE)
                .setHardness(0)
                .setResistance(30)
                .setLightLevel(0.125F)
                .setCreativeTab(DMItemGroups.MAIN);
        AETHER_DRAGON_EGG = registerDragonEgg("aether_dragon_egg", DragonTypes.AETHER, props);
        ENCHANT_DRAGON_EGG = registerDragonEgg("enchant_dragon_egg", DragonTypes.ENCHANT, props);
        ENDER_DRAGON_EGG = registerDragonEgg("ender_dragon_egg", DragonTypes.ENDER, props);
        FIRE_DRAGON_EGG = registerDragonEgg("fire_dragon_egg", DragonTypes.FIRE, props);
        FOREST_DRAGON_EGG = registerDragonEgg("forest_dragon_egg", DragonTypes.FOREST, props);
        ICE_DRAGON_EGG = registerDragonEgg("ice_dragon_egg", DragonTypes.ICE, props);
        MOONLIGHT_DRAGON_EGG = registerDragonEgg("moonlight_dragon_egg", DragonTypes.MOONLIGHT, props);
        NETHER_DRAGON_EGG = registerDragonEgg("nether_dragon_egg", DragonTypes.NETHER, props.setLightLevel(0.1875F));
        SKELETON_DRAGON_EGG = registerDragonEgg("skeleton_dragon_egg", DragonTypes.SKELETON, props.setLightLevel(0.125F));
        STORM_DRAGON_EGG = registerDragonEgg("storm_dragon_egg", DragonTypes.STORM, props);
        SUNLIGHT_DRAGON_EGG = registerDragonEgg("sunlight_dragon_egg", DragonTypes.SUNLIGHT, props);
        TERRA_DRAGON_EGG = registerDragonEgg("terra_dragon_egg", DragonTypes.TERRA, props);
        WATER_DRAGON_EGG = registerDragonEgg("water_dragon_egg", DragonTypes.WATER, props);
        WITHER_DRAGON_EGG = registerDragonEgg("wither_dragon_egg", DragonTypes.WITHER, props);
        ZOMBIE_DRAGON_EGG = registerDragonEgg("zombie_dragon_egg", DragonTypes.ZOMBIE, props);
        DARK_DRAGON_EGG = registerDragonEgg("dark_dragon_egg", DragonTypes.DARK, props);
    }
}