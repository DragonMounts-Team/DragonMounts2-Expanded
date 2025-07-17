package net.dragonmounts.client.variant;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.client.model.dragon.BuiltinFactory;
import net.dragonmounts.client.model.dragon.IModelFactory;
import net.minecraft.util.ResourceLocation;

import java.util.NoSuchElementException;
import java.util.function.Function;

import static net.dragonmounts.DragonMounts.makeId;
import static net.dragonmounts.DragonMountsTags.MOD_ID;
import static net.dragonmounts.client.variant.VariantAppearance.TEXTURES_ROOT;

public class VariantAppearances {
    public static VariantAppearance makeAppearance(String namespace, String path, IModelFactory factory) {
        StringBuilder builder = new StringBuilder(TEXTURES_ROOT + path);
        int length = builder.length();
        return new DefaultAppearance(
                new ResourceLocation(namespace, TEXTURES_ROOT + path + "/body.png"),
                new ResourceLocation(namespace, TEXTURES_ROOT + path + "/glow.png"),
                factory
        );
    }

    public static final VariantAppearance AETHER_FEMALE = makeAppearance(MOD_ID, "aether/female", BuiltinFactory.NORMAL);
    public static final VariantAppearance AETHER_MALE = makeAppearance(MOD_ID, "aether/male", BuiltinFactory.NORMAL);
    public static final VariantAppearance DARK_FEMALE = makeAppearance(MOD_ID, "dark/female", BuiltinFactory.NORMAL);
    public static final VariantAppearance DARK_MALE = makeAppearance(MOD_ID, "dark/male", BuiltinFactory.NORMAL);
    public static final VariantAppearance ENCHANTED_FEMALE = makeAppearance(MOD_ID, "enchanted/female", BuiltinFactory.NORMAL);
    public static final VariantAppearance ENCHANTED_MALE = makeAppearance(MOD_ID, "enchanted/male", BuiltinFactory.NORMAL);
    public static final VariantAppearance ENDER_FEMALE = makeAppearance(MOD_ID, "ender/female", BuiltinFactory.NORMAL);
    public static final VariantAppearance ENDER_MALE = makeAppearance(MOD_ID, "ender/male", BuiltinFactory.NORMAL);
    public static final VariantAppearance ENDER_RARE = makeAppearance(MOD_ID, "ender/rare", BuiltinFactory.NORMAL);
    public static final VariantAppearance FIRE_FEMALE = makeAppearance(MOD_ID, "fire/female", BuiltinFactory.NORMAL);
    public static final VariantAppearance FIRE_MALE = makeAppearance(MOD_ID, "fire/male", BuiltinFactory.NORMAL);
    public static final VariantAppearance FIRE_RARE = makeAppearance(MOD_ID, "fire/rare", BuiltinFactory.NORMAL);
    public static final VariantAppearance FOREST_FEMALE;
    public static final VariantAppearance FOREST_MALE;
    public static final VariantAppearance FOREST_DRY_FEMALE;
    public static final VariantAppearance FOREST_DRY_MALE;
    public static final VariantAppearance FOREST_TAIGA_FEMALE;
    public static final VariantAppearance FOREST_TAIGA_MALE;
    public static final VariantAppearance ICE_FEMALE = makeAppearance(MOD_ID, "ice/female", BuiltinFactory.TAIL_SCALE_INCLINED);
    public static final VariantAppearance ICE_MALE = makeAppearance(MOD_ID, "ice/male", BuiltinFactory.TAIL_SCALE_INCLINED);
    public static final VariantAppearance MOONLIGHT_FEMALE = makeAppearance(MOD_ID, "moonlight/female", BuiltinFactory.NORMAL);
    public static final VariantAppearance MOONLIGHT_MALE = makeAppearance(MOD_ID, "moonlight/male", BuiltinFactory.NORMAL);
    public static final VariantAppearance NETHER_FEMALE = makeAppearance(MOD_ID, "nether/female", BuiltinFactory.NORMAL);
    public static final VariantAppearance NETHER_MALE = makeAppearance(MOD_ID, "nether/male", BuiltinFactory.NORMAL);
    public static final VariantAppearance SKELETON = makeAppearance(MOD_ID, "skeleton", BuiltinFactory.SKELETON);
    public static final VariantAppearance STORM_FEMALE = makeAppearance(MOD_ID, "storm/female", BuiltinFactory.TAIL_HORNED);
    public static final VariantAppearance STORM_MALE = makeAppearance(MOD_ID, "storm/male", BuiltinFactory.TAIL_HORNED);
    public static final VariantAppearance STORM_RARE = makeAppearance(MOD_ID, "storm/rare", BuiltinFactory.TAIL_HORNED);
    public static final VariantAppearance SUNLIGHT_FEMALE = makeAppearance(MOD_ID, "sunlight/female", BuiltinFactory.NORMAL);
    public static final VariantAppearance SUNLIGHT_MALE = makeAppearance(MOD_ID, "sunlight/male", BuiltinFactory.NORMAL);
    public static final VariantAppearance TERRA_FEMALE = makeAppearance(MOD_ID, "terra/female", BuiltinFactory.NORMAL);
    public static final VariantAppearance TERRA_MALE = makeAppearance(MOD_ID, "terra/male", BuiltinFactory.NORMAL);
    public static final VariantAppearance WATER_FEMALE = makeAppearance(MOD_ID, "water/female", BuiltinFactory.TAIL_HORNED);
    public static final VariantAppearance WATER_MALE = makeAppearance(MOD_ID, "water/male", BuiltinFactory.TAIL_HORNED);
    public static final VariantAppearance WITHER = makeAppearance(MOD_ID, "wither", BuiltinFactory.SKELETON);
    public static final VariantAppearance ZOMBIE = makeAppearance(MOD_ID, "zombie", BuiltinFactory.TAIL_HORNED);

    static {
        ResourceLocation glow = makeId(TEXTURES_ROOT + "forest/glow.png");
        FOREST_FEMALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "forest/forest/female_body.png"), glow, BuiltinFactory.NORMAL);
        FOREST_MALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "forest/forest/male_body.png"), glow, BuiltinFactory.NORMAL);
        FOREST_DRY_FEMALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "forest/dry/female_body.png"), glow, BuiltinFactory.NORMAL);
        FOREST_DRY_MALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "forest/dry/male_body.png"), glow, BuiltinFactory.NORMAL);
        FOREST_TAIGA_FEMALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "forest/taiga/female_body.png"), glow, BuiltinFactory.NORMAL);
        FOREST_TAIGA_MALE = new DefaultAppearance(makeId(TEXTURES_ROOT + "forest/taiga/male_body.png"), glow, BuiltinFactory.NORMAL);
    }

    public static Function<String, VariantAppearance> getSupplier() {
        Object2ObjectOpenHashMap<String, VariantAppearance> map = new Object2ObjectOpenHashMap<>();
        map.put("aether_female", AETHER_FEMALE);
        map.put("aether_male", AETHER_MALE);
        map.put("dark_female", DARK_FEMALE);
        map.put("dark_male", DARK_MALE);
        map.put("enchanted_female", ENCHANTED_FEMALE);
        map.put("enchanted_male", ENCHANTED_MALE);
        map.put("ender_female", ENDER_FEMALE);
        map.put("ender_male", ENDER_MALE);
        map.put("ender_rare", ENDER_RARE);
        map.put("fire_female", FIRE_FEMALE);
        map.put("fire_male", FIRE_MALE);
        map.put("fire_rare", FIRE_RARE);
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
        map.put("skeleton", SKELETON);
        map.put("storm_female", STORM_FEMALE);
        map.put("storm_male", STORM_MALE);
        map.put("storm_rare", STORM_RARE);
        map.put("sunlight_female", SUNLIGHT_FEMALE);
        map.put("sunlight_male", SUNLIGHT_MALE);
        map.put("terra_female", TERRA_FEMALE);
        map.put("terra_male", TERRA_MALE);
        map.put("water_female", WATER_FEMALE);
        map.put("water_male", WATER_MALE);
        map.put("wither", WITHER);
        map.put("zombie", ZOMBIE);
        return key -> {
            VariantAppearance value = map.get(key);
            if (value == null) throw new NoSuchElementException();
            return value;
        };
    }
}
