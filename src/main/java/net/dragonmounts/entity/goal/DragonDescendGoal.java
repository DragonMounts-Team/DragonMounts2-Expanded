/*
 ** 2013 July 28
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.goal;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Dragon AI for instant landing, if left unmounted in air.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonDescendGoal extends EntityAIBase {
    private final double speed;
    public final ServerDragonEntity dragon;
    private MutableBlockPosEx landingPos;
    private boolean isHoverDisabled;

    public DragonDescendGoal(ServerDragonEntity dragon, double speed) {
        this.dragon = dragon;
        this.speed = speed;
        this.setMutexBits(0b01);
    }

    private boolean findLandingBlock() {
        ServerDragonEntity dragon = this.dragon;
        if (this.landingPos != null && !dragon.getNavigator().noPath()) return true;
        World level = this.dragon.world;
        // get current entity position and add some variance
        Random random = dragon.getRNG();
        Vec3d view = dragon.getLookVec();
        int posX = MathHelper.floor(dragon.posX + view.x * 8);
        int posZ = MathHelper.floor(dragon.posZ + view.z * 8);
        int posY = level.getHeight(posX, posZ);
        MutableBlockPosEx pos = new MutableBlockPosEx(posX, posY, posZ);
        // get ground block
        for (int dY = 0; dY < 4; ++dY) {
            for (int dX = -3; dX <= 3; ++dX) {
                pos.withX(posX + dX);
                for (int dZ = -3; dZ <= 3; ++dZ) {
                    pos.withX(posZ + dZ);
                    Material material = level.getBlockState(pos).getMaterial();
                    if (material.isSolid() || material.isLiquid()) {
                        this.landingPos = pos.climb();
                        return true;
                    }
                }
            }
            pos.climb();
        }
        return false;
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity dragon = this.dragon;
        return !dragon.isInWater() &&
                !dragon.isInLava() &&
                dragon.isFlying() &&
                dragon.getAttackTarget() == null &&
                this.findLandingBlock();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return dragon.isFlying() && !dragon.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        ServerDragonEntity dragon = this.dragon;
        this.isHoverDisabled = this.dragon.isUnHovered();
        this.dragon.setUnHovered(true);
        // try to fly to ground block position
        if (!tryMoveToBlockPos(landingPos)) {
            // probably too high, so simply descend vertically
            Vec3d view = dragon.getLookVec();
            tryMoveToBlockPos(new BlockPos(
                    view.x * 2 + dragon.posX,
                    dragon.posY - 4,
                    view.z * 2 + dragon.posZ
            ));
        }
    }

    public void resetTask() {
        this.landingPos = null;
        this.dragon.setUnHovered(this.isHoverDisabled);
    }

    protected boolean tryMoveToBlockPos(BlockPos pos) {
        ServerDragonEntity dragon = this.dragon;
        PathNavigate navigator = dragon.getNavigator();
        return navigator.setPath(navigator.getPathToPos(pos), this.speed);
    }
}
