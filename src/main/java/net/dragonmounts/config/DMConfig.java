package net.dragonmounts.config;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.DragonMountsTags;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class DMConfig {
    private static Configuration config;
    public static final String CATEGORY_DEBUG = "debug";
    public static final String CATEGORY_CLIENT = "client";
    public static final String CATEGORY_ATTRIBUTES = "attributes";
    public static final String CATEGORY_GAMEPLAY = "gameplay";
    public static final String CATEGORY_WORLD_GEN = "world_gen";
    // DEBUG:
    public static final BooleanEntry DEBUG_MODE = new BooleanEntry("debugMode", false);
    public static final BooleanEntry ENABLE_DEBUG_OVERLAY = new BooleanEntry("enableDebugOverlay", false);
    // CLIENT:
    public static final DoubleEntry CAMERA_DISTANCE = new DoubleEntry("cameraDistance", 20.0);
    // ATTRIBUTES:
    public static final DoubleEntry BASE_ARMOR = new DoubleEntry("baseArmor", 8.0);
    public static final DoubleEntry BASE_ARMOR_TOUGHNESS = new DoubleEntry("baseArmorToughness", 30.0);
    public static final DoubleEntry BASE_DAMAGE = new DoubleEntry("baseDamage", 12.0);
    public static final DoubleEntry BASE_HEALTH = new DoubleEntry("baseHealth", 90.0);
    public static final DoubleEntry BASE_KNOCKBACK_RESISTANCE = new DoubleEntry("baseKnockbackResistance", 10.0);
    public static final DoubleEntry BASE_FOLLOW_RANGE = new DoubleEntry("baseFollowRange", 70.0);
    public static final DoubleEntry BASE_MOVEMENT_SPEED = new DoubleEntry("baseMovementSpeed", 0.4);
    public static final DoubleEntry BASE_FLYING_SPEED = new DoubleEntry("baseFlyingSpeed", 0.75);
    public static final DoubleEntry BASE_SWIMMING_SPEED = new DoubleEntry("baseSwimmingSpeed", 5.0);
    public static final DoubleEntry BASE_BODY_SIZE = new DoubleEntry("baseBodySize", 1.6);
    public static final DoubleEntry BASE_STEP_HEIGHT = new DoubleEntry("baseStepHeight", 1.25);
    // GAMEPLAY:
    public static final BooleanEntry FORCED_RENAME = new BooleanEntry("forcedRename", false);
    public static final BooleanEntry BLOCK_OVERRIDE = new BooleanEntry("blockOverride", true);
    public static final IntEntry REPRODUCTION_LIMIT = new IntEntry("reproductionLimit", 3);
    public static final BooleanEntry ADAPTIVE_CONVERSION = new BooleanEntry("adaptiveConversion", true);
    public static final BooleanEntry BREATH_EFFECTS = new BooleanEntry("breathEffects", true);
    public static final BooleanEntry DESTRUCTIVE_BREATH = new BooleanEntry("destructiveBreath", true);
    public static final BooleanEntry IGNITING_BREATH = new BooleanEntry("ignitingBreath", true);
    public static final BooleanEntry SMELTING_BREATH = new BooleanEntry("smeltingBreath", false);
    public static final BooleanEntry FROSTY_BREATH = new BooleanEntry("frostyBreath", true);
    public static final BooleanEntry QUENCHING_BREATH = new BooleanEntry("quenchingBreath", true);
    // WORLD GEN:
    public static final BooleanEntry ALLOW_DECLARED_DIMENSIONS_ONLY = new BooleanEntry("toggleDimensionLimitation", false);
    public static final IntSetEntry LIMITED_DIMENSIONS = new IntSetEntry("limitedDimensions");

    // TEMP:
    public static int FireNestRarity = 150;
    //	public static int ZombieNestRarity1  = 180;
    public static int TerraNestRarity = 220;
    public static int ForestNestRarity = 220;
    public static int SunlightNestRarity = 60;
    public static int OceanNestRarity = 8000;
    public static int EnchantNestRarity = 300;
    public static int JungleNestRarity = 800;
    public static int WaterNestRarity = 180;
    public static int IceNestRarity = 200;

    public static int netherNestRarity = 200;
    public static int netherNestRarerityInX = 16;
    public static int netherNestRarerityInZ = 16;
    public static int zombieNestRarity = 500;
    public static int zombieNestRarerityInX = 28;
    public static int zombieNestRarerityInZ = 28;

    public static Configuration getConfig() {
        if (config == null) {
            config = new Configuration(new File(
                    Loader.instance().getConfigDir(),
                    DragonMountsTags.MOD_ID + ".cfg"
            ));
            config.load();
            // DEBUG:
            ObjectArrayList<String> order = new ObjectArrayList<>();
            ConfigCategory category = config.getCategory(CATEGORY_DEBUG).setShowInGui(false);
            register(category, order, DEBUG_MODE)
                    .setLanguageKey("config.dragonmounts.debugMode")
                    .setRequiresMcRestart(true);
            register(category, order, ENABLE_DEBUG_OVERLAY)
                    .setLanguageKey("config.dragonmounts.debugOverlay");
            category.setPropertyOrder(order);
            // CLIENT:
            order = new ObjectArrayList<>();
            category = config.getCategory(CATEGORY_CLIENT).setShowInGui(false);
            Property prop = register(category, order, CAMERA_DISTANCE)
                    .setLanguageKey("config.dragonmounts.cameraDistance")
                    .setMinValue(4.0)
                    .setMaxValue(24.0);
            prop.setHasSlidingControl(true);
            prop.setComment("Zoom out for third person view while riding dragons or carriages.");
            category.setPropertyOrder(order);
            // ATTRIBUTES
            order = new ObjectArrayList<>();
            category = config.getCategory(CATEGORY_ATTRIBUTES)
                    .setLanguageKey("config.dragonmounts.category.attributes");
            register(category, order, BASE_ARMOR)
                    .setMaxValue(Double.MAX_VALUE)
                    .setMinValue(-Double.MAX_VALUE)
                    .setLanguageKey("config.dragonmounts.baseArmor");
            register(category, order, BASE_ARMOR_TOUGHNESS)
                    .setMaxValue(Double.MAX_VALUE)
                    .setMinValue(-Double.MAX_VALUE)
                    .setLanguageKey("config.dragonmounts.baseArmorToughness");
            register(category, order, BASE_DAMAGE)
                    .setMaxValue(Double.MAX_VALUE)
                    .setMinValue(-Double.MAX_VALUE)
                    .setLanguageKey("config.dragonmounts.baseDamage");
            register(category, order, BASE_HEALTH)
                    .setMaxValue(Double.MAX_VALUE)
                    .setMinValue(-Double.MAX_VALUE)
                    .setLanguageKey("config.dragonmounts.baseHealth");
            register(category, order, BASE_KNOCKBACK_RESISTANCE)
                    .setMaxValue(Double.MAX_VALUE)
                    .setMinValue(-Double.MAX_VALUE)
                    .setLanguageKey("config.dragonmounts.baseKnockbackResistance");
            register(category, order, BASE_FOLLOW_RANGE)
                    .setMaxValue(Double.MAX_VALUE)
                    .setMinValue(-Double.MAX_VALUE)
                    .setLanguageKey("config.dragonmounts.baseFollowRange");
            register(category, order, BASE_MOVEMENT_SPEED)
                    .setMaxValue(1.75)
                    .setMinValue(0.25)
                    .setLanguageKey("config.dragonmounts.baseMovementSpeed");
            register(category, order, BASE_FLYING_SPEED)
                    .setMaxValue(1.75)
                    .setMinValue(0.25)
                    .setLanguageKey("config.dragonmounts.baseFlyingSpeed");
            register(category, order, BASE_SWIMMING_SPEED)
                    .setMaxValue(10.0)
                    .setMinValue(0.0)
                    .setLanguageKey("config.dragonmounts.baseSwimmingSpeed");
            register(category, order, BASE_BODY_SIZE)
                    .setMaxValue(2.0)
                    .setMinValue(0.25)
                    .setLanguageKey("config.dragonmounts.baseBodySize");
            register(category, order, BASE_STEP_HEIGHT)
                    .setMaxValue(Double.MAX_VALUE)
                    .setMinValue(-Double.MAX_VALUE)
                    .setLanguageKey("config.dragonmounts.baseStepHeight")
                    .setRequiresWorldRestart(true);
            category.setPropertyOrder(order);
            // GAMEPLAY
            order = new ObjectArrayList<>();
            category = config.getCategory(CATEGORY_GAMEPLAY)
                    .setLanguageKey("config.dragonmounts.category.gameplay");
            register(category, order, FORCED_RENAME)
                    .setLanguageKey("config.dragonmounts.forcedRename")
                    .setComment("Whether to rename dragon when renaming amulet.");
            register(category, order, BLOCK_OVERRIDE)
                    .setLanguageKey("config.dragonmounts.blockOverride")
                    .setComment("Whether interaction hook about vanilla dragon egg is enabled.");
            register(category, order, REPRODUCTION_LIMIT)
                    .setMinValue(0)
                    .setLanguageKey("config.dragonmounts.reproductionLimit");
            register(category, order, ADAPTIVE_CONVERSION)
                    .setLanguageKey("config.dragonmounts.adaptiveConversion")
                    .setComment("Whether dragon eggs will be converted to another type to adapt to the environment.");
            register(category, order, BREATH_EFFECTS)
                    .setLanguageKey("config.dragonmounts.breathEffects")
                    .setComment("Whether dragon breath has side effects when hits blocks.");
            register(category, order, DESTRUCTIVE_BREATH)
                    .setLanguageKey("config.dragonmounts.destructiveBreath")
                    .setComment("Whether airflow-like dragon breath can destroy the hit blocks.");
            register(category, order, IGNITING_BREATH)
                    .setLanguageKey("config.dragonmounts.ignitingBreath")
                    .setComment("Whether fire-like dragon breath can ignite the hit blocks.");
            register(category, order, SMELTING_BREATH)
                    .setLanguageKey("config.dragonmounts.smeltingBreath")
                    .setComment("Whether fire-like dragon breath can smelt the hit blocks.");
            register(category, order, FROSTY_BREATH)
                    .setLanguageKey("config.dragonmounts.frostyBreath")
                    .setComment("Whether blizzard-like dragon breath can leave snow on ground.");
            register(category, order, QUENCHING_BREATH)
                    .setLanguageKey("config.dragonmounts.quenchingBreath")
                    .setComment("Whether mist-like dragon breath can put out fire and solidify lava.");
            category.setPropertyOrder(order);
            // WORLD GEN:
            order = new ObjectArrayList<>();
            category = config.getCategory(CATEGORY_WORLD_GEN)
                    .setLanguageKey("config.dragonmounts.category.world_gen");
            register(category, order, ALLOW_DECLARED_DIMENSIONS_ONLY)
                    .setLanguageKey("config.dragonmounts.toggleDimensionLimitation")
                    .setComment("Whether the limited dimension list is an allow list or a block list.");
            register(category, order, LIMITED_DIMENSIONS)
                    .setLanguageKey("config.dragonmounts.limitedDimensions")
                    .setComment("Limited dimension list on dragon nest generation.");
            category.setPropertyOrder(order);
        }
        return config;
    }

    public static void load() {
        Configuration config = getConfig();
        ConfigCategory category = config.getCategory(CATEGORY_DEBUG);
        load(category, DEBUG_MODE);
        loadConditional(category, DEBUG_MODE.value, ENABLE_DEBUG_OVERLAY);

        load(config.getCategory(CATEGORY_CLIENT), CAMERA_DISTANCE);

        category = config.getCategory(CATEGORY_ATTRIBUTES);
        load(category, BASE_ARMOR);
        load(category, BASE_ARMOR_TOUGHNESS);
        load(category, BASE_DAMAGE);
        load(category, BASE_HEALTH);
        load(category, BASE_KNOCKBACK_RESISTANCE);
        load(category, BASE_FOLLOW_RANGE);
        load(category, BASE_MOVEMENT_SPEED);
        load(category, BASE_FLYING_SPEED);
        load(category, BASE_SWIMMING_SPEED);
        load(category, BASE_STEP_HEIGHT);
        load(category, BASE_BODY_SIZE);

        category = config.getCategory(CATEGORY_GAMEPLAY);
        load(category, FORCED_RENAME);
        load(category, BLOCK_OVERRIDE);
        load(category, REPRODUCTION_LIMIT);
        load(category, ADAPTIVE_CONVERSION);
        load(category, BREATH_EFFECTS);
        boolean flag = BREATH_EFFECTS.value;
        loadConditional(category, flag, DESTRUCTIVE_BREATH);
        loadConditional(category, flag, IGNITING_BREATH);
        loadConditional(category, flag, SMELTING_BREATH);
        loadConditional(category, flag, FROSTY_BREATH);
        loadConditional(category, flag, QUENCHING_BREATH);

        category = config.getCategory(CATEGORY_WORLD_GEN);
        load(category, ALLOW_DECLARED_DIMENSIONS_ONLY);
        load(category, LIMITED_DIMENSIONS);
        // TEMP:
        Property prop = config.get(CATEGORY_WORLD_GEN, "Forest Nest Rarity", ForestNestRarity);
        prop.setComment("Determines how rare Forest Plains dragon nests will mainly spawn. I did this because the forest biome is too common thus making the forest breed to common. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn), "
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        ForestNestRarity = prop.getInt();

        // sunlight world nest
        prop = config.get(CATEGORY_WORLD_GEN, "Sunlight Nest Rarity", SunlightNestRarity);
        prop.setComment("Determines how rare sunlight dragon temples will mainly spawn. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn), "
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        SunlightNestRarity = prop.getInt();

        // sunlight world nest
        prop = config.get(CATEGORY_WORLD_GEN, "Terra Nest Rarity", TerraNestRarity);
        prop.setComment("Determines how rare terra dragon nests will mainly spawn. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn), "
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        TerraNestRarity = prop.getInt();

        // sunlight world nest
        prop = config.get(CATEGORY_WORLD_GEN, "Ocean Nest Rarity", OceanNestRarity);
        prop.setComment("Determines how rare moonlight or aether dragon temples will spawn above the ocean. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn), "
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        OceanNestRarity = prop.getInt();

        prop = config.get(CATEGORY_WORLD_GEN, "Jungle Nest Rarity", JungleNestRarity);
        prop.setComment("Determines how rare forest jungnle dragon nests will mainly spawn. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn), "
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        JungleNestRarity = prop.getInt();

        prop = config.get(CATEGORY_WORLD_GEN, "Water Nest Rarity", WaterNestRarity);
        prop.setComment("Determines how rare water dragon nests will mainly spawn. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn), "
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        WaterNestRarity = prop.getInt();

        prop = config.get(CATEGORY_WORLD_GEN, "Ice Nest Rarity", IceNestRarity);
        prop.setComment("Determines how rare ice dragon nests will mainly spawn. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn), "
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        IceNestRarity = prop.getInt();

        prop = config.get(CATEGORY_WORLD_GEN, "Fire Nest Rarity", FireNestRarity);
        prop.setComment("Determines how rare fire dragon nests will mainly spawn. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn), "
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        FireNestRarity = prop.getInt();

        prop = config.get(CATEGORY_WORLD_GEN, "Enchant Nest Rarity", EnchantNestRarity);
        prop.setComment("Determines how rare forest enchant dragon nests will mainly spawn. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn), "
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        EnchantNestRarity = prop.getInt();

        // nether nest
        prop = config.get(CATEGORY_WORLD_GEN, "Nether Nest Chance", netherNestRarity);
        prop.setComment("Determines how rare nether nests will mainly spawn. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn)"
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        netherNestRarity = prop.getInt();

        prop = config.get(CATEGORY_WORLD_GEN, "2 Nether Nest Rarity X", netherNestRarerityInX);
        prop.setComment("Determines how rare nether nests will spawn in the X Axis. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn)"
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        netherNestRarerityInX = prop.getInt();

        prop = config.get(CATEGORY_WORLD_GEN, "2 Nest Nether Rarity Z", netherNestRarerityInZ);
        prop.setComment("Determines how rare nether nests will spawn in the Z Axis. Higher numbers = higher rarity (in other words  how many blocks for another nest to spawn)"
                + "(Note: Expermiment on a new world when editing these numbers because it may cause damages to your own worlds)");
        netherNestRarerityInZ = prop.getInt();
        if (config.hasChanged()) {
            config.save();
        }
    }

    static Property register(ConfigCategory category, ObjectArrayList<String> order, IConfigEntry entry) {
        Property prop = entry.getOrCreate(category);
        order.add(prop.getName());
        return prop;
    }

    static void load(ConfigCategory category, BooleanEntry entry) {
        entry.value = entry.getOrCreate(category).getBoolean(entry.fallback);
    }

    static void loadConditional(ConfigCategory category, boolean condition, BooleanEntry entry) {
        Property prop = entry.getOrCreate(category).setShowInGui(condition);
        entry.value = condition && prop.getBoolean(entry.fallback);
    }

    static void load(ConfigCategory category, DoubleEntry entry) {
        entry.value = entry.getOrCreate(category).getDouble(entry.fallback);
    }

    static void load(ConfigCategory category, IntEntry entry) {
        entry.value = entry.getOrCreate(category).getInt(entry.fallback);
    }

    static void load(ConfigCategory category, IntSetEntry entry) {
        entry.value = new IntOpenHashSet(entry.getOrCreate(category).getIntList());
    }
}
