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

    static DragonVariant makeVariant(
            DragonType type,
            String name,
            Function<String, VariantAppearance> factory
    ) {
        return new DragonVariant(type, makeId(name), factory, variant -> {
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
        Function<String, VariantAppearance> appearance = DragonMounts.PROXY.getVariantAppearances();
        BUILTIN_VALUES.add(AETHER_FEMALE = makeVariant(DragonTypes.AETHER, "aether_female", appearance));
        BUILTIN_VALUES.add(AETHER_MALE = makeVariant(DragonTypes.AETHER, "aether_male", appearance));
        BUILTIN_VALUES.add(DARK = makeVariant(DragonTypes.DARK, "dark", appearance));
        BUILTIN_VALUES.add(ENCHANT_FEMALE = makeVariant(DragonTypes.ENCHANT, "enchant_female", appearance));
        BUILTIN_VALUES.add(ENCHANT_MALE = makeVariant(DragonTypes.ENCHANT, "enchant_male", appearance));
        BUILTIN_VALUES.add(ENDER_FEMALE = makeVariant(DragonTypes.ENDER, "ender_female", appearance));
        BUILTIN_VALUES.add(ENDER_MALE = makeVariant(DragonTypes.ENDER, "ender_male", appearance));
        BUILTIN_VALUES.add(FIRE_FEMALE = makeVariant(DragonTypes.FIRE, "fire_female", appearance));
        BUILTIN_VALUES.add(FIRE_MALE = makeVariant(DragonTypes.FIRE, "fire_male", appearance));
        BUILTIN_VALUES.add(FOREST_FEMALE = makeVariant(DragonTypes.FOREST, "forest_female", appearance));
        BUILTIN_VALUES.add(FOREST_MALE = makeVariant(DragonTypes.FOREST, "forest_male", appearance));
        BUILTIN_VALUES.add(FOREST_DRY_FEMALE = makeVariant(DragonTypes.FOREST, "forest_dry_female", appearance));
        BUILTIN_VALUES.add(FOREST_DRY_MALE = makeVariant(DragonTypes.FOREST, "forest_dry_male", appearance));
        BUILTIN_VALUES.add(FOREST_TAIGA_FEMALE = makeVariant(DragonTypes.FOREST, "forest_taiga_female", appearance));
        BUILTIN_VALUES.add(FOREST_TAIGA_MALE = makeVariant(DragonTypes.FOREST, "forest_taiga_male", appearance));
        BUILTIN_VALUES.add(ICE_FEMALE = makeVariant(DragonTypes.ICE, "ice_female", appearance));
        BUILTIN_VALUES.add(ICE_MALE = makeVariant(DragonTypes.ICE, "ice_male", appearance));
        BUILTIN_VALUES.add(MOONLIGHT_FEMALE = makeVariant(DragonTypes.MOONLIGHT, "moonlight_female", appearance));
        BUILTIN_VALUES.add(MOONLIGHT_MALE = makeVariant(DragonTypes.MOONLIGHT, "moonlight_male", appearance));
        BUILTIN_VALUES.add(NETHER_FEMALE = makeVariant(DragonTypes.NETHER, "nether_female", appearance));
        BUILTIN_VALUES.add(NETHER_MALE = makeVariant(DragonTypes.NETHER, "nether_male", appearance));
        BUILTIN_VALUES.add(SKELETON_FEMALE = makeVariant(DragonTypes.SKELETON, "skeleton_female", appearance));
        BUILTIN_VALUES.add(SKELETON_MALE = makeVariant(DragonTypes.SKELETON, "skeleton_male", appearance));
        BUILTIN_VALUES.add(STORM_FEMALE = makeVariant(DragonTypes.STORM, "storm_female", appearance));
        BUILTIN_VALUES.add(STORM_MALE = makeVariant(DragonTypes.STORM, "storm_male", appearance));
        BUILTIN_VALUES.add(SUNLIGHT_FEMALE = makeVariant(DragonTypes.SUNLIGHT, "sunlight_female", appearance));
        BUILTIN_VALUES.add(SUNLIGHT_MALE = makeVariant(DragonTypes.SUNLIGHT, "sunlight_male", appearance));
        BUILTIN_VALUES.add(TERRA_FEMALE = makeVariant(DragonTypes.TERRA, "terra_female", appearance));
        BUILTIN_VALUES.add(TERRA_MALE = makeVariant(DragonTypes.TERRA, "terra_male", appearance));
        BUILTIN_VALUES.add(WATER_FEMALE = makeVariant(DragonTypes.WATER, "water_female", appearance));
        BUILTIN_VALUES.add(WATER_MALE = makeVariant(DragonTypes.WATER, "water_male", appearance));
        BUILTIN_VALUES.add(WITHER_FEMALE = makeVariant(DragonTypes.WITHER, "wither_female", appearance));
        BUILTIN_VALUES.add(WITHER_MALE = makeVariant(DragonTypes.WITHER, "wither_male", appearance));
        BUILTIN_VALUES.add(ZOMBIE_FEMALE = makeVariant(DragonTypes.ZOMBIE, "zombie_female", appearance));
        BUILTIN_VALUES.add(ZOMBIE_MALE = makeVariant(DragonTypes.ZOMBIE, "zombie_male", appearance));
    }
}
