package net.dragonmounts.world;

import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.LevelUtil;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.common.BiomeDictionary;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import static net.dragonmounts.world.NestConfig.drawConfig;

public class DragonNest {
    private static final int MIN_Y_INDEX = 15;
    public final DragonNestRegistry registry;
    public final ResourceLocation name;
    public final Predicate<Set<BiomeDictionary.Type>> biomes;
    public final List<NestConfig> configs;

    public DragonNest(
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

    public Start populateStart(World level, int chunkX, int chunkZ, Random random) {
        return new Start(level, drawConfig(this.configs, random), chunkX, chunkZ, this.registry.salt);
    }

    public static class Start extends StructureStart {
        public Start(World level, NestConfig config, int chunkX, int chunkZ, int salt) {
            super(chunkX, chunkZ);
            Random random = level.setRandomSeed(chunkX, chunkZ, salt);
            ResourceLocation structure = DMUtils.getRandom(config.templates, random);
            Rotation rotation = DMUtils.getRandom(Rotation.values(), random);
            TemplateManager manager = level.getSaveHandler().getStructureTemplateManager();
            BlockPos size = manager.getTemplate(null, structure).transformedSize(rotation);
            int centerX = (chunkX << 4) + 7, centerZ = (chunkZ << 4) + 7;
            int posX = centerX - size.getX() / 2, posZ = centerZ - size.getZ() / 2;
            this.components.add(new DragonNestPiece(
                    manager,
                    structure,
                    new BlockPos(posX, findSuitableY(
                            level,
                            config,
                            posX,
                            posZ,
                            centerX,
                            centerZ,
                            size,
                            random
                    ), posZ),
                    rotation,
                    DMUtils.getRandom(Mirror.values(), random)
            ));
            this.updateBoundingBox();
        }

        public static int randomBetweenInclusive(Random random, int minInclusive, int maxInclusive) {
            return random.nextInt(maxInclusive - minInclusive + 1) + minInclusive;
        }

        public static int getRandomWithinInterval(Random random, int min, int max) {
            return min < max ? randomBetweenInclusive(random, min, max) : max;
        }

        public static int findSuitableY(
                World level,
                NestConfig config,
                int minX,
                int minZ,
                int centerX,
                int centerZ,
                BlockPos size,
                Random random
        ) {
            int bottom = MIN_Y_INDEX;
            int height;
            switch (config.placement) {
                case IN_MOUNTAIN:
                    height = getRandomWithinInterval(random, 70, level.getHeight(centerX, centerZ) - size.getY());
                    break;
                case UNDERGROUND:
                    height = getRandomWithinInterval(random, bottom, level.getHeight(centerX, centerZ) - size.getY());
                    break;
                case PARTLY_BURIED:
                    height = level.getHeight(centerX, centerZ) - randomBetweenInclusive(random, 2, size.getY() / 2);
                    break;
                case IN_NETHER:
                    height = randomBetweenInclusive(random, 27, 127 - size.getY());
                    break;
                case IN_CLOUDS: {
                    int maxY = level.getHeight();
                    return Math.max(
                            level.getHeight(centerX, centerZ) + MIN_Y_INDEX,
                            randomBetweenInclusive(random, maxY - 96, maxY - 48)
                    );
                }
                case ON_LAND_SURFACE:
                    height = level.getHeight(centerX, centerZ);
                    if (height > level.getSeaLevel()) break;
                    return level.getSeaLevel();
                default:
                    height = level.getHeight(centerX, centerZ);
            }
            int sizeX = size.getX(), sizeZ = size.getZ(), maxX, maxZ;
            if (sizeX < 16) {
                maxX = minX + sizeX;
            } else {
                maxX = centerX + 8;
                minX = centerX - 7;
            }
            if (sizeZ < 16) {
                maxZ = minZ + sizeZ;
            } else {
                maxZ = centerZ + 8;
                minZ = centerZ - 7;
            }
            MutableBlockPosEx pos = new MutableBlockPosEx(0, 0, 0);
            do {
                int supports = 0;
                if (!LevelUtil.isAir(level, pos.with(minX, height, minZ))) ++supports;
                if (!LevelUtil.isAir(level, pos.withZ(maxZ))) ++supports;
                if (!LevelUtil.isAir(level, pos.withX(maxX))) ++supports;
                if (supports == 3 || (supports == 2 && !LevelUtil.isAir(level, pos.withZ(maxZ)))) return height;
            } while (--height > bottom);
            return bottom;
        }

        @SuppressWarnings("unused")
        public Start() {}
    }
}
