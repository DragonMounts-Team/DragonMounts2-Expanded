package net.dragonmounts.init;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.DragonMounts;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.registry.DragonVariant;

import static net.dragonmounts.DragonMounts.makeId;

public class DragonVariants {
    public static final ObjectArrayList<DragonVariant> BUILTIN_VALUES = new ObjectArrayList<>();
    public static final DragonVariant AETHER_FEMALE;
    public static final DragonVariant AETHER_MALE;
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

    static {
        Function<String, VariantAppearance> function = DragonMounts.PROXY.getVariantAppearances();
        BUILTIN_VALUES.add(AETHER_FEMALE = new DragonVariant(DragonTypes.AETHER, makeId("aether_female"), function));
        BUILTIN_VALUES.add(AETHER_MALE = new DragonVariant(DragonTypes.AETHER, makeId("aether_male"), function));
        BUILTIN_VALUES.add(ENCHANT_FEMALE = new DragonVariant(DragonTypes.ENCHANT, makeId("enchant_female"), function));
        BUILTIN_VALUES.add(ENCHANT_MALE = new DragonVariant(DragonTypes.ENCHANT, makeId("enchant_male"), function));
        BUILTIN_VALUES.add(ENDER_FEMALE = new DragonVariant(DragonTypes.ENDER, DragonVariant.DEFAULT_KEY, function));
        BUILTIN_VALUES.add(ENDER_MALE = new DragonVariant(DragonTypes.ENDER, makeId("ender_male"), function));
        BUILTIN_VALUES.add(FIRE_FEMALE = new DragonVariant(DragonTypes.FIRE, makeId("fire_female"), function));
        BUILTIN_VALUES.add(FIRE_MALE = new DragonVariant(DragonTypes.FIRE, makeId("fire_male"), function));
        BUILTIN_VALUES.add(FOREST_FEMALE = new DragonVariant(DragonTypes.FOREST, makeId("forest_female"), function));
        BUILTIN_VALUES.add(FOREST_MALE = new DragonVariant(DragonTypes.FOREST, makeId("forest_male"), function));
        BUILTIN_VALUES.add(FOREST_DRY_FEMALE = new DragonVariant(DragonTypes.FOREST, makeId("forest_dry_female"), function));
        BUILTIN_VALUES.add(FOREST_DRY_MALE = new DragonVariant(DragonTypes.FOREST, makeId("forest_dry_male"), function));
        BUILTIN_VALUES.add(FOREST_TAIGA_FEMALE = new DragonVariant(DragonTypes.FOREST, makeId("forest_taiga_female"), function));
        BUILTIN_VALUES.add(FOREST_TAIGA_MALE = new DragonVariant(DragonTypes.FOREST, makeId("forest_taiga_male"), function));
        BUILTIN_VALUES.add(ICE_FEMALE = new DragonVariant(DragonTypes.ICE, makeId("ice_female"), function));
        BUILTIN_VALUES.add(ICE_MALE = new DragonVariant(DragonTypes.ICE, makeId("ice_male"), function));
        BUILTIN_VALUES.add(MOONLIGHT_FEMALE = new DragonVariant(DragonTypes.MOONLIGHT, makeId("moonlight_female"), function));
        BUILTIN_VALUES.add(MOONLIGHT_MALE = new DragonVariant(DragonTypes.MOONLIGHT, makeId("moonlight_male"), function));
        BUILTIN_VALUES.add(NETHER_FEMALE = new DragonVariant(DragonTypes.NETHER, makeId("nether_female"), function));
        BUILTIN_VALUES.add(NETHER_MALE = new DragonVariant(DragonTypes.NETHER, makeId("nether_male"), function));
        BUILTIN_VALUES.add(SKELETON_FEMALE = new DragonVariant(DragonTypes.SKELETON, makeId("skeleton_female"), function));
        BUILTIN_VALUES.add(SKELETON_MALE = new DragonVariant(DragonTypes.SKELETON, makeId("skeleton_male"), function));
        BUILTIN_VALUES.add(STORM_FEMALE = new DragonVariant(DragonTypes.STORM, makeId("storm_female"), function));
        BUILTIN_VALUES.add(STORM_MALE = new DragonVariant(DragonTypes.STORM, makeId("storm_male"), function));
        BUILTIN_VALUES.add(SUNLIGHT_FEMALE = new DragonVariant(DragonTypes.SUNLIGHT, makeId("sunlight_female"), function));
        BUILTIN_VALUES.add(SUNLIGHT_MALE = new DragonVariant(DragonTypes.SUNLIGHT, makeId("sunlight_male"), function));
        BUILTIN_VALUES.add(TERRA_FEMALE = new DragonVariant(DragonTypes.TERRA, makeId("terra_female"), function));
        BUILTIN_VALUES.add(TERRA_MALE = new DragonVariant(DragonTypes.TERRA, makeId("terra_male"), function));
        BUILTIN_VALUES.add(WATER_FEMALE = new DragonVariant(DragonTypes.WATER, makeId("water_female"), function));
        BUILTIN_VALUES.add(WATER_MALE = new DragonVariant(DragonTypes.WATER, makeId("water_male"), function));
        BUILTIN_VALUES.add(WITHER_FEMALE = new DragonVariant(DragonTypes.WITHER, makeId("wither_female"), function));
        BUILTIN_VALUES.add(WITHER_MALE = new DragonVariant(DragonTypes.WITHER, makeId("wither_male"), function));
        BUILTIN_VALUES.add(ZOMBIE_FEMALE = new DragonVariant(DragonTypes.ZOMBIE, makeId("zombie_female"), function));
        BUILTIN_VALUES.add(ZOMBIE_MALE = new DragonVariant(DragonTypes.ZOMBIE, makeId("zombie_male"), function));
    }
}
