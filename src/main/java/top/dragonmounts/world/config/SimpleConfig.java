package top.dragonmounts.world.config;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

import static top.dragonmounts.world.config.IDragonNestConfig.isBiome;

public class SimpleConfig implements IDragonNestConfig {
    public final BiomeDictionary.Type limit;
    public final ResourceLocation lootTable;
    public final String name;
    public final String template;
    public final int offset;

    public SimpleConfig(String name, String template, ResourceLocation lootTable, BiomeDictionary.Type limit, int offset) {
        this.name = name;
        this.template = template;
        this.lootTable = lootTable;
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(World level, int chunkX, int chunkZ) {
        return isBiome(level, chunkX, chunkZ, this.limit);
    }

    @Override
    public BlockPos getPosition(World level, int chunkX, int chunkZ) {
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;
        return new BlockPos(x, level.getHeight(x, z) + this.offset, z);
    }

    @Override
    public String getTemplate(World level, Random random, BlockPos pos) {
        return this.template;
    }

    @Override
    public ResourceLocation getLootTable(World level, Random random, BlockPos pos) {
        return this.lootTable;
    }
}
