/*
 ** 2016 March 13
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.navigation;

import mcp.MethodsReturnNonnullByDefault;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.SwimNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Based on SwimNodeProcessor but for air blocks.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NodeProcessorFlying extends SwimNodeProcessor {
    @Override
    public int findPathOptions(PathPoint[] options, PathPoint start, PathPoint dest, float maxDistance) {
        int len = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            PathPoint node = this.getAirNode(
                    start.x + facing.getXOffset(),
                    start.y + facing.getYOffset(),
                    start.z + facing.getZOffset()
            );
            if (node != null && !node.visited && node.distanceTo(dest) < maxDistance) {
                options[len++] = node;
            }
        }
        return len;
    }

    private @Nullable PathPoint getAirNode(int x, int y, int z) {
        return this.getType(x, y, z) == PathNodeType.WALKABLE ? this.openPoint(x, y, z) : null;
    }

    private PathNodeType getType(int x, int y, int z) {
        MutableBlockPosEx pos = new MutableBlockPosEx(x, y, z);
        for (int i = 0; i < this.entitySizeX; ++i) {
            for (int j = 0; j < this.entitySizeY; ++j) {
                for (int k = 0; k < this.entitySizeZ; ++k) {
                    Material material = this.blockaccess.getBlockState(pos.with(x + i, y + j, z + k)).getMaterial();
                    if (material.isSolid() || material.isLiquid()) return PathNodeType.BLOCKED;
                }
            }
        }
        return PathNodeType.WALKABLE;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess level, int x, int y, int z, EntityLiving entity, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        return PathNodeType.WALKABLE;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess level, int x, int y, int z) {
        return PathNodeType.WALKABLE;
    }
}