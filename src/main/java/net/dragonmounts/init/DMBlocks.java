package net.dragonmounts.init;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.block.DragonCoreBlock;
import net.dragonmounts.block.DragonNestBlock;
import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.registry.DragonType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;


public class DMBlocks {
    public static final ObjectArrayList<Block> BLOCKS = new ObjectArrayList<>();
    public static final DragonNestBlock DRAGON_NEST = register("dragon_nest", new DragonNestBlock());
    public static final DragonCoreBlock DRAGON_CORE = register("dragon_core", new DragonCoreBlock());
    public static final HatchableDragonEggBlock AETHER_DRAGON_EGG = registerDragonEgg("aether_dragon_egg", DragonTypes.AETHER);
    public static final HatchableDragonEggBlock ENCHANT_DRAGON_EGG = registerDragonEgg("enchant_dragon_egg", DragonTypes.ENCHANT);
    public static final HatchableDragonEggBlock ENDER_DRAGON_EGG = registerDragonEgg("ender_dragon_egg", DragonTypes.ENDER);
    public static final HatchableDragonEggBlock FIRE_DRAGON_EGG = registerDragonEgg("fire_dragon_egg", DragonTypes.FIRE);
    public static final HatchableDragonEggBlock FOREST_DRAGON_EGG = registerDragonEgg("forest_dragon_egg", DragonTypes.FOREST);
    public static final HatchableDragonEggBlock ICE_DRAGON_EGG = registerDragonEgg("ice_dragon_egg", DragonTypes.ICE);
    public static final HatchableDragonEggBlock MOONLIGHT_DRAGON_EGG = registerDragonEgg("moonlight_dragon_egg", DragonTypes.MOONLIGHT);
    public static final HatchableDragonEggBlock NETHER_DRAGON_EGG = registerDragonEgg("nether_dragon_egg", DragonTypes.NETHER);
    public static final HatchableDragonEggBlock SKELETON_DRAGON_EGG = registerDragonEgg("skeleton_dragon_egg", DragonTypes.SKELETON);
    public static final HatchableDragonEggBlock STORM_DRAGON_EGG = registerDragonEgg("storm_dragon_egg", DragonTypes.STORM);
    public static final HatchableDragonEggBlock SUNLIGHT_DRAGON_EGG = registerDragonEgg("sunlight_dragon_egg", DragonTypes.SUNLIGHT);
    public static final HatchableDragonEggBlock TERRA_DRAGON_EGG = registerDragonEgg("terra_dragon_egg", DragonTypes.TERRA);
    public static final HatchableDragonEggBlock WATER_DRAGON_EGG = registerDragonEgg("water_dragon_egg", DragonTypes.WATER);
    public static final HatchableDragonEggBlock WITHER_DRAGON_EGG = registerDragonEgg("wither_dragon_egg", DragonTypes.WITHER);
    public static final HatchableDragonEggBlock ZOMBIE_DRAGON_EGG = registerDragonEgg("zombie_dragon_egg", DragonTypes.ZOMBIE);

    static <T extends Block> T register(String name, T block) {
        BLOCKS.add(block.setTranslationKey(name).setRegistryName(name));
        return block;
    }

    static HatchableDragonEggBlock registerDragonEgg(String name, DragonType type) {
        HatchableDragonEggBlock block = new HatchableDragonEggBlock(type);
        type.bindInstance(HatchableDragonEggBlock.class, block);
        BLOCKS.add(block.setRegistryName(name));
        DMItems.ITEMS.add(new ItemBlock(block).setRegistryName(name));
        return block;
    }
}