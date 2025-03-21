package net.dragonmounts.entity.breath;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
class EntityBreathNode extends Entity {
    public static EntityBreathNode createEntityBreathNodeServer(World world, Vec3d pos, Vec3d direction, BreathPower power) {
        BreathNode breathNode = new BreathNode(power);
        Vec3d actualMotion = breathNode.getRandomisedStartingMotion(direction.normalize(), world.rand);
        // don't randomise the other properties (size, age) on the server.
        return new EntityBreathNode(world, pos.x, pos.y, pos.z, actualMotion, breathNode);
    }

    private final BreathNode breathNode;
    private float intensityAtCollision;

    private EntityBreathNode(
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

    @Override
    public void onUpdate() {
        this.onServerTick();
    }

    /**
     * Get a collection of the collisions that occurred during the last tick update
     *
     * @return returns a collection showing which parts of the entity collided with an object- eg
     * (WEST, [3,2,6]-->[3.5, 2, 6] means the west face of the entity collided; the entity tried to move to
     * x = 3, but got pushed back to x=3.5
     */
    public NodeLineSegment onServerTick() {
        float radius = this.getCurrentRadius();
        Vec3d prevPos = this.getPositionVector();
        handleWaterMovement();
        float size = breathNode.getCurrentAABBcollisionSize();
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;
        ObjectArrayList<Pair<EnumFacing, AxisAlignedBB>> collisions = EntityUtil.moveAndResize(this, motionX, motionY, motionZ, size, size);
        this.intensityAtCollision = getCurrentIntensity();
        if (collided && onGround) {
            motionY -= 0.01F;         // ensure that we hit the ground next time too
        }
        breathNode.updateAge(this);
        if (breathNode.isDead()) {
            setDead();
        }
        return new NodeLineSegment(
                prevPos,
                this.getPositionVector(),
                radius,
                collisions
        );
    }

    public float getCurrentRadius() {
        return breathNode.getCurrentDiameterOfEffect() / 2.0F;
    }

    public float getCurrentIntensity() {
        return breathNode.getCurrentIntensity();
    }

    /**
     * The intensity of the node at the time the last collision occurred
     *
     * @return snapshot of getCurrentIntensity at the last collision.  Meaningless if getRecentCollisions() empty.
     */
    public float getIntensityAtCollision() {
        return intensityAtCollision;
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {}
}
