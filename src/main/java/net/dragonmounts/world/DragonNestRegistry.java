package net.dragonmounts.world;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.LogUtil;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

public class DragonNestRegistry extends DragonNestStructure {
    private final Object2ObjectRBTreeMap<ResourceLocation, DragonNestImpl> registry = new Object2ObjectRBTreeMap<>();
    private final Reference2ObjectOpenHashMap<Biome, List<DragonNestImpl>> lookup = new Reference2ObjectOpenHashMap<>();
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

    public DragonNestImpl byName(ResourceLocation name) {
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
                        this.registry.put(key, new DragonNestImpl(this, key, BiomeCondition.parse(JsonUtils.getJsonObject(json, "biome")), list));
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

    public List<DragonNestImpl> getValidNests(Biome biome) {
        List<DragonNestImpl> nests = this.lookup.get(biome);
        if (nests == null) {
            Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
            ImmutableList.Builder<DragonNestImpl> builder = ImmutableList.builder();
            for (DragonNestImpl nest : this.registry.values()) {
                if (nest.biomes.test(types)) {
                    builder.add(nest);
                }
            }
            this.lookup.put(biome, nests = builder.build());
        }
        return nests;
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
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
        Random random = this.world.setRandomSeed(x, z, this.salt);
        return chunkX == x * spacing + random.nextInt(distance) &&
                chunkZ == z * spacing + random.nextInt(distance) &&
                !this.getValidNests(this.world.getBiomeProvider().getBiome(
                        getChunkCenter(chunkX, chunkZ, 0))
                ).isEmpty();
    }

    @Override
    public @Nullable BlockPos getNearestStructurePos(@Nonnull World level, @Nonnull BlockPos pos, boolean findUnexplored) {
        ImmutablePair<BlockPos, DragonNestImpl> result = this.findNearestNest(level, pos, 100, findUnexplored, Predicates.alwaysTrue());
        return result == null ? null : result.getLeft();
    }

    @Override
    protected Start getStructureStartSafely(World level, int chunkX, int chunkZ) {
        return DMUtils.getRandom(this.getValidNests(level.getBiomeProvider().getBiome(
                getChunkCenter(chunkX, chunkZ, 0))
        ), new Random(chunkX + (long) chunkZ * this.salt)).getStructureStartSafely(level, chunkX, chunkZ);
    }

    @Override
    protected @Nonnull StructureStart getStructureStart(int chunkX, int chunkZ) {
        return this.getStructureStartSafely(this.world, chunkX, chunkZ);
    }

    public final ImmutablePair<BlockPos, DragonNestImpl> findNearestNest(World level, BlockPos center, int maxAttempts, boolean findUnexplored, Predicate<DragonNestImpl> target) {
        this.world = level;
        int spacing = this.spacing, distance = spacing - this.separation, salt = this.salt;
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
                        List<DragonNestImpl> nests = this.getValidNests(level.getBiomeProvider().getBiome(getChunkCenter(chunkX, chunkZ, 0)));
                        if (!nests.isEmpty()) {
                            DragonNestImpl nest = DMUtils.getRandom(nests, new Random(chunkX + (long) chunkZ * salt));
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
}
