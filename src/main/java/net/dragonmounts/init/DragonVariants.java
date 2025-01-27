package net.dragonmounts.init;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

import static net.dragonmounts.DragonMounts.makeId;

public class DragonVariants {
    public static final ObjectArrayList<DragonVariant> BUILTIN_VALUES = new ObjectArrayList<>();
    public static final DragonVariant AETHER_FEMALE;
    public static final DragonVariant AETHER_MALE;
    public static final DragonVariant DARK;
    public static final DragonVariant ENCHANT_FEMALE;
    public static final DragonVariant ENCHANT_MALE;
    public static final DragonVariant ENDER_FEMALE;
    public static final DragonVariant ENDER_MALE;
    public static final DragonVariant FIRE_FEMALE;
    public static final DragonVariant FIRE_MALE;
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
    public static final DragonVariant NETHER_FEMALE;
    public static final DragonVariant NETHER_MALE;
    public static final DragonVariant SKELETON_FEMALE;
    public static final DragonVariant SKELETON_MALE;
    public static final DragonVariant STORM_FEMALE;
    public static final DragonVariant STORM_MALE;
    public static final DragonVariant SUNLIGHT_FEMALE;
    public static final DragonVariant SUNLIGHT_MALE;
    public static final DragonVariant TERRA_FEMALE;
    public static final DragonVariant TERRA_MALE;
    public static final DragonVariant WATER_FEMALE;
    public static final DragonVariant WATER_MALE;
    public static final DragonVariant WITHER_FEMALE;
    public static final DragonVariant WITHER_MALE;
    public static final DragonVariant ZOMBIE_FEMALE;
    public static final DragonVariant ZOMBIE_MALE;

    static DragonVariant make(Function<String, VariantAppearance> supplier, DragonType type, String name) {
        return new DragonVariant(type, makeId(name), supplier.apply(name), variant -> {
            DragonHeadItem item = new DragonHeadItem(variant);
            DragonHeadStandingBlock standing = new DragonHeadStandingBlock(Material.CIRCUITS, variant);
            DragonHeadWallBlock wall = new DragonHeadWallBlock(Material.CIRCUITS, variant);
            String full = name + "_dragon_head_wall";
            String base = full.substring(0, full.length() - 5);
            item.setCreativeTab(DMItemGroups.MAIN).setTranslationKey("dragon_head").setRegistryName(base);
            standing.setRegistryName(base);
            wall.setRegistryName(full);
            return new DragonHeadBlock.Holder(standing, wall, item);
        });
    }

    static {
        Function<String, VariantAppearance> appearance = DragonMounts.PROXY.getBuiltinAppearances();
        BUILTIN_VALUES.add(AETHER_FEMALE = make(appearance, DragonTypes.AETHER, "aether_female"));
        BUILTIN_VALUES.add(AETHER_MALE = make(appearance, DragonTypes.AETHER, "aether_male"));
        BUILTIN_VALUES.add(DARK = make(appearance, DragonTypes.DARK, "dark"));
        BUILTIN_VALUES.add(ENCHANT_FEMALE = make(appearance, DragonTypes.ENCHANT, "enchant_female"));
        BUILTIN_VALUES.add(ENCHANT_MALE = make(appearance, DragonTypes.ENCHANT, "enchant_male"));
        BUILTIN_VALUES.add(ENDER_FEMALE = make(appearance, DragonTypes.ENDER, "ender_female"));
        BUILTIN_VALUES.add(ENDER_MALE = make(appearance, DragonTypes.ENDER, "ender_male"));
        BUILTIN_VALUES.add(FIRE_FEMALE = make(appearance, DragonTypes.FIRE, "fire_female"));
        BUILTIN_VALUES.add(FIRE_MALE = make(appearance, DragonTypes.FIRE, "fire_male"));
        BUILTIN_VALUES.add(FOREST_FEMALE = make(appearance, DragonTypes.FOREST, "forest_female"));
        BUILTIN_VALUES.add(FOREST_MALE = make(appearance, DragonTypes.FOREST, "forest_male"));
        BUILTIN_VALUES.add(FOREST_DRY_FEMALE = make(appearance, DragonTypes.FOREST, "forest_dry_female"));
        BUILTIN_VALUES.add(FOREST_DRY_MALE = make(appearance, DragonTypes.FOREST, "forest_dry_male"));
        BUILTIN_VALUES.add(FOREST_TAIGA_FEMALE = make(appearance, DragonTypes.FOREST, "forest_taiga_female"));
        BUILTIN_VALUES.add(FOREST_TAIGA_MALE = make(appearance, DragonTypes.FOREST, "forest_taiga_male"));
        BUILTIN_VALUES.add(ICE_FEMALE = make(appearance, DragonTypes.ICE, "ice_female"));
        BUILTIN_VALUES.add(ICE_MALE = make(appearance, DragonTypes.ICE, "ice_male"));
        BUILTIN_VALUES.add(MOONLIGHT_FEMALE = make(appearance, DragonTypes.MOONLIGHT, "moonlight_female"));
        BUILTIN_VALUES.add(MOONLIGHT_MALE = make(appearance, DragonTypes.MOONLIGHT, "moonlight_male"));
        BUILTIN_VALUES.add(NETHER_FEMALE = make(appearance, DragonTypes.NETHER, "nether_female"));
        BUILTIN_VALUES.add(NETHER_MALE = make(appearance, DragonTypes.NETHER, "nether_male"));
        BUILTIN_VALUES.add(SKELETON_FEMALE = make(appearance, DragonTypes.SKELETON, "skeleton_female"));
        BUILTIN_VALUES.add(SKELETON_MALE = make(appearance, DragonTypes.SKELETON, "skeleton_male"));
        BUILTIN_VALUES.add(STORM_FEMALE = make(appearance, DragonTypes.STORM, "storm_female"));
        BUILTIN_VALUES.add(STORM_MALE = make(appearance, DragonTypes.STORM, "storm_male"));
        BUILTIN_VALUES.add(SUNLIGHT_FEMALE = make(appearance, DragonTypes.SUNLIGHT, "sunlight_female"));
        BUILTIN_VALUES.add(SUNLIGHT_MALE = make(appearance, DragonTypes.SUNLIGHT, "sunlight_male"));
        BUILTIN_VALUES.add(TERRA_FEMALE = make(appearance, DragonTypes.TERRA, "terra_female"));
        BUILTIN_VALUES.add(TERRA_MALE = make(appearance, DragonTypes.TERRA, "terra_male"));
        BUILTIN_VALUES.add(WATER_FEMALE = make(appearance, DragonTypes.WATER, "water_female"));
        BUILTIN_VALUES.add(WATER_MALE = make(appearance, DragonTypes.WATER, "water_male"));
        BUILTIN_VALUES.add(WITHER_FEMALE = make(appearance, DragonTypes.WITHER, "wither_female"));
        BUILTIN_VALUES.add(WITHER_MALE = make(appearance, DragonTypes.WITHER, "wither_male"));
        BUILTIN_VALUES.add(ZOMBIE_FEMALE = make(appearance, DragonTypes.ZOMBIE, "zombie_female"));
        BUILTIN_VALUES.add(ZOMBIE_MALE = make(appearance, DragonTypes.ZOMBIE, "zombie_male"));
    }
}
