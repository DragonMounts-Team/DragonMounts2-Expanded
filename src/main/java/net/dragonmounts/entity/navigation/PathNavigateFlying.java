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

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Based on PathNavigateSwimmer but for air blocks.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class PathNavigateFlying extends PathNavigateSwimmer {
    public PathNavigateFlying(EntityLiving entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    @Override
    protected @Nonnull PathFinder getPathFinder() {
        return new PathFinder(new NodeProcessorFlying());
    }

    @Override
    protected boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canEntityStandOnPos(@Nonnull BlockPos pos) {
        return true;
    }
}
