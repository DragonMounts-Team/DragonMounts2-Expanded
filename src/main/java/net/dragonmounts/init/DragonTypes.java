package net.dragonmounts.init;

import net.dragonmounts.def.*;
import net.dragonmounts.registry.DragonType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.loot.LootTableList;

import static net.dragonmounts.DragonMounts.makeId;

public class DragonTypes {
    public static final AetherType AETHER = new AetherType(
            makeId("aether"),
            new DragonType.Properties(0x0294BD, TextFormatting.DARK_AQUA)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Blocks.LAPIS_BLOCK)
                    .addHabitat(Blocks.LAPIS_ORE)
    );
    public static final EnchantType ENCHANT = new EnchantType(
            makeId("enchant"),
            new DragonType.Properties(0x8359AE, TextFormatting.LIGHT_PURPLE)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Blocks.BOOKSHELF)
                    .addHabitat(Blocks.ENCHANTING_TABLE)
    );
    public static final EnderType ENDER = new EnderType(
            DragonType.DEFAULT_KEY,
            new DragonType.Properties(0xAB39BE, TextFormatting.DARK_PURPLE)
                    .notConvertible()
                    .putAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "DragonTypeBonus", 10.0D, 0)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .setSneezeParticle(EnumParticleTypes.PORTAL)
                    .setEggParticle(EnumParticleTypes.PORTAL)
    );
    public static final FireType FIRE = new FireType(
            makeId("fire"),
            new DragonType.Properties(0x960B0F, TextFormatting.RED)
                    .avoidWater()
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Blocks.FIRE)
                    .addHabitat(Blocks.LIT_FURNACE)
                    .addHabitat(Blocks.LAVA)
                    .addHabitat(Blocks.FLOWING_LAVA)
    );
    public static final ForestType FOREST = new ForestType(
            makeId("forest"),
            new DragonType.Properties(0x298317, TextFormatting.DARK_GREEN)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Blocks.YELLOW_FLOWER)
                    .addHabitat(Blocks.RED_FLOWER)
                    .addHabitat(Blocks.MOSSY_COBBLESTONE)
                    .addHabitat(Blocks.VINE)
                    .addHabitat(Blocks.SAPLING)
                    .addHabitat(Blocks.LEAVES)
                    .addHabitat(Blocks.LEAVES2)
                    .addHabitat(Biomes.JUNGLE)
                    .addHabitat(Biomes.JUNGLE_HILLS)
    );
    public static final IceType ICE = new IceType(
            makeId("ice"),
            new DragonType.Properties(0x00F2FF, TextFormatting.AQUA)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Blocks.SNOW)
                    .addHabitat(Blocks.SNOW_LAYER)
                    .addHabitat(Blocks.ICE)
                    .addHabitat(Blocks.PACKED_ICE)
                    .addHabitat(Blocks.FROSTED_ICE)
                    .addHabitat(Biomes.FROZEN_OCEAN)
                    .addHabitat(Biomes.FROZEN_RIVER)
                    .setSneezeParticle(null)
    );
    public static final MoonlightType MOONLIGHT = new MoonlightType(
            makeId("moonlight"),
            new DragonType.Properties(0x2C427C, TextFormatting.BLUE)
                    .addHabitat(Blocks.DAYLIGHT_DETECTOR_INVERTED)
                    .addHabitat(Blocks.BLUE_GLAZED_TERRACOTTA)
                    .setSneezeParticle(null)
    );
    public static final NetherType NETHER = new NetherType(
            makeId("nether"),
            new DragonType.Properties(0xE5B81B, TextFormatting.DARK_RED)
                    .putAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "DragonTypeBonus", 5.0D, 0)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Biomes.HELL)
                    .setEggParticle(EnumParticleTypes.DRIP_LAVA)
    );
    public static final SkeletonType SKELETON = new SkeletonType(
            makeId("skeleton"),
            new DragonType.Properties(0xFFFFFF, TextFormatting.WHITE)
                    .isSkeleton()
                    .putAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "DragonTypeBonus", -15.0D, 0)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Blocks.BONE_BLOCK)
                    .setSneezeParticle(null)
    );
    public static final StormType STORM = new StormType(
            makeId("storm"),
            new DragonType.Properties(0xF5F1E9, TextFormatting.BLUE)
                    .addImmunity(DamageSource.DROWN)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .setSneezeParticle(null)
    );
    public static final SunlightType SUNLIGHT = new SunlightType(
            makeId("sunlight"),
            new DragonType.Properties(0xFFDE00, TextFormatting.YELLOW)
                    .addHabitat(Blocks.GLOWSTONE)
                    .addHabitat(Blocks.DAYLIGHT_DETECTOR)
                    .addHabitat(Blocks.YELLOW_GLAZED_TERRACOTTA)
    );
    public static final TerraType TERRA = new TerraType(
            makeId("terra"),
            new DragonType.Properties(0xA56C21, TextFormatting.GOLD)
                    .addHabitat(Blocks.HARDENED_CLAY)
                    .addHabitat(Blocks.SAND)
                    .addHabitat(Blocks.SANDSTONE)
                    .addHabitat(Blocks.SANDSTONE_STAIRS)
                    .addHabitat(Blocks.RED_SANDSTONE)
                    .addHabitat(Blocks.RED_SANDSTONE_STAIRS)
                    .addHabitat(Biomes.MESA)
                    .addHabitat(Biomes.MESA_ROCK)
                    .addHabitat(Biomes.MESA_CLEAR_ROCK)
                    .addHabitat(Biomes.MUTATED_MESA_CLEAR_ROCK)
                    .addHabitat(Biomes.MUTATED_MESA_ROCK)
    );
    public static final WaterType WATER = new WaterType(
            makeId("water"),
            new DragonType.Properties(0x4F69A8, TextFormatting.BLUE)
                    .addImmunity(DamageSource.DROWN)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Blocks.WATER)
                    .addHabitat(Blocks.FLOWING_WATER)
                    .addHabitat(Biomes.OCEAN)
                    .addHabitat(Biomes.RIVER)
                    .setSneezeParticle(null)
    );
    public static final WitherType WITHER = new WitherType(
            makeId("wither"),
            new DragonType.Properties(0x50260A, TextFormatting.DARK_GRAY)
                    .notConvertible()
                    .isSkeleton()
                    .putAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "DragonTypeBonus", -10.0D, 0)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .setSneezeParticle(null)
                    .loot(LootTableList.ENTITIES_WITHER_SKELETON)
    );
    public static final ZombieType ZOMBIE = new ZombieType(
            makeId("zombie"),
            new DragonType.Properties(0x5A5602, TextFormatting.DARK_GREEN)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Blocks.SOUL_SAND)
                    .addHabitat(Blocks.SOUL_SAND)
                    .addHabitat(Blocks.NETHER_WART_BLOCK)
                    .setSneezeParticle(null)
    );
    public static final DarkType DARK = new DarkType(
            makeId("dark"),
            new DragonType.Properties(0x808080, TextFormatting.GRAY)
                    .addImmunity(DamageSource.MAGIC)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.LIGHTNING_BOLT)
                    .addImmunity(DamageSource.WITHER)
                    .addHabitat(Blocks.LAPIS_BLOCK)
                    .addHabitat(Blocks.LAPIS_ORE)
                    .removeLoot()//TODO: add loot table later
    );
}
