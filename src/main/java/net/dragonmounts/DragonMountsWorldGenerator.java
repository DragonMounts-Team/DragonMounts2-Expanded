package net.dragonmounts;

import net.dragonmounts.util.LogUtil;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.IWorldGenerator;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

import static net.dragonmounts.util.LevelUtil.getSurface;

/**
 * Handles world generation for dragon nests, make a separate package if we are gonna use Mappers to optimize instead of the IWorldGenerator
 */
public class DragonMountsWorldGenerator implements IWorldGenerator {
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
    public static final ResourceLocation ENCHANT = new ResourceLocation(DragonMountsTags.MOD_ID, "enchant");


    @Override
    public void generate(Random random, int x, int z, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.isRemote ||
                // isGenerationDisabled:
                DragonMountsConfig.limitedDimensions.contains(world.provider.getDimension()) != DragonMountsConfig.allowDeclaredDimensionsOnly
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
        Set<Type> types = BiomeDictionary.getTypes(world.getBiome(height));
        boolean isMountainOrBeach = types.contains(Type.MOUNTAIN) || world.getBiomeForCoordsBody(height) == Biomes.STONE_BEACH;
        boolean isSnowy = types.contains(Type.SNOWY);
        boolean isJungle = types.contains(Type.JUNGLE);
        boolean isForest = types.contains(Type.FOREST);
        boolean isSwamp = types.contains(Type.SWAMP);
        boolean isDesert = types.contains(Type.SANDY);
        boolean isPlains = types.contains(Type.PLAINS);
        boolean isMesa = types.contains(Type.MESA);
        boolean isOcean = types.contains(Type.OCEAN);

        if (isOcean && random.nextInt((DragonMountsConfig.OceanNestRarity)) == 1) {
            loadStructure(height.withY(height.getY() + 10), world, random.nextBoolean() ? AETHER : MOONLIGHT, LootTableList.CHESTS_SIMPLE_DUNGEON, random);

        } else if (isSnowy && random.nextInt((DragonMountsConfig.IceNestRarity)) == 1 && canSpawnHere(world, height, 7, false)) {

            loadStructure(height.withY(height.getY() - 2), world, ICE, LootTableList.CHESTS_SIMPLE_DUNGEON, random);

        } else if (isJungle && random.nextInt((DragonMountsConfig.JungleNestRarity)) == 1) {
            loadStructure(height, world, FOREST1, LootTableList.CHESTS_SIMPLE_DUNGEON, random);

        } else if (isDesert && random.nextInt((DragonMountsConfig.SunlightNestRarity)) == 1 && canSpawnHere(world, height, 22, false)) {

            loadStructure(height.withY(height.getY() - 10), world, SUNLIGHT, LootTableList.CHESTS_DESERT_PYRAMID, random);

        } else if (isMesa && random.nextInt((DragonMountsConfig.TerraNestRarity)) == 1 && canSpawnHere(world, height, 4, false)) {
            loadStructure(height.withY(height.getY() - 2), world, TERRA, LootTableList.CHESTS_SIMPLE_DUNGEON, random);

        } else if ((isSwamp) && random.nextInt((DragonMountsConfig.WaterNestRarity)) == 1 && canSpawnHere(world, height, 4, true)) {
            loadStructure(height.withY(height.getY() - 4), world, WATER3, LootTableList.CHESTS_SIMPLE_DUNGEON, random);

        } else if ((isPlains || isForest) && random.nextInt((DragonMountsConfig.ForestNestRarity)) == 1 && canSpawnHere(world, height, 4, false)) {
            loadStructure(height.withY(height.getY() - 2), world, FOREST2, LootTableList.CHESTS_SIMPLE_DUNGEON, random);

        } else if (isMountainOrBeach && random.nextInt(DragonMountsConfig.FireNestRarity) == 1 && canSpawnHere(world, height, 4, false)) {
            loadStructure(height.withY(height.getY() - 2), world, FIRE, LootTableList.CHESTS_SIMPLE_DUNGEON, random);
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
        if (random.nextInt(DragonMountsConfig.netherNestRarity) == 1) {
            MutableBlockPosEx pos = getNetherHeight(
                    world,
                    chunkX * 16 + random.nextInt(DragonMountsConfig.netherNestRarerityInX),
                    chunkZ * 16 + random.nextInt(DragonMountsConfig.netherNestRarerityInZ)
            );
            if (pos != null && canSpawnNetherHere(world, pos, 6)) {
                loadStructure(pos, world, NETHER, LootTableList.CHESTS_NETHER_BRIDGE, random);
            }
        }
    }

    public static void generateZombieAtNether(World world, Random random, int chunkX, int chunkZ) {
        int x = chunkX * 16 + random.nextInt(DragonMountsConfig.zombieNestRarerityInX);
        int z = chunkZ * 16 + random.nextInt(DragonMountsConfig.zombieNestRarerityInZ);
        MutableBlockPosEx pos = new MutableBlockPosEx(x, 85, z);
        for (int y = 85; y >= 5; --y) {
            if (world.getBlockState(pos.withY(y)).isBlockNormalCube() && random.nextInt(DragonMountsConfig.zombieNestRarity) == 1) {
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
                loadStructure(pos.with(x, y - 10, z), world, random.nextBoolean() ? SKELETON : ZOMBIE, LootTableList.CHESTS_NETHER_BRIDGE, random);
                return;
            }
        }
    }

    public static void generateNestAtEnd(World world, Random random, int chunkX, int chunkZ) {
        if (random.nextInt(DragonMountsConfig.EnchantNestRarity) == 1) {
            int x = (chunkX * 16) + random.nextInt(16);
            int z = (chunkZ * 16) + random.nextInt(16);
            MutableBlockPosEx height = getSurface(world, x, z);

            if (canSpawnHere(world, height, 5, false)) {
                loadStructure(height.withY(height.getY() - 1), world, ENCHANT, LootTableList.CHESTS_END_CITY_TREASURE, random);
            }
        }
    }

    public static void loadStructure(MutableBlockPosEx pos, World world, ResourceLocation structure, @Nullable ResourceLocation lootTable, Random rand) {
        WorldServer worldserver = (WorldServer) world;
        if (DragonMountsConfig.isDebug()) {
            LogUtil.LOGGER.info("Placing Dragon Nest at [{}]: {}", pos.toString(), structure);
        }
        Template template = worldserver.getStructureTemplateManager().getTemplate(world.getMinecraftServer(), structure);
        IBlockState iblockstate = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
        BlockPos size = template.getSize();
        template.addBlocksToWorldChunk(world, pos.with(
                pos.getX() - size.getX() / 2,
                pos.getY() + 1,
                pos.getZ() - size.getZ() / 2
        ), new PlacementSettings().setIgnoreEntities(false).setIgnoreStructureBlock(true));
        if (lootTable == null) return;
        int sizeX = size.getX(), sizeY = size.getY(), sizeZ = size.getZ();
        int posX = pos.getX(), posY = pos.getY(), posZ = pos.getZ();
        for (int x = 0; x <= sizeX; ++x) {
            for (int y = 0; y <= sizeY; ++y) {
                for (int z = 0; z <= sizeZ; ++z) {
                    TileEntity tileentity = world.getTileEntity(pos.with(posX + x, posY + y, posZ + z));
                    if (tileentity instanceof TileEntityChest) {
                        ((TileEntityChest) tileentity).setLootTable(lootTable, rand.nextLong());
                    }
                }
            }
        }
    }
}