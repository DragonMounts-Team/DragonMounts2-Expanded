package net.dragonmounts.init;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.block.DragonCoreBlock;
import net.dragonmounts.block.DragonNestBlock;
import net.dragonmounts.block.DragonScaleBlock;
import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.item.CraftableBlockItem;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.BlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

public class DMBlocks {
    public static final ObjectArrayList<Block> BLOCKS = new ObjectArrayList<>();
    public static final DragonNestBlock DRAGON_NEST = register("dragon_nest", new DragonNestBlock());
    public static final DragonCoreBlock DRAGON_CORE = register("dragon_core", new DragonCoreBlock());
    public static final DragonScaleBlock AETHER_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock WATER_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock ICE_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock FIRE_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock FOREST_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock NETHER_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock ENDER_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock ENCHANT_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock SUNLIGHT_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock MOONLIGHT_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock STORM_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock TERRA_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock ZOMBIE_DRAGON_SCALE_BLOCK;
    public static final DragonScaleBlock DARK_DRAGON_SCALE_BLOCK;
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

    static DragonScaleBlock registerDragonScaleBlock(String name, DragonType type, BlockProperties props) {
        DragonScaleBlock block = new DragonScaleBlock(type, props);
        block.setHarvestLevel("pickaxe", 3);
        type.bindInstance(DragonScaleBlock.class, block);
        BLOCKS.add(block.setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setTranslationKey(DragonScaleBlock.TRANSLATION_KEY).setRegistryName(name));
        DMItems.ITEMS.add(new CraftableBlockItem(block, props.creativeTab).setRegistryName(name));
        return block;
    }

    static {
        BlockProperties props = new BlockProperties()
                .setSoundType(SoundType.STONE)
                .setHardness(0)
                .setResistance(30)
                .setLightLevel(0.125F)
                .setCreativeTab(DMItemGroups.BLOCKS);
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
        props = new BlockProperties()
                .setMaterial(Material.IRON)
                .setSoundType(SoundType.WOOD)
                .setHardness(4)
                .setResistance(20)
                .setCreativeTab(DMItemGroups.BLOCKS);
        AETHER_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("aether_dragon_scale_block", DragonTypes.AETHER, props.setColor(MapColor.LIGHT_BLUE));
        WATER_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("water_dragon_scale_block", DragonTypes.WATER, props.setColor(MapColor.BLUE));
        ICE_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("ice_dragon_scale_block", DragonTypes.ICE, props.setColor(MapColor.ICE));
        FIRE_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("fire_dragon_scale_block", DragonTypes.FIRE, props.setColor(MapColor.RED));
        FOREST_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("forest_dragon_scale_block", DragonTypes.FOREST, props.setColor(MapColor.GRASS));
        NETHER_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("nether_dragon_scale_block", DragonTypes.NETHER, props.setColor(MapColor.NETHERRACK));
        ENDER_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("ender_dragon_scale_block", DragonTypes.ENDER, props.setColor(MapColor.PURPLE));
        ENCHANT_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("enchant_dragon_scale_block", DragonTypes.ENCHANT, props.setColor(MapColor.MAGENTA));
        SUNLIGHT_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("sunlight_dragon_scale_block", DragonTypes.SUNLIGHT, props.setColor(MapColor.YELLOW));
        MOONLIGHT_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("moonlight_dragon_scale_block", DragonTypes.MOONLIGHT, props.setColor(MapColor.SILVER));
        STORM_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("storm_dragon_scale_block", DragonTypes.STORM, props.setColor(MapColor.CYAN));
        TERRA_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("terra_dragon_scale_block", DragonTypes.TERRA, props.setColor(MapColor.BROWN));
        ZOMBIE_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("zombie_dragon_scale_block", DragonTypes.ZOMBIE, props.setColor(MapColor.GREEN));
        DARK_DRAGON_SCALE_BLOCK = registerDragonScaleBlock("dark_dragon_scale_block", DragonTypes.DARK, props.setColor(MapColor.BLACK));
    }
}