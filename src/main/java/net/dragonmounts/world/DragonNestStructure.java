package net.dragonmounts.world;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.LevelUtil;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public abstract class DragonNestStructure extends MapGenStructure {
    private static final int MIN_Y_INDEX = 15;
    public static final String NAME = DragonMountsTags.MOD_ID + "dragon_nest";

    @Override
    public @Nonnull String getStructureName() {
        return NAME;
    }

    protected abstract Start getStructureStartSafely(World level, int chunkX, int chunkZ);

    public static class Start extends StructureStart {
        private ResourceLocation island;

        public Start(World level, List<NestConfig> configs, int chunkX, int chunkZ, int salt) {
            super(chunkX, chunkZ);
            Random random = level.setRandomSeed(chunkX, chunkZ, salt);
            NestConfig config = drawConfig(configs, random);
            ResourceLocation structure = DMUtils.getRandom(config.templates, random);
            TemplateManager manager = level.getSaveHandler().getStructureTemplateManager();
            BlockPos size = manager.getTemplate(null, structure).getSize();
            int centerX = (chunkX << 4) + 8, centerZ = (chunkZ << 4) + 8;
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
                    DMUtils.getRandom(Rotation.values(), random),
                    DMUtils.getRandom(Mirror.values(), random)
            ));
            this.updateBoundingBox();
        }

        @Override
        public void writeToNBT(@Nonnull NBTTagCompound root) {
            if (this.island != null) {
                root.setString("BASE", this.island.toString());
            }
        }

        @Override
        public void readFromNBT(@Nonnull NBTTagCompound root) {
            if (root.hasKey("BASE")) {
                this.island = new ResourceLocation(root.getString("BASE"));
            }
        }

        @Override
        public void generateStructure(@Nonnull World level, @Nonnull Random random, @Nonnull StructureBoundingBox box) {
            super.generateStructure(level, random, box);
            if (this.island == null) return;
            Block block = ForgeRegistries.BLOCKS.getValue(this.island);
            if (block == null) return;
            IBlockState state = block.getDefaultState();
            int radius = Math.max(box.getXSize(), box.getZSize()) / 2 - 1,
                    centerX = box.minX + (box.maxX - box.minX + 1) / 2,
                    centerY = box.minY,
                    centerZ = box.minZ + (box.maxZ - box.minZ + 1) / 2,
                    shrink = 2;
            MutableBlockPosEx place = new MutableBlockPosEx(0, 0, 0);
            do {
                placeCircle(level, place.with(centerX, --centerY, centerZ), radius, state);
            } while ((radius -= random.nextInt(++shrink) + 1) > 3);
        }

        public static NestConfig drawConfig(List<NestConfig> configs, Random random) {
            if (configs.size() > 1) {
                float total = 0.0F;
                for (NestConfig candidate : configs) {
                    total += candidate.weight;
                }
                float target = random.nextFloat() * total;
                for (NestConfig candidate : configs) {
                    if ((target -= candidate.weight) < 0.0F) return candidate;
                }
            }
            return configs.get(0);
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
            NestPlacement placement = config.placement;
            int bottom = MIN_Y_INDEX;
            int height;
            switch (placement) {
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
                    if (height < bottom && config.island != null) {
                        int range = level.getHeight();
                        return randomBetweenInclusive(
                                random,
                                bottom + (int) (range * 0.125F),
                                bottom + (int) (range * 0.375F)
                        );
                    }
                    break;
                default:
                    height = level.getHeight(centerX, centerZ);
            }
            int maxX = minX + size.getX(), maxZ = minZ + size.getZ();
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

        public static void placeCircle(World level, MutableBlockPosEx center, double radius, IBlockState state) {
            double dist = radius * (radius + 0.8);
            for (int offsetX = 0,
                 x = center.getX(),
                 y = center.getY(),
                 z = center.getZ(),
                 end = MathHelper.ceil(radius);
                 offsetX < end;
                 ++offsetX
            ) {
                for (int offsetZ = 0; offsetZ < end; ++offsetZ) {
                    if (offsetX * offsetX + offsetZ * offsetZ > dist) continue;
                    checkAndPlace(level, center.with(x + offsetX, y, z + offsetZ), state);
                    checkAndPlace(level, center.withZ(z - offsetZ), state);
                    checkAndPlace(level, center.with(x - offsetX, y, z + offsetZ), state);
                    checkAndPlace(level, center.withZ(z - offsetZ), state);
                }
            }
        }

        public static void checkAndPlace(World level, BlockPos pos, IBlockState state) {
            if (level.getBlockState(pos).getBlock().isReplaceable(level, pos)) {
                level.setBlockState(pos, state, 3);
            }
        }

        @SuppressWarnings("unused")
        public Start() {}
    }
}
