package top.dragonmounts.world.config;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public interface IDragonNestConfig {
    String getName();

    boolean isValid(World level, int chunkX, int chunkZ);

    BlockPos getPosition(World level, int chunkX, int chunkZ);

    String getTemplate(World level, Random random, BlockPos pos);

    ResourceLocation getLootTable(World level, Random random, BlockPos pos);

    static boolean isBiome(World level, int chunkX, int chunkZ, BiomeDictionary.Type type) {
        Biome biome = level.getBiomeProvider().getBiome(new BlockPos(chunkX << 4, 0, chunkZ << 4));
        //noinspection ConstantValue
        return biome != null && BiomeDictionary.hasType(biome, type);
    }
}
