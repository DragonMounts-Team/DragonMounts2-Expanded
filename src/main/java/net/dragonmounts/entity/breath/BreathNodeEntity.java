package net.dragonmounts.entity.breath;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.ICollisionObserver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Created by TGG on 31/07/2015.
 * Server side; tracks the position, motion, and collision detection of a breath node in a breath weapon stream,
 * Used with an associated BreathNode to track age, size and initial speed
 * <p>
 * Usage:
 * 1) construct using createEntityBreathNodeServer
 * 2) call onUpdate() every tick to move and collide
 * 3) various getters for intensity, radius, and recent collisions.
 */
class BreathNodeEntity extends Entity implements ICollisionObserver {
    public static BreathNodeEntity createEntityBreathNodeServer(World world, Vec3d pos, Vec3d direction, BreathPower power) {
        BreathNode breathNode = new BreathNode(power);
        Vec3d actualMotion = breathNode.getRandomisedStartingMotion(direction, world.rand);
        // don't randomise the other properties (size, age) on the server.
        return new BreathNodeEntity(world, pos.x, pos.y, pos.z, actualMotion, breathNode);
    }

    private final BreathNode breathNode;
    public final ReferenceOpenHashSet<EntityLivingBase> checked = new ReferenceOpenHashSet<>();
    private final ObjectArrayList<ImmutablePair<EnumFacing, AxisAlignedBB>> collisions = new ObjectArrayList<>();
    private NodeLineSegment segment;

    private BreathNodeEntity(
            World world,
            double x,
            double y,
            double z,
            Vec3d motion,
            BreathNode node
    ) {
        super(world);
        breathNode = node;

        final float ARBITRARY_START_SIZE=0.2F;
        this.setSize(ARBITRARY_START_SIZE, ARBITRARY_START_SIZE);
        this.setPosition(x, y, z);
        lastTickPosX=x;
        lastTickPosY=y;
        lastTickPosZ=z;

        motionX=motion.x;
        motionY=motion.y;
        motionZ=motion.z;
    }

    /**
     * Get a collection of the collisions that occurred during the last tick update
     *
     * @return returns a collection showing which parts of the entity collided with an object- eg
     * (WEST, [3,2,6]-->[3.5, 2, 6] means the west face of the entity collided; the entity tried to move to
     * x = 3, but got pushed back to x=3.5
     */
    public NodeLineSegment update(Long2ObjectMap<BreathAffectedBlock> hitDensity) {
        Vec3d prevPos = this.getPositionVector();
        handleWaterMovement();
        float size = breathNode.getCurrentAABBcollisionSize();
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;
        EntityUtil.resizeAndMove(this, motionX, motionY, motionZ, size, size, this);
        if (collided && onGround) {
            motionY -= 0.01F;         // ensure that we hit the ground next time too
        }
        breathNode.updateAge(this);
        if (breathNode.isDead()) {
            setDead();
        }
        return this.segment = new NodeLineSegment(
                this.rand,
                prevPos,
                this.getPositionVector(),
                this.getCurrentRadius(),
                this.breathNode.getCurrentIntensity(),
                hitDensity,
                this.collisions
        );
    }

    /**
     * Get a collection of the collisions that occurred during the last tick update
     *
     * @return returns a collection showing which parts of the entity collided with an object- eg
     * (WEST, [3,2,6]-->[3.5, 2, 6] means the west face of the entity collided; the entity tried to move to
     * x = 3, but got pushed back to x=3.5
     */
    public NodeLineSegment getSegment() {
        return this.segment;
    }

    public float getCurrentRadius() {
        return breathNode.getCurrentDiameterOfEffect() * 0.5F;
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {}

    @Override
    public void handleMovement(double desiredX, double desiredY, double desiredZ, double actualX, double actualY, double actualZ) {
        ObjectArrayList<ImmutablePair<EnumFacing, AxisAlignedBB>> collisions = this.collisions;
        AxisAlignedBB box = this.getEntityBoundingBox();
        this.collidedHorizontally = false;

        // if we collided in any direction, stop the entity's motion in that direction, and mark the truncated zone
        //   as a collision zone - i.e if we wanted to move to dx += 0.5, but actually could only move +0.2, then the
        //   collision zone is the region from +0.2 to +0.5
        if (desiredX != actualX) {
            this.motionX = 0.0;
            if (desiredX < 0) {
                collisions.add(new ImmutablePair<>(EnumFacing.WEST, new AxisAlignedBB(box.minX + (desiredX - actualX), box.minY, box.minZ, box.minX, box.maxY, box.maxZ)));
            } else {
                collisions.add(new ImmutablePair<>(EnumFacing.EAST, new AxisAlignedBB(box.maxX, box.minY, box.minZ, box.maxX + (desiredX - actualX), box.maxY, box.maxZ)));
            }
            this.collidedHorizontally = true;
        }

        if (desiredY == actualY) {
            this.collidedVertically = false;
        } else {
            this.motionY = 0.0;
            if (desiredY < 0) {
                this.onGround = true;
                collisions.add(new ImmutablePair<>(EnumFacing.DOWN, new AxisAlignedBB(box.minX, box.minY + (desiredY - actualY), box.minZ, box.maxX, box.minY, box.maxZ)));
            } else {
                this.onGround = false;
                collisions.add(new ImmutablePair<>(EnumFacing.UP, new AxisAlignedBB(box.minX, box.maxY, box.minZ, box.maxX, box.maxY + (desiredY - actualY), box.maxZ)));
            }
            this.collidedVertically = true;
        }

        if (desiredZ != actualZ) {
            this.motionZ = 0.0;
            if (desiredZ < 0) {
                collisions.add(new ImmutablePair<>(EnumFacing.NORTH, new AxisAlignedBB(box.minX, box.minY, box.minZ + (desiredZ - actualZ), box.maxX, box.maxY, box.minZ)));
            } else {
                collisions.add(new ImmutablePair<>(EnumFacing.SOUTH, new AxisAlignedBB(box.minX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ + (desiredZ - actualZ))));
            }
            this.collidedHorizontally = true;
        }
        this.collided = this.collidedHorizontally || this.collidedVertically;
    }
}
