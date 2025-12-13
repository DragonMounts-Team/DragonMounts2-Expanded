package net.dragonmounts.world;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.LogUtil;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import static net.dragonmounts.util.LevelUtil.getChunkCenter;
import static net.minecraftforge.common.crafting.CraftingHelper.GSON;
import static net.minecraftforge.common.crafting.CraftingHelper.findFiles;

/// @see net.minecraft.world.gen.structure.MapGenStructure
public class DragonNestRegistry {
    public static String SAVED_DATA = "DragonMounts.DragonNest";

    private static final int RANGE = 8;
    private final Object2ObjectRBTreeMap<ResourceLocation, DragonNest> registry = new Object2ObjectRBTreeMap<>();
    private final Reference2ObjectOpenHashMap<Biome, List<DragonNest>> lookup = new Reference2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<Structures> structures = new Int2ObjectOpenHashMap<>();
    public final int salt;
    public final int spacing;
    public final int separation;

    public DragonNestRegistry(int salt, int spacing, int separation) {
        this.salt = salt;
        this.spacing = spacing;
        this.separation = separation;
    }

    public Collection<ResourceLocation> keys() {
        return this.registry.keySet();
    }

    public DragonNest byName(ResourceLocation name) {
        return this.registry.get(name);
    }

    public void loadNests(ModContainer mod) {
        JsonContext ctx = new JsonContext(mod.getModId());
        findFiles(mod, "data/" + mod.getModId() + "/worldgen/structure", null, (root, file) -> {
                    String relative = root.relativize(file).toString();
                    if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_")) return true;
                    ResourceLocation key = new ResourceLocation(
                            ctx.getModId(),
                            FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/")
                    );
                    try (BufferedReader reader = Files.newBufferedReader(file)) {
                        JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
                        if (json == null || json.isJsonNull()) throw new JsonSyntaxException("Json cannot be null");
                        if (!"dragonmounts:dragon_nest".equals(ctx.appendModId(JsonUtils.getString(json, "type"))))
                            return false;
                        ImmutableList.Builder<NestConfig> configs = ImmutableList.builder();
                        for (JsonElement element : JsonUtils.getJsonArray(json, "configs")) {
                            if (element.isJsonObject()) {
                                configs.add(NestConfig.parse(element.getAsJsonObject(), ctx));
                            } else throw new JsonParseException("Unexpected element in object only array");
                        }
                        List<NestConfig> list = configs.build();
                        if (list.isEmpty()) throw new JsonSyntaxException("No configs defined");
                        this.registry.put(key, new DragonNest(this, key, BiomeCondition.parse(JsonUtils.getJsonObject(json, "biome")), list));
                    } catch (JsonParseException e) {
                        LogUtil.LOGGER.error("Parsing error loading structure {}", key, e);
                        return false;
                    } catch (IOException e) {
                        LogUtil.LOGGER.error("Couldn't read structure {} from {}", key, file, e);
                        return false;
                    }
                    return true;
                },
                true,
                true
        );
    }

    public List<DragonNest> getValidNests(Biome biome) {
        List<DragonNest> nests = this.lookup.get(biome);
        if (nests == null) {
            Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
            ImmutableList.Builder<DragonNest> builder = ImmutableList.builder();
            for (DragonNest nest : this.registry.values()) {
                if (nest.biomes.test(types)) {
                    builder.add(nest);
                }
            }
            this.lookup.put(biome, nests = builder.build());
        }
        return nests;
    }

    public int getDimensionBasedSalt(World level) {
        return this.salt + level.provider.getDimensionType().getName().hashCode();
    }

    protected boolean canSpawnAtCoords(World level, int chunkX, int chunkZ) {
        int spacing = this.spacing, distance = spacing - this.separation;
        int x = chunkX, z = chunkZ;
        if (x < 0) {
            x -= spacing - 1;
        }
        if (z < 0) {
            z -= spacing - 1;
        }
        x /= spacing;
        z /= spacing;
        Random random = level.setRandomSeed(x, z, this.getDimensionBasedSalt(level));
        return chunkX == x * spacing + random.nextInt(distance) &&
                chunkZ == z * spacing + random.nextInt(distance) &&
                !this.getValidNests(level.getBiomeProvider().getBiome(
                        getChunkCenter(chunkX, chunkZ, 0))
                ).isEmpty();
    }

    protected @Nonnull StructureStart populateStart(World level, int chunkX, int chunkZ, Random random) {
        return DMUtils.getRandom(this.getValidNests(level.getBiomeProvider().getBiome(
                getChunkCenter(chunkX, chunkZ, 0))
        ), random).populateStart(level, chunkX, chunkZ, random);
    }

    public final ImmutablePair<BlockPos, DragonNest> findNearestNest(World level, BlockPos center, int maxAttempts, boolean findUnexplored, Predicate<DragonNest> target) {
        int spacing = this.spacing, distance = spacing - this.separation, salt = this.getDimensionBasedSalt(level);
        int centerChunkX = center.getX() >> 4, centerChunkZ = center.getZ() >> 4;
        Random random = new Random();
        for (int attempts = 0; attempts <= maxAttempts; ++attempts) {
            for (int x = -attempts; x <= attempts; ++x) {
                boolean flag = x == attempts || x == -attempts;
                for (int z = -attempts; z <= attempts; ++z) {
                    if (flag || z == attempts || z == -attempts) {
                        int chunkX = centerChunkX + spacing * x, chunkZ = centerChunkZ + spacing * z;
                        if (chunkX < 0) {
                            chunkX -= spacing - 1;
                        }
                        if (chunkZ < 0) {
                            chunkZ -= spacing - 1;
                        }
                        chunkX /= spacing;
                        chunkZ /= spacing;
                        Random structure = level.setRandomSeed(chunkX, chunkZ, salt);
                        chunkX = chunkX * spacing + structure.nextInt(distance);
                        chunkZ = chunkZ * spacing + structure.nextInt(distance);
                        MapGenBase.setupChunkSeed(level.getSeed(), random, chunkX, chunkZ);
                        random.nextInt();
                        List<DragonNest> nests = this.getValidNests(level.getBiomeProvider().getBiome(getChunkCenter(chunkX, chunkZ, 0)));
                        if (!nests.isEmpty()) {
                            DragonNest nest = DMUtils.getRandom(nests, random);
                            if (target.test(nest)) {
                                if (!findUnexplored || !level.isChunkGeneratedAt(chunkX, chunkZ)) {
                                    return new ImmutablePair<>(getChunkCenter(chunkX, chunkZ, 64), nest);
                                }
                            } else if (attempts == 0) break;
                        } else if (attempts == 0) break;
                    }
                }
                if (attempts == 0) break;
            }
        }
        return null;
    }

    protected Structures getStructures(World level) {
        int dimension = level.provider.getDimension();
        Structures structures = this.structures.get(dimension);
        if (structures == null) {
            structures = (Structures) level.getPerWorldStorage().getOrLoadData(Structures.class, SAVED_DATA);
            if (structures == null) {
                level.getPerWorldStorage().setData(SAVED_DATA, structures = new Structures(SAVED_DATA));
            } else {
                structures.reload(level);
            }
            this.structures.put(dimension, structures);
        }
        return structures;
    }

    public void generate(World level, int centerX, int centerZ, Random random) {
        long seed = level.getSeed();
        random.setSeed(seed);
        long seedX = random.nextLong();
        long seedZ = random.nextLong();
        Structures structures = this.getStructures(level);
        for (int x = centerX - RANGE; x <= centerX + RANGE; ++x) {
            for (int z = centerZ - RANGE; z <= centerZ + RANGE; ++z) {
                random.setSeed(((long) x * seedX) ^ ((long) z * seedZ) ^ seed);
                this.prepareStructures(level, structures, x, z, random);
            }
        }
        seedX = seedX / 2L * 2L + 1L;
        seedZ = seedZ / 2L * 2L + 1L;
        random.setSeed((long) centerX * seedX + (long) centerZ * seedZ ^ seed);
        this.placeStructures(level, structures, centerX, centerZ, random);
    }

    protected final synchronized void prepareStructures(World level, Structures structures, int chunkX, int chunkZ, Random random) {
        if (structures.instances.containsKey(ChunkPos.asLong(chunkX, chunkZ))) return;
        random.nextInt();
        try {
            if (this.canSpawnAtCoords(level, chunkX, chunkZ)) {
                long chunk = ChunkPos.asLong(chunkX, chunkZ);
                StructureStart start = this.populateStart(level, chunkX, chunkZ, random);
                structures.instances.put(chunk, start);
                if (start.isSizeableStructure()) {
                    structures.persistent(chunk, start.writeStructureComponentsToNBT(chunkX, chunkZ));
                }
            }
        } catch (Throwable throwable) {
            LogUtil.LOGGER.error("Failed to generate structure", throwable);
        }
    }

    public final synchronized void placeStructures(World level, Structures structures, int chunkX, int chunkZ, Random random) {
        int minX = (chunkX << 4) + 8, minZ = (chunkZ << 4) + 8;
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        for (StructureStart start : structures.instances.values()) {
            if (start.isSizeableStructure() && start.isValidForPostProcess(pos) && start.getBoundingBox().intersectsWith(minX, minZ, minX + 15, minZ + 15)) {
                start.generateStructure(level, random, new StructureBoundingBox(minX, minZ, minX + 15, minZ + 15));
                start.notifyPostProcessAt(pos);
                int posX = start.getChunkPosX(), posZ = start.getChunkPosZ();
                structures.persistent(
                        ChunkPos.asLong(posX, posZ),
                        start.writeStructureComponentsToNBT(posX, posZ)
                );
            }
        }
    }
}
