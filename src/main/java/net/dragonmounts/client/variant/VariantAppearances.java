package net.dragonmounts.client.variant;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.client.breath.BuiltinBreathTextures;
import net.dragonmounts.client.breath.impl.*;
import net.dragonmounts.client.model.dragon.BuiltinFactory;
import net.dragonmounts.client.variant.DefaultAppearance.Builder;
import net.minecraft.util.ResourceLocation;

import java.util.NoSuchElementException;
import java.util.function.Function;

import static net.dragonmounts.DragonMounts.makeId;
import static net.dragonmounts.DragonMountsTags.MOD_ID;
import static net.dragonmounts.client.variant.VariantAppearance.TEXTURES_ROOT;

public class VariantAppearances {
    public static final VariantAppearance AETHER_FEMALE;
    public static final VariantAppearance AETHER_MALE;
    public static final VariantAppearance DARK_FEMALE;
    public static final VariantAppearance DARK_MALE;
    public static final VariantAppearance ENCHANTED_FEMALE;
    public static final VariantAppearance ENCHANTED_MALE;
    public static final VariantAppearance ENDER_FEMALE;
    public static final VariantAppearance ENDER_MALE;
    public static final VariantAppearance ENDER_RARE;
    public static final VariantAppearance FIRE_FEMALE;
    public static final VariantAppearance FIRE_MALE;
    public static final VariantAppearance FIRE_RARE;
    public static final VariantAppearance FOREST_FEMALE;
    public static final VariantAppearance FOREST_MALE;
    public static final VariantAppearance FOREST_DRY_FEMALE;
    public static final VariantAppearance FOREST_DRY_MALE;
    public static final VariantAppearance FOREST_TAIGA_FEMALE;
    public static final VariantAppearance FOREST_TAIGA_MALE;
    public static final VariantAppearance ICE_FEMALE;
    public static final VariantAppearance ICE_MALE;
    public static final VariantAppearance MOONLIGHT_FEMALE;
    public static final VariantAppearance MOONLIGHT_MALE;
    public static final VariantAppearance NETHER_FEMALE;
    public static final VariantAppearance NETHER_MALE;
    public static final VariantAppearance SOUL;
    public static final VariantAppearance SKELETON;
    public static final VariantAppearance STORM_FEMALE;
    public static final VariantAppearance STORM_MALE;
    public static final VariantAppearance STORM_RARE;
    public static final VariantAppearance SUNLIGHT_FEMALE;
    public static final VariantAppearance SUNLIGHT_MALE;
    public static final VariantAppearance TERRA_FEMALE;
    public static final VariantAppearance TERRA_MALE;
    public static final VariantAppearance WATER_FEMALE;
    public static final VariantAppearance WATER_MALE;
    public static final VariantAppearance WITHER;
    public static final VariantAppearance ZOMBIE;

    static {
        Builder builder = new Builder(BuiltinFactory.NORMAL)
                .withBreath(BuiltinBreathTextures.AIRFLOW_BREATH, AirflowBreathParticle.FACTORY);
        AETHER_FEMALE = builder.build(MOD_ID, "aether/female");
        AETHER_MALE = builder.build(MOD_ID, "aether/male");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.NORMAL)
                .withBreath(BuiltinBreathTextures.DARK_BREATH);
        DARK_FEMALE = builder.build(MOD_ID, "dark/female");
        DARK_MALE = builder.build(MOD_ID, "dark/male");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.NORMAL);
        ENCHANTED_FEMALE = builder.build(MOD_ID, "enchanted/female");
        ENCHANTED_MALE = builder.build(MOD_ID, "enchanted/male");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.NORMAL)
                .withBreath(BuiltinBreathTextures.ENDER_BREATH, EnderBreathParticle.FACTORY);
        ENDER_FEMALE = builder.build(MOD_ID, "ender/female");
        ENDER_MALE = builder.build(MOD_ID, "ender/male");
        ENDER_RARE = builder.build(MOD_ID, "ender/rare");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.NORMAL);
        FIRE_FEMALE = builder.build(MOD_ID, "fire/female");
        FIRE_MALE = builder.build(MOD_ID, "fire/male");
        FIRE_RARE = builder.build(MOD_ID, "fire/rare");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.NORMAL);
        ResourceLocation glow = makeId(TEXTURES_ROOT + "forest/glow.png");
        FOREST_FEMALE = builder.build(makeId(TEXTURES_ROOT + "forest/forest/female_body.png"), glow);
        FOREST_MALE = builder.build(makeId(TEXTURES_ROOT + "forest/forest/male_body.png"), glow);
        FOREST_DRY_FEMALE = builder.build(makeId(TEXTURES_ROOT + "forest/dry/female_body.png"), glow);
        FOREST_DRY_MALE = builder.build(makeId(TEXTURES_ROOT + "forest/dry/male_body.png"), glow);
        FOREST_TAIGA_FEMALE = builder.build(makeId(TEXTURES_ROOT + "forest/taiga/female_body.png"), glow);
        FOREST_TAIGA_MALE = builder.build(makeId(TEXTURES_ROOT + "forest/taiga/male_body.png"), glow);
    }

    static {
        Builder builder = new Builder(BuiltinFactory.TAIL_SCALE_INCLINED)
                .withBreath(BuiltinBreathTextures.ICE_BREATH, IceBreathParticle.FACTORY);
        ICE_FEMALE = builder.build(MOD_ID, "ice/female");
        ICE_MALE = builder.build(MOD_ID, "ice/male");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.NORMAL);
        MOONLIGHT_FEMALE = builder.build(MOD_ID, "moonlight/female");
        MOONLIGHT_MALE = builder.build(MOD_ID, "moonlight/male");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.TAIL_HORNED)
                .withBreath(BuiltinBreathTextures.NETHER_BREATH, NetherBreathParticle.FACTORY);
        NETHER_FEMALE = builder.build(MOD_ID, "nether/female");
        NETHER_MALE = builder.build(MOD_ID, "nether/male");
        SOUL = builder.withBreath(BuiltinBreathTextures.SOUL_BREATH).build(MOD_ID, "nether/soul");
    }

    static {
        SKELETON = new Builder(BuiltinFactory.SKELETON).build(MOD_ID, "skeleton");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.TAIL_HORNED);
        STORM_FEMALE = builder.build(MOD_ID, "storm/female");
        STORM_MALE = builder.build(MOD_ID, "storm/male");
        STORM_RARE = builder.build(MOD_ID, "storm/rare");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.NORMAL);
        SUNLIGHT_FEMALE = builder.build(MOD_ID, "sunlight/female");
        SUNLIGHT_MALE = builder.build(MOD_ID, "sunlight/male");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.NORMAL);
        TERRA_FEMALE = builder.build(MOD_ID, "terra/female");
        TERRA_MALE = builder.build(MOD_ID, "terra/male");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.TAIL_HORNED)
                .withBreath(BuiltinBreathTextures.WATER_BREATH, WaterBreathParticle.FACTORY);
        WATER_FEMALE = builder.build(MOD_ID, "water/female");
        WATER_MALE = builder.build(MOD_ID, "water/male");
    }


    static {
        Builder builder = new Builder(BuiltinFactory.SKELETON)
                .withBreath(BuiltinBreathTextures.WITHER_BREATH);
        WITHER = builder.build(MOD_ID, "wither");
    }

    static {
        Builder builder = new Builder(BuiltinFactory.TAIL_HORNED)
                .withBreath(BuiltinBreathTextures.POISON_BREATH, PoisonBreathParticle.FACTORY);
        ZOMBIE = builder.build(MOD_ID, "zombie");
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
        map.put("soul", SOUL);
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
