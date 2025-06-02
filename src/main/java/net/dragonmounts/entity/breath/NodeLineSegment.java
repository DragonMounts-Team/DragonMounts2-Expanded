package net.dragonmounts.entity.breath;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.util.MutableBlockPosEx;
import net.dragonmounts.util.math.MathX;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

/**
 * Created by TGG on 31/07/2015.
 * NodeLineSegment is used to represent a spherical node which has moved from one [x,y,z] point to a second [x,y,z] point.
 * Each line segment has a start point and a finish point.  The node has a defined radius.
 * The line segment is then used to detect collisions with other objects
 * <p>
 * Optionally, the segment
 * can be provided with a collection of collisions as well (each collision corresponds to an AABB which is known to
 * overlap with a block or entity, as discovered while moving the node.  the facing shows which face of the node
 * collided with the object.)
 * Typical usage:
 * (1) create node segment with a start point, finish point, and node radius.  Optional collisions from entity moving.
 * (2) collisionCheckAABB(), collisionCheckAABBcorners(), addStochasticCloud() and/or addBlockCollisions() to
 * perform collision checks of the against the node against blocks or entities
 */
public class NodeLineSegment {
    public static final int CLOUD_POINTS = 10;
    public final Vec3d startPoint;
    public final Vec3d direction;
    public final AxisAlignedBB box;
    public final float squaredRadius;
    public final float totalDensity;
    public final double squaredLength;
    private final Collection<AxisAlignedBB> collisions;
    private final Vec3d[] hitPoints;

    /**
     * Creates a cloud of points around the line segment, to simulate the movement of a sphere starting from the
     * beginning of the line segment and moving to the end.  Each point is mapped onto the world grid.
     * Uses stochastic simulation, each point is generated as
     * 1) a point [x1,y1,z1] is chosen along the line segment, evenly distributed according to the number of cloud points,
     * plus a small random jitter
     * 2) a random point [x2,y2,z2] is chosen within the sphere centred on [x1,y1,z1].  This is generated from spherical
     * coordinates radius, phi, theta, uniformly distributed.  This puts more points near the centre of the sphere
     * i.e. the density of points is highest in the centre which is roughly what we want.
     * Each call to addStochasticCloud adds a total of totalDensity to the world grid -
     * eg if totalDensity = 1.0, it adds 1.0 to a single location, or 0.2 to location 1 and 0.8 to location 2, etc
     * Then:
     * For each of the direct collisions for this node (overlaps between the node AABB and the world, as calculated
     * in the entity movement), increment the hit density of the corresponding blocks
     * (The collision may have been caused by an entity not the blocks, however if the block actually has nothing in
     * it then it won't be affected anyway.)
     *
     * @param hitDensity the density of points at each world grid location - is updated by the method
     */
    public NodeLineSegment(
            Random random,
            Vec3d start,
            Vec3d end,
            float radius,
            float totalDensity,
            Long2ObjectMap<BreathAffectedBlock> hitDensity,
            Collection<ImmutablePair<EnumFacing, AxisAlignedBB>> collisions
    ) {
        this.startPoint = start;
        this.direction = end.subtract(start);
        this.squaredLength = this.direction.lengthSquared();
        this.squaredRadius = radius * radius;
        this.totalDensity = totalDensity;

        int numberOfCloudPoints = CLOUD_POINTS;
        Vec3d[] hitPoints = new Vec3d[numberOfCloudPoints];
        final float DENSITY_PER_POINT = totalDensity / numberOfCloudPoints;
        final double SUBSEGMENT_WIDTH = 1.0 / (numberOfCloudPoints + 1);
        MutableBlockPosEx pos = new MutableBlockPosEx(0, 0, 0);
        for (int i = 0; i < numberOfCloudPoints; ++i) {
            double linePos = i * SUBSEGMENT_WIDTH + random.nextDouble() * SUBSEGMENT_WIDTH;
            double x = MathX.lerp(start.x, end.x, linePos);
            double y = MathX.lerp(start.y, end.y, linePos);
            double z = MathX.lerp(start.z, end.z, linePos);
            int theta = random.nextInt(TABLE_POINTS);
            int phi = random.nextInt(TABLE_POINTS);
            double r = random.nextDouble() * radius;
            Vec3d hit = hitPoints[i] = new Vec3d(
                    r * cosTable[theta] * sinTable[phi] + x,
                    r * sinTable[theta] * sinTable[phi] + y,
                    r * cosTable[phi] + z
            );
            computeIfAbsent(hitDensity, pos.with(hit.x, hit.y, hit.z))
                    .addHitDensity(getIntersectedFace(x, y, z, hit), DENSITY_PER_POINT);
        }
        this.hitPoints = hitPoints;

        double minX = Math.min(start.x, end.x) - radius;
        double maxX = Math.max(start.x, end.x) + radius;
        double minY = Math.min(start.y, end.y) - radius;
        double maxY = Math.max(start.y, end.y) + radius;
        double minZ = Math.min(start.z, end.z) - radius;
        double maxZ = Math.max(start.z, end.z) + radius;
        AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        ObjectArrayList<AxisAlignedBB> collided = new ObjectArrayList<>(collisions.size());
        final double CONTRACTION = 0.001;
        for (ImmutablePair<EnumFacing, AxisAlignedBB> collision : collisions) {
            AxisAlignedBB aabb = collision.getRight();
            collided.add(aabb);
            box = box.union(aabb);
            if (aabb.maxX - aabb.minX > 2 * CONTRACTION && aabb.maxY - aabb.minY > 2 * CONTRACTION && aabb.maxZ - aabb.minZ > 2 * CONTRACTION) {
                aabb = aabb.contract(CONTRACTION, CONTRACTION, CONTRACTION);
                EnumFacing side = collision.getLeft().getOpposite();
                for (BlockPos blockpos : BlockPos.getAllInBox(
                        MathHelper.floor(aabb.minX),
                        MathHelper.floor(aabb.minY),
                        MathHelper.floor(aabb.minZ),
                        MathHelper.floor(aabb.maxX),
                        MathHelper.floor(aabb.maxY),
                        MathHelper.floor(aabb.maxZ)
                )) {
                    computeIfAbsent(hitDensity, blockpos).addHitDensity(side, totalDensity);
                }
            }
        }
        this.collisions = collided;
        this.box = box;
    }

    /**
     * Models collision between the node and the given aabb (of an entity)
     * Three checks:
     * a) For each of the direct collisions for this node (overlaps between the node AABB and the world, as calculated
     * in the entity movement) - if any overlap occurs, apply the full density.  (Currently not used)
     * Otherwise:
     * b) stochastically, based on the area of effect on nearby objects, even if no direct AABB overlap.  see below.  This
     * is most useful when the node is small compared to the aabb.
     * and
     * c) check corner of the aabb to see if it lies inside the nodelinesegment.  This is most useful when the node is large
     * compared to the aabb.
     * <p>
     * stochastically check how much the line segment collides with the specified aabb
     * Uses stochastic simulation, each point is generated as
     * 1) a point [x1,y1,z1] is chosen along the line segment, evenly distributed according to the number of cloud points,
     * plus a small random jitter
     * 2) a random point [x2,y2,z2] is chosen within the sphere centred on [x1,y1,z1].  This is generated from spherical
     * coordinates radius, phi, theta, uniformly distributed.  This puts more points near the centre of the sphere
     * i.e. the density of points is highest in the centre which is roughly what we want.
     *
     * @return a value from 0.0 (no collision) to totalDensity (total collision)
     */
    public float collisionCheckAABB(AxisAlignedBB aabb) {
        for (AxisAlignedBB collision : collisions) {
            if (collision.intersects(aabb)) return this.totalDensity;
        }
        float hit = checkAABBHits(aabb); // stochastic density
        float corner = checkAABBCorners(aabb);// corner density
        return Math.max(hit, corner);
    }

    /**
     * Check all eight corners of the aabb to see how many lie within the nodelinesegment.
     * Most useful for when the aabb is smaller than the nodelinesegment
     *
     * @param aabb         the aabb to check against
     * @return a value from 0.0 (no collision) to totalDensity (total collision)
     */
    public float checkAABBCorners(AxisAlignedBB aabb) {
        int cornersInside=0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.minX, aabb.minY, aabb.minZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.minX, aabb.minY, aabb.maxZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.minX, aabb.maxY, aabb.minZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.minX, aabb.maxY, aabb.maxZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.maxX, aabb.minY, aabb.minZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.maxX, aabb.minY, aabb.maxZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.maxX, aabb.maxY, aabb.minZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.maxX, aabb.maxY, aabb.maxZ) ? 1 : 0;
        return cornersInside * this.totalDensity * 0.125F;
    }

    /**
     * check whether the given point lies within the nodeLineSegment (i.e. within the sphere around the start point,
     * within the sphere around the end point, or within the cylinder about the line connecting start and end)
     *
     * @param x [x,y,z] is the world coordinate to check
     * @return true if it lies inside, false otherwise
     */
    public boolean isPointWithinNodeLineSegment(double x, double y, double z) {
        // first, find the closest point on the line segment between start and finish.
        // This is given by the formula
        //  projection_of_u_on_v = v . ( u dot v) / length(v)^2
        // where u = vector from startpoint to test point, and v = vector from startpoint to endpoint

        Vec3d deltaAxis = this.direction;
        Vec3d deltaPointToCheck=new Vec3d(x - startPoint.x, y - startPoint.y, z - startPoint.z);
        double dotProduct = deltaAxis.dotProduct(deltaPointToCheck);
        Vec3d closestPoint;
        if (dotProduct <= 0) {
            closestPoint = Vec3d.ZERO;
        } else if (dotProduct >= this.squaredLength) {
            closestPoint = deltaAxis;
        } else {
            closestPoint = deltaAxis.scale(
                    dotProduct / this.squaredLength // projectionFraction
            );
        }
        return closestPoint.squareDistanceTo(deltaPointToCheck) <= this.squaredRadius;
    }

    /**
     * Choose a number of random points from the nodelinesegment and see how many of them lie within the given aabb.
     * Most useful for when the aabb is larger than the nodelinesegment.
     *
     * @param aabb                the aabb to check against
     * @return a value from 0.0 (no collision) to totalDensity (total collision)
     */
    private float checkAABBHits(AxisAlignedBB aabb) {
        float density = 0.0F;
        final float DENSITY_PER_POINT = totalDensity / CLOUD_POINTS;
        for (Vec3d hit : this.hitPoints) {
            if (aabb.contains(hit)) {
                density += DENSITY_PER_POINT;
            }
        }
        return density;
    }

    /**
     * Given a ray which originated at xyzOrigin and terminated at xyzHit:
     * Find which face of the block at xyzHit was hit by the ray.
     * @return the face which was hit.  If none (was inside block), returns null
     */
    @Nullable
    public static EnumFacing getIntersectedFace(double xOrigin, double yOrigin, double zOrigin, Vec3d hit) {
        RayTraceResult mop = new AxisAlignedBB(
                Math.floor(hit.x),
                Math.floor(hit.y),
                Math.floor(hit.z),
                Math.ceil(hit.x),
                Math.ceil(hit.y),
                Math.ceil(hit.z)
        ).calculateIntercept(new Vec3d(xOrigin, yOrigin, zOrigin), hit);
        return mop == null ? null : mop.sideHit;
    }

    private static final int TABLE_POINTS=256;
    private static final float[] sinTable;
    private static final float[] cosTable;

    static {
        float[] sin = new float[TABLE_POINTS];
        float[] cos = new float[TABLE_POINTS];
        for (int i = 0; i < TABLE_POINTS; ++i) {
            double angle = i * 2.0 * Math.PI / TABLE_POINTS;
            sin[i] = (float) Math.sin(angle);
            cos[i] = (float) Math.cos(angle);
        }
        sinTable = sin;
        cosTable = cos;
    }

    /// to avoid using lambda
    static BreathAffectedBlock computeIfAbsent(Long2ObjectMap<BreathAffectedBlock> map, BlockPos pos) {
        long key = pos.toLong();
        BreathAffectedBlock instance;
        if ((instance = map.get(key)) == null) {
            instance = new BreathAffectedBlock();
            map.put(key, instance);
        }
        return instance;
    }
}
