package top.dragonmounts.world;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;
import top.dragonmounts.world.config.IDragonNestConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class DragonNestStructure extends MapGenStructure {
    public final IDragonNestConfig config;
    public final int salt;
    private int distance = 32;

    public DragonNestStructure(IDragonNestConfig config, int salt) {
        this.config = config;
        this.salt = salt;
    }

    @Override
    public String getStructureName() {
        return this.config.getName();
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World level, BlockPos pos, boolean flag) {
        this.world = level;
        return findNearestStructurePosBySpacing(level, this, pos, this.distance, 8, this.salt, false, 100, flag);
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int x = chunkX;
        int z = chunkZ;
        int distance = this.distance;
        if (chunkX < 0) {
            chunkX += 1 - distance;
        }
        if (chunkZ < 0) {
            chunkZ += 1 - distance;
        }
        int k = chunkX / distance;
        int l = chunkZ / distance;
        Random random = this.world.setRandomSeed(k, l, this.salt);
        if (x == k * distance + random.nextInt(distance - 8) && z == l * distance + random.nextInt(distance - 8)) {
            return this.config.isValid(this.world, x, z);
        } else {
            return false;
        }
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new Start(this.config, this.world, this.rand, chunkX, chunkZ);
    }

    public static class Start extends StructureStart {
        @SuppressWarnings("unused")
        public Start() {}

        public Start(IDragonNestConfig config, World level, Random random, int chunkX, int chunkZ) {
            super(chunkX, chunkZ);
            Rotation[] rotations = Rotation.values();
            BlockPos pos = config.getPosition(level, chunkX, chunkZ);
            this.components.add(new DragonNestTemplate(
                    level.getSaveHandler().getStructureTemplateManager(),
                    config.getTemplate(level, random, pos),
                    config.getLootTable(level, random, pos),
                    rotations[random.nextInt(rotations.length)],
                    pos
            ));
            this.updateBoundingBox();
        }
    }
}
