package net.dragonmounts.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class DragonNestImpl extends DragonNestStructure {
    public final DragonNestRegistry registry;
    public final ResourceLocation name;
    public final Predicate<Set<BiomeDictionary.Type>> biomes;
    public final List<NestConfig> configs;

    public DragonNestImpl(
            DragonNestRegistry registry,
            ResourceLocation name,
            Predicate<Set<BiomeDictionary.Type>> biomes,
            List<NestConfig> configs
    ) {
        assert !configs.isEmpty();
        this.registry = registry;
        this.name = name;
        this.biomes = biomes;
        this.configs = configs;
    }

    @Override
    public @Nullable BlockPos getNearestStructurePos(@Nonnull World level, @Nonnull BlockPos pos, boolean flag) {
        this.world = level;
        return this.registry.findNearestNest(level, pos, 100, flag, this::equals);
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return true;
    }

    @Override
    @Deprecated
    protected @Nonnull StructureStart getStructureStart(int chunkX, int chunkZ) {
        return this.getStructureStartSafely(this.world, chunkX, chunkZ);
    }

    @Override
    protected Start getStructureStartSafely(World level, int chunkX, int chunkZ) {
        return new Start(level, this.configs, chunkX, chunkZ, this.registry.salt);
    }
}
