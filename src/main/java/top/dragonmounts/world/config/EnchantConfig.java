package top.dragonmounts.world.config;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import top.dragonmounts.DragonMountsTags;

import java.util.Random;

import static top.dragonmounts.world.config.IDragonNestConfig.isBiome;

public class EnchantConfig implements IDragonNestConfig {
    public static final String TEMPLATE = DragonMountsTags.MOD_ID + ":enchant";

    public static int getHeight(World level, int chunkX, int chunkZ) {
        if (chunkX < -1875000 || chunkZ < -1875000 || chunkX >= 1875000 || chunkZ >= 1875000) return 0;
        Chunk chunk = level.getChunk(chunkX, chunkZ);
        int[] base = {
                chunk.getHeightValue(5, 8),
                chunk.getHeightValue(13, 8),
                chunk.getHeightValue(8, 5),
                chunk.getHeightValue(8, 13)
        };
        int height = base[0];
        for (int i = 1; i < 3; ++i) {
            if (base[i] > height) height = base[i];
        }
        if (height < 20) return 0;
        for (int i = 0, j = height - 4; i < 3; ++i) {
            if (base[i] > j && base[i] < height) height = base[i];
        }
        return height;
    }

    @Override
    public String getName() {
        return "EnchantDragonNest";
    }

    @Override
    public boolean isValid(World level, int chunkX, int chunkZ) {
        return (chunkX > 124 || chunkZ > 124 || chunkX < -125 || chunkZ < -125) && getHeight(level, chunkX, chunkZ) != 0 && isBiome(level, chunkX, chunkZ, BiomeDictionary.Type.END);
    }

    @Override
    public BlockPos getPosition(World level, int chunkX, int chunkZ) {
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;
        return new BlockPos(x, getHeight(level, chunkX, chunkZ), z);
    }

    @Override
    public String getTemplate(World level, Random random, BlockPos pos) {
        return TEMPLATE;
    }

    @Override
    public ResourceLocation getLootTable(World level, Random random, BlockPos pos) {
        return LootTableList.CHESTS_END_CITY_TREASURE;
    }
}
