package net.dragonmounts.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class LevelUtil {
    public static boolean isAir(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos);
    }

    public static MutableBlockPosEx getSurface(World level, int x, int z) {
        return new MutableBlockPosEx(x, level.getHeight(x, z), z);
    }

    public static BlockPos getChunkCenter(int chunkX, int chunkZ, int height) {
        return new BlockPos((chunkX << 4) + 8, height, (chunkZ << 4) + 8);
    }

    public static void playExtinguishEffect(World level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (level.rand.nextFloat() - level.rand.nextFloat()) * 0.8F);
        if (level instanceof WorldServer) {
            ((WorldServer) level).spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
        }
    }
}
