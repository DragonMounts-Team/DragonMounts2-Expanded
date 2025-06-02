package net.dragonmounts;

import net.dragonmounts.config.DMConfig;
import net.dragonmounts.util.LogUtil;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;
import java.util.Set;

import static net.dragonmounts.util.LevelUtil.getSurface;

/**
 * Handles world generation for dragon nests, make a separate package if we are gonna use Mappers to optimize instead of the IWorldGenerator
 */
public class DragonMountsWorldGenerator implements IWorldGenerator {
    private static final PlacementSettings PLACEMENT_SETTINGS = new PlacementSettings().setIgnoreEntities(false).setIgnoreStructureBlock(true);
    public static final ResourceLocation AETHER = new ResourceLocation(DragonMountsTags.MOD_ID, "aether");
    public static final ResourceLocation MOONLIGHT = new ResourceLocation(DragonMountsTags.MOD_ID, "moonlight");
    public static final ResourceLocation ICE = new ResourceLocation(DragonMountsTags.MOD_ID, "ice");
    public static final ResourceLocation FOREST1 = new ResourceLocation(DragonMountsTags.MOD_ID, "forest1");
    public static final ResourceLocation SUNLIGHT = new ResourceLocation(DragonMountsTags.MOD_ID, "sunlight");
    public static final ResourceLocation TERRA = new ResourceLocation(DragonMountsTags.MOD_ID, "terra");
    public static final ResourceLocation WATER3 = new ResourceLocation(DragonMountsTags.MOD_ID, "water3");
    public static final ResourceLocation FOREST2 = new ResourceLocation(DragonMountsTags.MOD_ID, "forest2");
    public static final ResourceLocation FIRE = new ResourceLocation(DragonMountsTags.MOD_ID, "fire");
    public static final ResourceLocation NETHER = new ResourceLocation(DragonMountsTags.MOD_ID, "nether");
    public static final ResourceLocation ZOMBIE = new ResourceLocation(DragonMountsTags.MOD_ID, "zombie");
    public static final ResourceLocation SKELETON = new ResourceLocation(DragonMountsTags.MOD_ID, "skeleton");
    public static final ResourceLocation ENCHANTED = new ResourceLocation(DragonMountsTags.MOD_ID, "enchanted");

    @Override
    public void generate(Random random, int x, int z, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.isRemote ||
                // isGenerationDisabled:
                DMConfig.LIMITED_DIMENSIONS.value.contains(world.provider.getDimension()) != DMConfig.ALLOW_DECLARED_DIMENSIONS_ONLY.value
        ) return;
        DimensionType type = world.provider.getDimensionType();
        if (type == DimensionType.NETHER) {
            generateNestAtNether(world, random, x, z);
            generateZombieAtNether(world, random, x, z);
        } else if (type == DimensionType.THE_END && (x > 2000 || z > 2000 || x < -2000 || z < 2000)) {
            generateNestAtEnd(world, random, x, z);
        } else {
            generateNestAtSurface(world, random, x, z);
        }
    }

    private static MutableBlockPosEx getNetherHeight(World world, int x, int z) {
        MutableBlockPosEx pos = new MutableBlockPosEx(x, 1, z);
        for (int i = 1, end = world.getHeight(); i < end; pos.withY(++i)) {
            if (world.isAirBlock(pos) && world.getBlockState(pos.withY(i - 1)).getBlock() == Blocks.LAVA) {
                return pos;
            }
        }
        return null;
    }

    private static boolean canReplace(World world, BlockPos pos) {
        Material material = world.getBlockState(pos).getMaterial();
        // we think it's replaceable if it's air / liquid / snow, plants, or leaves
        return material.isReplaceable() || material == Material.PLANTS;
    }

    private static boolean isSolid(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().isSolid();
    }

    private static boolean isLava(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.LAVA;
    }

    private static boolean isWater(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.WATER;
    }

    public static void generateNestAtSurface(World world, Random random, int chunkX, int chunkZ) {
        int x = (chunkX * 16) + random.nextInt(16);
        int z = (chunkZ * 16) + random.nextInt(16);
        MutableBlockPosEx height = getSurface(world, x, z);
        Biome biome = world.getBiomeForCoordsBody(height);
        Set<Type> types = BiomeDictionary.getTypes(biome);
        if (types.contains(Type.OCEAN) && random.nextInt(DMConfig.OceanNestRarity) == 1) {

            loadStructure(world, height.withY(height.getY() + 10), random.nextBoolean() ? AETHER : MOONLIGHT);

        } else if (types.contains(Type.SNOWY) && random.nextInt(DMConfig.IceNestRarity) == 1 && canSpawnHere(world, height, 7, false)) {

            loadStructure(world, height.withY(height.getY() - 2), ICE);

        } else if (types.contains(Type.JUNGLE) && random.nextInt(DMConfig.JungleNestRarity) == 1) {

            loadStructure(world, height, FOREST1);

        } else if (types.contains(Type.SANDY) && random.nextInt(DMConfig.SunlightNestRarity) == 1 && canSpawnHere(world, height, 22, false)) {

            loadStructure(world, height.withY(height.getY() - 10), SUNLIGHT);

        } else if (types.contains(Type.MESA) && random.nextInt(DMConfig.TerraNestRarity) == 1 && canSpawnHere(world, height, 4, false)) {

            loadStructure(world, height.withY(height.getY() - 2), TERRA);

        } else if (types.contains(Type.SWAMP) && random.nextInt(DMConfig.WaterNestRarity) == 1 && canSpawnHere(world, height, 4, true)) {

            loadStructure(world, height.withY(height.getY() - 4), WATER3);

        } else if ((types.contains(Type.PLAINS) || types.contains(Type.FOREST)) && random.nextInt(DMConfig.ForestNestRarity) == 1 && canSpawnHere(world, height, 4, false)) {

            loadStructure(world, height.withY(height.getY() - 2), FOREST2);

        } else if ((types.contains(Type.MOUNTAIN) || biome == Biomes.STONE_BEACH) && random.nextInt(DMConfig.FireNestRarity) == 1 && canSpawnHere(world, height, 4, false)) {

            loadStructure(world, height.withY(height.getY() - 2), FIRE);

        } else if (types.contains(Type.VOID) && types.contains(Type.MAGICAL) && random.nextInt(DMConfig.OceanNestRarity) == 0) {

            loadStructure(world, height.withY(height.getY() + 10), AETHER);

        }
    }

    private static boolean canSpawnHere(World world, BlockPos posAboveGround, int step, boolean allowWater) {
        int x = posAboveGround.getX(), y = posAboveGround.getY(), z = posAboveGround.getZ(), positiveZ = z + step, negativeZ = z - step;
        MutableBlockPosEx pos = new MutableBlockPosEx(x, y, z);
        // if Y > 20 and all corners are replaceable, it's okay to spawn the structure
        return posAboveGround.getY() > 20 && canReplace( // (x       , y, z + step)
                world, pos
        ) && canReplace( // (x       , y, z + step)
                world, pos.withZ(positiveZ)
        ) && canReplace( // (x       , y, z - step)
                world, pos.withZ(negativeZ)
        ) && canReplace( // (x - step, y, z - step)
                world, pos.withX(x - step)
        ) && canReplace( // (x - step, y, z       )
                world, pos.withZ(z)
        ) && canReplace( // (x - step, y, z + step)
                world, pos.withZ(positiveZ)
        ) && canReplace( // (x + step, y, z + step)
                world, pos.withX(x + step)
        ) && canReplace( // (x + step, y, z - step)
                world, pos.withZ(negativeZ)
        ) && canReplace( // (x + step, y, z       )
                world, pos.withZ(z)
        ) && (isSolid( // (x + step, y - 1, z       )
                world, pos.withY(--y)
        ) && isSolid(  // (x - step, y - 1, z       )
                world, pos.withX(x - step)
        ) && isSolid(  // (x       , y - 1, z - step)
                world, pos.with(x, y, negativeZ)
        ) && isSolid(  // (x       , y - 1, z + step)
                world, pos.withZ(positiveZ)
        ) || (allowWater && isWater( // (x       , y - 1, z + step)
                world, pos.with(x, y, positiveZ)
        ) && isWater( // (x       , y - 1, z - step)
                world, pos.withZ(negativeZ)
        ) && isWater( // (x + step, y - 1, z)
                world, pos.with(x + step, y, z)
        ) && isWater( // (x - step, y - 1, z)
                world, pos.with(x - step, y, z)
        )));
    }

    private static boolean canSpawnNetherHere(World world, BlockPos posAboveGround, int step) {
        int x = posAboveGround.getX(), z = posAboveGround.getZ();
        MutableBlockPosEx pos = new MutableBlockPosEx(posAboveGround.getX(), posAboveGround.getY() - 1, z);
        // if Y > 20 and all corners pass the test, it's okay to spawn the structure && below7Solid && below4Solid
        return posAboveGround.getY() > 20 && isLava(
                world, pos.withZ(z + step)
        ) && isLava(
                world, pos.withZ(z - step)
        ) && isLava(
                world, pos.with(x + step, posAboveGround.getY() - 1, z)
        ) && isLava(
                world, pos.withX(x - step)
        );
    }

    public static void generateNestAtNether(World world, Random random, int chunkX, int chunkZ) {
        if (random.nextInt(DMConfig.netherNestRarity) == 1) {
            MutableBlockPosEx pos = getNetherHeight(
                    world,
                    chunkX * 16 + random.nextInt(DMConfig.netherNestRarerityInX),
                    chunkZ * 16 + random.nextInt(DMConfig.netherNestRarerityInZ)
            );
            if (pos != null && canSpawnNetherHere(world, pos, 6)) {
                loadStructure(world, pos, NETHER);
            }
        }
    }

    public static void generateZombieAtNether(World world, Random random, int chunkX, int chunkZ) {
        int x = chunkX * 16 + random.nextInt(DMConfig.zombieNestRarerityInX);
        int z = chunkZ * 16 + random.nextInt(DMConfig.zombieNestRarerityInZ);
        MutableBlockPosEx pos = new MutableBlockPosEx(x, 85, z);
        for (int y = 85; y >= 5; --y) {
            if (world.getBlockState(pos.withY(y)).isBlockNormalCube() && random.nextInt(DMConfig.zombieNestRarity) == 1) {
                boolean invalid = false;
                for (int Y = 1; Y < 4; ++Y) {
                    for (int Z = 0; Z < 3; ++Z) {
                        for (int X = 0; X < 3; ++X) {
                            Block block = world.getBlockState(pos.with(X + x, Y + y, Z + z)).getBlock();
                            // == Blocks.LAVA ?
                            if (block != Blocks.AIR && block != Blocks.LAVA) {
                                invalid = true;
                                break;
                            }
                        }
                    }
                }
                if (invalid) continue;
                loadStructure(world, pos.with(x, y - 10, z), random.nextBoolean() ? SKELETON : ZOMBIE);
                return;
            }
        }
    }

    public static void generateNestAtEnd(World world, Random random, int chunkX, int chunkZ) {
        if (random.nextInt(DMConfig.EnchantNestRarity) == 1) {
            int x = (chunkX * 16) + random.nextInt(16);
            int z = (chunkZ * 16) + random.nextInt(16);
            MutableBlockPosEx height = getSurface(world, x, z);
            if (canSpawnHere(world, height, 5, false)) {
                loadStructure(world, height.descent(), ENCHANTED);
            }
        }
    }

    public static void loadStructure(World world, MutableBlockPosEx pos, ResourceLocation structure) {
        if (DMConfig.DEBUG_MODE.value) {
            LogUtil.LOGGER.info("Placing Dragon Nest at [{}]: {}", pos, structure);
        }
        Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(), structure);
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 2); // ?
        BlockPos size = template.getSize();
        template.addBlocksToWorldChunk(world, pos.with(
                pos.getX() - size.getX() / 2,
                pos.getY() + 1,
                pos.getZ() - size.getZ() / 2
        ), PLACEMENT_SETTINGS);
    }
}