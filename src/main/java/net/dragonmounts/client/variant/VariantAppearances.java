package net.dragonmounts.client.variant;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

import static net.dragonmounts.DragonMounts.makeId;
import static net.dragonmounts.DragonMountsTags.MOD_ID;
import static net.dragonmounts.client.variant.VariantAppearance.TEXTURES_ROOT;

public class VariantAppearances {
    public static AgeableAppearance createAgeableAppearance(String namespace, String path, boolean hasTailHorns, boolean hasSideTailScale) {
        StringBuilder builder = new StringBuilder(TEXTURES_ROOT + path);
        int length = builder.length();
        return new AgeableAppearance(
                new ResourceLocation(namespace, builder.append("/body.png").toString()),
                new ResourceLocation(namespace, builder.insert(length, "/baby").toString()),
                new ResourceLocation(namespace, builder.replace(length + 6, length + 10, "glow").toString()),
                new ResourceLocation(namespace, builder.delete(length, length + 5).toString()),
                hasTailHorns,
                hasSideTailScale,
                false);
    }

    public static VariantAppearance createDefaultAppearance(String namespace, String path, boolean hasTailHorns, boolean hasSideTailScale, boolean isSkeleton) {
        StringBuilder builder = new StringBuilder(TEXTURES_ROOT + path);
        int length = builder.length();
        return new DefaultAppearance(
                new ResourceLocation(namespace, builder.append("/body.png").toString()),
                new ResourceLocation(namespace, builder.replace(length + 1, length + 5, "glow").toString()),
                hasTailHorns,
                hasSideTailScale,
                isSkeleton);
    }

    public static final VariantAppearance AETHER_FEMALE;
    public static final VariantAppearance AETHER_MALE;
    public static final VariantAppearance ENCHANT_FEMALE = createAgeableAppearance(MOD_ID, "enchant/female", false, false);
    public static final VariantAppearance ENCHANT_MALE = createAgeableAppearance(MOD_ID, "enchant/male", false, false);
    public static final VariantAppearance ENDER_FEMALE;
    public static final VariantAppearance ENDER_MALE;
    public static final VariantAppearance FIRE_FEMALE = createAgeableAppearance(MOD_ID, "fire/female", false, false);
    public static final VariantAppearance FIRE_MALE = createAgeableAppearance(MOD_ID, "fire/male", false, false);
    public static final VariantAppearance FOREST_FEMALE;
    public static final VariantAppearance FOREST_MALE;
    public static final VariantAppearance FOREST_DRY_FEMALE;
    public static final VariantAppearance FOREST_DRY_MALE;
    public static final VariantAppearance FOREST_TAIGA_FEMALE;
    public static final VariantAppearance FOREST_TAIGA_MALE;
    public static final VariantAppearance ICE_FEMALE;
    public static final VariantAppearance ICE_MALE;
    public static final VariantAppearance MOONLIGHT_FEMALE = createDefaultAppearance(MOD_ID, "moonlight/female", false, false, false);
    public static final VariantAppearance MOONLIGHT_MALE = createDefaultAppearance(MOD_ID, "moonlight/male", false, false, false);
    public static final VariantAppearance NETHER_FEMALE = createAgeableAppearance(MOD_ID, "nether/female", false, false);
    public static final VariantAppearance NETHER_MALE = createAgeableAppearance(MOD_ID, "nether/male", false, false);
    public static final VariantAppearance SKELETON_FEMALE;
    public static final VariantAppearance SKELETON_MALE;
    public static final VariantAppearance STORM_FEMALE;
    public static final VariantAppearance STORM_MALE = createAgeableAppearance(MOD_ID, "storm/male", true, false);
    public static final VariantAppearance SUNLIGHT_FEMALE = createAgeableAppearance(MOD_ID, "sunlight/female", false, false);
    public static final VariantAppearance SUNLIGHT_MALE = createAgeableAppearance(MOD_ID, "sunlight/male", false, false);
    public static final VariantAppearance TERRA_FEMALE = createAgeableAppearance(MOD_ID, "terra/female", false, false);
    public static final VariantAppearance TERRA_MALE = createAgeableAppearance(MOD_ID, "terra/male", false, false);
    public static final VariantAppearance WATER_FEMALE = createAgeableAppearance(MOD_ID, "water/female", true, false);
    public static final VariantAppearance WATER_MALE = createAgeableAppearance(MOD_ID, "water/male", true, false);
    public static final VariantAppearance WITHER_FEMALE = createDefaultAppearance(MOD_ID, "wither/female", true, false, true);
    public static final VariantAppearance WITHER_MALE = createDefaultAppearance(MOD_ID, "wither/male", true, false, true);
    public static final VariantAppearance ZOMBIE_FEMALE;
    public static final VariantAppearance ZOMBIE_MALE;

    static {
        ResourceLocation glow = makeId(TEXTURES_ROOT + "aether/glow.png");
        ResourceLocation babyGlow = makeId(TEXTURES_ROOT + "aether/baby_glow.png");
        AETHER_FEMALE = new AgeableAppearance(makeId(TEXTURES_ROOT + "aether/female/body.png"), makeId(TEXTURES_ROOT + "aether/female/baby_body.png"), babyGlow, glow, false, false, false);
        AETHER_MALE = new AgeableAppearance(makeId(TEXTURES_ROOT + "aether/male/body.png"), makeId(TEXTURES_ROOT + "aether/male/baby_body.png"), babyGlow, glow, false, false, false);
    }

    static {
        ResourceLocation glow = makeId(TEXTURES_ROOT + "ender/glow.png");
        ENDER_FEMALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "ender/female/body.png"), glow, false, false, false);
        ENDER_MALE = new AgeableAppearance(makeId(TEXTURES_ROOT + "ender/male/body.png"), makeId(TEXTURES_ROOT + "ender/male/baby_body.png"), glow, glow, false, false, false);
    }

    static {
        ResourceLocation glow = makeId(TEXTURES_ROOT + "forest/glow.png");
        ResourceLocation babyBody = makeId(TEXTURES_ROOT + "forest/forest/baby_body.png");
        FOREST_FEMALE = new AgeableAppearance(makeId(TEXTURES_ROOT + "forest/forest/female_body.png"), babyBody, glow, glow, false, false, false);
        FOREST_MALE = new AgeableAppearance(makeId(TEXTURES_ROOT + "forest/forest/male_body.png"), babyBody, glow, glow, false, false, false);
        babyBody = makeId(TEXTURES_ROOT + "forest/dry/baby_body.png");
        FOREST_DRY_FEMALE = new AgeableAppearance(makeId(TEXTURES_ROOT + "forest/dry/female_body.png"), babyBody, glow, glow, false, false, false);
        FOREST_DRY_MALE = new AgeableAppearance(makeId(TEXTURES_ROOT + "forest/dry/male_body.png"), babyBody, glow, glow, false, false, false);
        FOREST_TAIGA_FEMALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "forest/taiga/female_body.png"), glow, false, false, false);
        FOREST_TAIGA_MALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "forest/taiga/male_body.png"), glow, false, false, false);
    }

    static {
        ResourceLocation babyGlow = makeId(TEXTURES_ROOT + "ice/baby_glow.png");
        ResourceLocation maleBody = makeId(TEXTURES_ROOT + "ice/male/body.png");
        ICE_FEMALE = new AgeableAppearance(
                makeId(TEXTURES_ROOT + "ice/female/body.png"),
                makeId(TEXTURES_ROOT + "ice/female/baby_body.png"),
                babyGlow,
                makeId(TEXTURES_ROOT + "ice/female/glow.png"),
                false,
                true,
                false);
        ICE_MALE = new AgeableAppearance(maleBody, maleBody, babyGlow, makeId(TEXTURES_ROOT + "ice/male/glow.png"), false, true, false);
    }

    static {
        ResourceLocation glow = makeId(TEXTURES_ROOT + "skeleton/glow.png");
        SKELETON_FEMALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "skeleton/female_body.png"), glow, false, false, true);
        SKELETON_MALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "skeleton/male_body.png"), glow, false, false, true);
    }

    static {
        ResourceLocation body = makeId(TEXTURES_ROOT + "storm/female/body.png");
        STORM_FEMALE = new AgeableAppearance(body, body, makeId(TEXTURES_ROOT + "storm/female/baby_glow.png"), makeId(TEXTURES_ROOT + "storm/female/glow.png"), true, false, false);
    }

    static {
        ResourceLocation body = makeId(TEXTURES_ROOT + "zombie/body.png");
        ZOMBIE_FEMALE = new DefaultAppearance(body, makeId(TEXTURES_ROOT + "zombie/female_glow.png"), false, false, false);
        ZOMBIE_MALE = new DefaultAppearance(body, makeId(TEXTURES_ROOT + "zombie/male_glow.png"), false, false, false);
    }

    public static Function<String, VariantAppearance> getFactory() {
        Object2ObjectOpenHashMap<String, VariantAppearance> map = new Object2ObjectOpenHashMap<>();
        map.put("aether_female", AETHER_FEMALE);
        map.put("aether_male", AETHER_MALE);
        map.put("enchant_female", ENCHANT_FEMALE);
        map.put("enchant_male", ENCHANT_MALE);
        map.put("ender_female", ENDER_FEMALE);
        map.put("ender_male", ENDER_MALE);
        map.put("fire_female", FIRE_FEMALE);
        map.put("fire_male", FIRE_MALE);
        map.put("forest_female", FOREST_FEMALE);
        map.put("forest_male", FOREST_MALE);
        map.put("forest_dry_female", FOREST_DRY_FEMALE);
        map.put("forest_dry_male", FOREST_DRY_MALE);
        map.put("forest_taiga_female", FOREST_TAIGA_FEMALE);
        map.put("forest_taiga_male", FOREST_TAIGA_MALE);
        map.put("ice_female", ICE_FEMALE);
        map.put("ice_male", ICE_MALE);
        map.put("moonlight_female", MOONLIGHT_FEMALE);
        map.put("moonlight_male", MOONLIGHT_MALE);
        map.put("nether_female", NETHER_FEMALE);
        map.put("nether_male", NETHER_MALE);
        map.put("skeleton_female", SKELETON_FEMALE);
        map.put("skeleton_male", SKELETON_MALE);
        map.put("storm_female", STORM_FEMALE);
        map.put("storm_male", STORM_MALE);
        map.put("sunlight_female", SUNLIGHT_FEMALE);
        map.put("sunlight_male", SUNLIGHT_MALE);
        map.put("terra_female", TERRA_FEMALE);
        map.put("terra_male", TERRA_MALE);
        map.put("water_female", WATER_FEMALE);
        map.put("water_male", WATER_MALE);
        map.put("wither_female", WITHER_FEMALE);
        map.put("wither_male", WITHER_MALE);
        map.put("zombie_female", ZOMBIE_FEMALE);
        map.put("zombie_male", ZOMBIE_MALE);
        return map::get;
    }
}
