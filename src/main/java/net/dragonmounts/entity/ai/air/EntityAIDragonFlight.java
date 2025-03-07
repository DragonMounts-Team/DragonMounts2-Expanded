/*
 ** 2013 July 28
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.ai.air;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Dragon AI for instant landing, if left unmounted in air.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityAIDragonFlight extends EntityAIBase {
    private final double speed;
    public final ServerDragonEntity dragon;
    private MutableBlockPosEx landingPos;

    public EntityAIDragonFlight(ServerDragonEntity dragon, double speed) {
        this.dragon = dragon;
        this.speed = speed;
        this.setMutexBits(0b01);
    }

    private boolean findLandingBlock() {
        ServerDragonEntity dragon = this.dragon;
        World level = this.dragon.world;
        // get current entity position and add some variance
        Random random = dragon.getRNG();
        int posX = MathHelper.floor(dragon.posX) - 16 + random.nextInt(16) * 2;
        int posY = MathHelper.floor(dragon.posY + 0.5D);
        int posZ = MathHelper.floor(dragon.posZ) - 16 + random.nextInt(16) * 2;
        MutableBlockPosEx pos = this.landingPos = new MutableBlockPosEx(posX, posY, posZ);
        // get ground block
        for (int dY = 0; dY < 8; ++dY) {
            pos.descent();
            for (int dX = -3; dX <= 3; ++dX) {
                pos.withX(posX + dX);
                for (int dZ = -3; dZ <= 3; ++dZ) {
                    pos.withX(posZ + dZ);
                    Material material = level.getBlockState(pos).getMaterial();
                    if (material.isSolid() || material.isLiquid()) {
                        pos.climb();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldExecute() {
        ServerDragonEntity dragon = this.dragon;
        return !dragon.isInWater() &&
                !dragon.isInLava() &&
                dragon.isFlying() &&
                dragon.getControllingPlayer() == null &&
                dragon.getAttackTarget() == null &&
                this.findLandingBlock();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return dragon.isFlying() && dragon.getControllingPlayer() == null && !dragon.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        // try to fly to ground block position
        if (!tryMoveToBlockPos(landingPos)) {
            // probably too high, so simply descend vertically
            tryMoveToBlockPos(dragon.getPosition().down(4));
        }
    }

    protected boolean tryMoveToBlockPos(Vec3i pos) {
        return dragon.getNavigator().tryMoveToXYZ(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, speed);
    }
}
