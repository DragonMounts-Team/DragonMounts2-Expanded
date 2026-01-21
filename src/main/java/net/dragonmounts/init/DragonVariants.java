package net.dragonmounts.init;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.dragonmounts.DragonMounts;
import net.dragonmounts.block.DragonHeadBlock;
import net.dragonmounts.block.DragonHeadStandingBlock;
import net.dragonmounts.block.DragonHeadWallBlock;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.item.DragonHeadItem;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.block.material.Material;

import java.util.function.Function;

import static net.dragonmounts.DragonMounts.applyId;
import static net.dragonmounts.DragonMounts.makeId;

public class DragonVariants {
    public static final ObjectList<DragonVariant> BUILTIN_VALUES;
    public static final DragonVariant AETHER_FEMALE;
    public static final DragonVariant AETHER_MALE;
    public static final DragonVariant BREEZE;
    public static final DragonVariant DARK_FEMALE;
    public static final DragonVariant DARK_MALE;
    public static final DragonVariant ENCHANTED_FEMALE;
    public static final DragonVariant ENCHANTED_MALE;
    public static final DragonVariant ENCHANTING_TABLE;
    public static final DragonVariant ENDER_FEMALE;
    public static final DragonVariant ENDER_MALE;
    public static final DragonVariant ENDER_RARE;
    public static final DragonVariant FIRE_FEMALE;
    public static final DragonVariant FIRE_MALE;
    public static final DragonVariant BLUE_FIRE;
    public static final DragonVariant FOREST_FEMALE;
    public static final DragonVariant FOREST_MALE;
    public static final DragonVariant FOREST_DRY_FEMALE;
    public static final DragonVariant FOREST_DRY_MALE;
    public static final DragonVariant FOREST_TAIGA_FEMALE;
    public static final DragonVariant FOREST_TAIGA_MALE;
    public static final DragonVariant ICE_FEMALE;
    public static final DragonVariant ICE_MALE;
    public static final DragonVariant MOONLIGHT_FEMALE;
    public static final DragonVariant MOONLIGHT_MALE;
    public static final DragonVariant ECLIPSE;
    public static final DragonVariant NETHER_FEMALE;
    public static final DragonVariant NETHER_MALE;
    public static final DragonVariant SOUL;
    public static final DragonVariant SKELETON;
    public static final DragonVariant STRAY;
    public static final DragonVariant BOGGED;
    public static final DragonVariant STORM_FEMALE;
    public static final DragonVariant STORM_MALE;
    public static final DragonVariant BRONZED_STORM;
    public static final DragonVariant SUNLIGHT_FEMALE;
    public static final DragonVariant SUNLIGHT_MALE;
    public static final DragonVariant AURORA;
    public static final DragonVariant TERRA_FEMALE;
    public static final DragonVariant TERRA_MALE;
    public static final DragonVariant CRYSTAL;
    public static final DragonVariant WATER_FEMALE;
    public static final DragonVariant WATER_MALE;
    public static final DragonVariant BRINE;
    public static final DragonVariant WITHER;
    public static final DragonVariant ZOMBIE;

    static DragonVariant make(Function<? super String, VariantAppearance> supplier, DragonType type, String name) {
        return new DragonVariant(type, makeId(name), supplier.apply(name), variant -> {
            final String key = DragonHeadBlock.TRANSLATION_KEY;
            DragonHeadItem item = new DragonHeadItem(variant);
            DragonHeadStandingBlock standing = new DragonHeadStandingBlock(Material.CIRCUITS, variant);
            DragonHeadWallBlock wall = new DragonHeadWallBlock(Material.CIRCUITS, variant);
            String full = name + "_dragon_head_wall";
            String base = full.substring(0, full.length() - 5);
            applyId(item, base).setCreativeTab(DMItemGroups.BLOCKS).setTranslationKey(key);
            applyId(standing, base).setTranslationKey(key);
            applyId(wall, full).setTranslationKey(key);
            return new DragonHeadBlock.Holder(standing, wall, item);
        });
    }

    static {
        Function<? super String, VariantAppearance> appearance = DragonMounts.PROXY.getBuiltinAppearances();
        ObjectArrayList<DragonVariant> values = new ObjectArrayList<>(45);
        values.add(AETHER_FEMALE = make(appearance, DragonTypes.AETHER, "aether_female"));
        values.add(AETHER_MALE = make(appearance, DragonTypes.AETHER, "aether_male"));
        values.add(BREEZE = make(appearance, DragonTypes.AETHER, "breeze"));
        values.add(DARK_FEMALE = make(appearance, DragonTypes.DARK, "dark_female"));
        values.add(DARK_MALE = make(appearance, DragonTypes.DARK, "dark_male"));
        values.add(ENCHANTED_FEMALE = make(appearance, DragonTypes.ENCHANTED, "enchanted_female"));
        values.add(ENCHANTED_MALE = make(appearance, DragonTypes.ENCHANTED, "enchanted_male"));
        values.add(ENCHANTING_TABLE = make(appearance, DragonTypes.ENCHANTED, "enchanting_table"));
        values.add(ENDER_FEMALE = make(appearance, DragonTypes.ENDER, "ender_female"));
        values.add(ENDER_MALE = make(appearance, DragonTypes.ENDER, "ender_male"));
        values.add(ENDER_RARE = make(appearance, DragonTypes.ENDER, "ender_rare"));
        values.add(FIRE_FEMALE = make(appearance, DragonTypes.FIRE, "fire_female"));
        values.add(FIRE_MALE = make(appearance, DragonTypes.FIRE, "fire_male"));
        values.add(BLUE_FIRE = make(appearance, DragonTypes.FIRE, "blue_fire"));
        values.add(FOREST_FEMALE = make(appearance, DragonTypes.FOREST, "forest_female"));
        values.add(FOREST_MALE = make(appearance, DragonTypes.FOREST, "forest_male"));
        values.add(FOREST_DRY_FEMALE = make(appearance, DragonTypes.FOREST, "forest_dry_female"));
        values.add(FOREST_DRY_MALE = make(appearance, DragonTypes.FOREST, "forest_dry_male"));
        values.add(FOREST_TAIGA_FEMALE = make(appearance, DragonTypes.FOREST, "forest_taiga_female"));
        values.add(FOREST_TAIGA_MALE = make(appearance, DragonTypes.FOREST, "forest_taiga_male"));
        values.add(ICE_FEMALE = make(appearance, DragonTypes.ICE, "ice_female"));
        values.add(ICE_MALE = make(appearance, DragonTypes.ICE, "ice_male"));
        values.add(MOONLIGHT_FEMALE = make(appearance, DragonTypes.MOONLIGHT, "moonlight_female"));
        values.add(MOONLIGHT_MALE = make(appearance, DragonTypes.MOONLIGHT, "moonlight_male"));
        values.add(ECLIPSE = make(appearance, DragonTypes.MOONLIGHT, "eclipse"));
        values.add(NETHER_FEMALE = make(appearance, DragonTypes.NETHER, "nether_female"));
        values.add(NETHER_MALE = make(appearance, DragonTypes.NETHER, "nether_male"));
        values.add(SOUL = make(appearance, DragonTypes.NETHER, "soul"));
        values.add(SKELETON = make(appearance, DragonTypes.SKELETON, "skeleton"));
        values.add(STRAY = make(appearance, DragonTypes.SKELETON, "stray"));
        values.add(BOGGED = make(appearance, DragonTypes.SKELETON, "bogged"));
        values.add(STORM_FEMALE = make(appearance, DragonTypes.STORM, "storm_female"));
        values.add(STORM_MALE = make(appearance, DragonTypes.STORM, "storm_male"));
        values.add(BRONZED_STORM = make(appearance, DragonTypes.STORM, "bronzed_storm"));
        values.add(SUNLIGHT_FEMALE = make(appearance, DragonTypes.SUNLIGHT, "sunlight_female"));
        values.add(SUNLIGHT_MALE = make(appearance, DragonTypes.SUNLIGHT, "sunlight_male"));
        values.add(AURORA = make(appearance, DragonTypes.SUNLIGHT, "aurora"));
        values.add(TERRA_FEMALE = make(appearance, DragonTypes.TERRA, "terra_female"));
        values.add(TERRA_MALE = make(appearance, DragonTypes.TERRA, "terra_male"));
        values.add(CRYSTAL = make(appearance, DragonTypes.TERRA, "crystal"));
        values.add(WATER_FEMALE = make(appearance, DragonTypes.WATER, "water_female"));
        values.add(WATER_MALE = make(appearance, DragonTypes.WATER, "water_male"));
        values.add(BRINE = make(appearance, DragonTypes.WATER, "brine"));
        values.add(WITHER = make(appearance, DragonTypes.WITHER, "wither"));
        values.add(ZOMBIE = make(appearance, DragonTypes.ZOMBIE, "zombie"));
        BUILTIN_VALUES = ObjectLists.unmodifiable(values);
    }
}
