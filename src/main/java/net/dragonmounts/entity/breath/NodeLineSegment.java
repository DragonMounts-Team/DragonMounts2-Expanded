package net.dragonmounts.entity.breath;

import net.dragonmounts.util.MutableBlockPosEx;
import net.dragonmounts.util.Pair;
import net.dragonmounts.util.math.MathX;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

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
    public final Vec3d startPoint;
    public final Vec3d endPoint;
    public final float radius;
    public final AxisAlignedBB box;
    private final Collection<Pair<EnumFacing, AxisAlignedBB>> collisions; // TODO: remove

    public NodeLineSegment(Vec3d start, Vec3d end, float radius, @Nullable Collection<Pair<EnumFacing, AxisAlignedBB>> collisions) {
        this.startPoint = start;
        this.endPoint = end;
        this.radius = radius;
        this.collisions = (collisions == null) ? Collections.emptyList() : collisions;
        double minX = Math.min(start.x, end.x) - radius;
        double maxX = Math.max(start.x, end.x) + radius;
        double minY = Math.min(start.y, end.y) - radius;
        double maxY = Math.max(start.y, end.y) + radius;
        double minZ = Math.min(start.z, end.z) - radius;
        double maxZ = Math.max(start.z, end.z) + radius;
        AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        for (Pair<EnumFacing, AxisAlignedBB> collision : this.collisions) {
            box = box.union(collision.getSecond());
        }
        this.box = box;
    }

    /**
     * getChangeInValue the vector corresponding to the segment (from start point to end point)
     *
     * @return
     */
    public Vec3d getSegmentDirection() {
        return new Vec3d(endPoint.x - startPoint.x, endPoint.y - startPoint.y, endPoint.z - startPoint.z);
    }

    /**
     * return an AABB which contains all the line segments
     *
     * @param segments
     * @return the AABB which contains all the line segments; null if collection empty
     */
    public static AxisAlignedBB getAxisAlignedBoundingBoxForAll(Collection<NodeLineSegment> segments) {
        AxisAlignedBB box = MathX.ZERO_AABB;
        for (NodeLineSegment segment : segments) {
            box = box.union(segment.box);
        }
        return box;
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
     * @param aabb                the aabb to check against
     * @param totalDensity        the density of a complete collision
     * @param numberOfCloudPoints number of cloud points to use (1 - 1000) - clamped if out of range
     * @return a value from 0.0 (no collision) to totalDensity (total collision)
     */
    public float collisionCheckAABB(AxisAlignedBB aabb, float totalDensity, int numberOfCloudPoints) {
        for (Pair<EnumFacing, AxisAlignedBB> collision : collisions) {
            if (collision.getSecond().intersects(aabb)) {
                return totalDensity;
            }
        }
        return Math.max(
                checkAABBStochastic(aabb, totalDensity, numberOfCloudPoints), // stochastic density
                checkAABBCorners(aabb, totalDensity) // corner density
        );
    }

    /**
     * Check all eight corners of the aabb to see how many lie within the nodelinesegment.
     * Most useful for when the aabb is smaller than the nodelinesegment
     *
     * @param aabb         the aabb to check against
     * @param totalDensity the density of a complete collision
     * @return a value from 0.0 (no collision) to totalDensity (total collision)
     */
    public float checkAABBCorners(AxisAlignedBB aabb, float totalDensity) {
        int cornersInside=0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.minX, aabb.minY, aabb.minZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.minX, aabb.minY, aabb.maxZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.minX, aabb.maxY, aabb.minZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.minX, aabb.maxY, aabb.maxZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.maxX, aabb.minY, aabb.minZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.maxX, aabb.minY, aabb.maxZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.maxX, aabb.maxY, aabb.minZ) ? 1 : 0;
        cornersInside+=isPointWithinNodeLineSegment(aabb.maxX, aabb.maxY, aabb.maxZ) ? 1 : 0;
        return cornersInside * totalDensity * 0.125F;
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

        Vec3d deltaAxis=endPoint.subtract(startPoint);
        Vec3d deltaPointToCheck=new Vec3d(x - startPoint.x, y - startPoint.y, z - startPoint.z);
        double deltaAxisLengthSq=deltaAxis.x * deltaAxis.x + deltaAxis.y * deltaAxis.y + deltaAxis.z * deltaAxis.z;
        double dotProduct=deltaAxis.dotProduct(deltaPointToCheck);
        Vec3d closestPoint;
        if (dotProduct <= 0) {
            closestPoint=new Vec3d(0, 0, 0);
        } else if (dotProduct >= deltaAxisLengthSq) {
            closestPoint=deltaAxis;
        } else {
            closestPoint = deltaAxis.scale(
                    dotProduct / deltaAxisLengthSq // projectionFraction
            );
        }
        return closestPoint.squareDistanceTo(deltaPointToCheck) <= radius * radius;
    }

    /**
     * Choose a number of random points from the nodelinesegment and see how many of them lie within the given aabb.
     * Most useful for when the aabb is larger than the nodelinesegment.
     *
     * @param aabb                the aabb to check against
     * @param totalDensity        the density of a complete collision
     * @param numberOfCloudPoints number of cloud points to use (1 - 1000) - clamped if out of range
     * @return a value from 0.0 (no collision) to totalDensity (total collision)
     */
    private float checkAABBStochastic(AxisAlignedBB aabb, float totalDensity, int numberOfCloudPoints) {
        float retval = 0.0F;
        final int MINIMUM_REASONABLE_CLOUD_POINTS=1;
        final int MAXIMUM_REASONABLE_CLOUD_POINTS=1000;
        numberOfCloudPoints=MathHelper.clamp(numberOfCloudPoints, MINIMUM_REASONABLE_CLOUD_POINTS, MAXIMUM_REASONABLE_CLOUD_POINTS);
        final int NUMBER_OF_CLOUD_POINTS=numberOfCloudPoints;
        final float DENSITY_PER_POINT=totalDensity / NUMBER_OF_CLOUD_POINTS;
        final double SUBSEGMENT_WIDTH=1.0 / (NUMBER_OF_CLOUD_POINTS + 1);

        //    Equation of sphere converting from polar to cartesian:
        //    x = r.cos(theta).sin(phi)
        //    y = r.sin(theta).sin(phi)
        //    z = r.cos(phi)
        Random random=new Random();
        for (int i=0; i < NUMBER_OF_CLOUD_POINTS; ++i) {
            double linePos = i * SUBSEGMENT_WIDTH + random.nextDouble() * SUBSEGMENT_WIDTH;
            double x=MathX.lerp(startPoint.x, endPoint.x, linePos);
            double y=MathX.lerp(startPoint.y, endPoint.y, linePos);
            double z=MathX.lerp(startPoint.z, endPoint.z, linePos);
            int theta=random.nextInt(TABLE_POINTS);
            int phi=random.nextInt(TABLE_POINTS);
            double r=random.nextDouble() * radius;
            x+=r * cosTable[theta] * sinTable[phi];
            y+=r * sinTable[theta] * sinTable[phi];
            z+=r * cosTable[phi];
            if (x >= aabb.minX && x <= aabb.maxX && y >= aabb.minY && y <= aabb.maxY && z >= aabb.minZ && z <= aabb.maxZ) {
                retval+=DENSITY_PER_POINT;
            }
        }
        return retval;
    }


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
     * @param hitDensity          the density of points at each world grid location - is updated by the method
     * @param totalDensity        the total density to be added (eg 1.0F)
     * @param numberOfCloudPoints number of cloud points to use (1 - 1000) - clamped if out of range
     */
    public void addBlockCollisionsAndStochasticCloud(Random random, Map<BlockPos, BreathAffectedBlock> hitDensity, float totalDensity, int numberOfCloudPoints) {
        numberOfCloudPoints = MathHelper.clamp(numberOfCloudPoints, 1, 1000);
        final float DENSITY_PER_POINT = totalDensity / numberOfCloudPoints;
        final double SUBSEGMENT_WIDTH = 1.0 / (numberOfCloudPoints + 1);
        Function<BlockPos, BreathAffectedBlock> fallback = ignored -> new BreathAffectedBlock();

        //    Equation of sphere converting from polar to cartesian:
        //    x = r.cos(theta).sin(phi)
        //    y = r.sin(theta).sin(phi)
        //    z = r.cos(phi)
        //TODO: Deprecated
        MutableBlockPosEx pos = new MutableBlockPosEx(0, 0, 0);
        for (int i = 0; i < numberOfCloudPoints; ++i) {
            double linePos = i * SUBSEGMENT_WIDTH + random.nextDouble() * SUBSEGMENT_WIDTH;
            double x=MathX.lerp(startPoint.x, endPoint.x, linePos);
            double y=MathX.lerp(startPoint.y, endPoint.y, linePos);
            double z=MathX.lerp(startPoint.z, endPoint.z, linePos);
            int theta=random.nextInt(TABLE_POINTS);
            int phi=random.nextInt(TABLE_POINTS);
            double r=random.nextDouble() * radius;
            double hitX = r * cosTable[theta] * sinTable[phi] + x;
            double hitY = r * sinTable[theta] * sinTable[phi] + y;
            double hitZ = r * cosTable[phi] + z;
            hitDensity.computeIfAbsent(pos.with(hitX, hitY, hitZ), fallback)
                    .addHitDensity(getIntersectedFace(x, y, z, hitX, hitY, hitZ), DENSITY_PER_POINT);
        }

        final double CONTRACTION = 0.001;
        for (Pair<EnumFacing, AxisAlignedBB> collision : collisions) {
            AxisAlignedBB aabb=collision.getSecond();
            if (aabb.maxX - aabb.minX > 2 * CONTRACTION && aabb.maxY - aabb.minY > 2 * CONTRACTION && aabb.maxZ - aabb.minZ > 2 * CONTRACTION) {
                aabb=aabb.contract(CONTRACTION, CONTRACTION, CONTRACTION);
                for (BlockPos blockpos : BlockPos.getAllInBox(
                        MathHelper.floor(aabb.minX),
                        MathHelper.floor(aabb.minY),
                        MathHelper.floor(aabb.minZ),
                        MathHelper.floor(aabb.maxX),
                        MathHelper.floor(aabb.maxY),
                        MathHelper.floor(aabb.maxZ)
                )) {
                    hitDensity.computeIfAbsent(blockpos, fallback)
                            .addHitDensity(collision.getFirst().getOpposite(), totalDensity);
                }
            }
        }
    }

    /**
     * Given a ray which originated at xyzOrigin and terminated at xyzHit:
     * Find which face of the block at xyzHit was hit by the ray.
     * @return the face which was hit.  If none (was inside block), returns null
     */
    @Nullable
    public static EnumFacing getIntersectedFace(double xOrigin, double yOrigin, double zOrigin, double xHit, double yHit, double zHit) {
        RayTraceResult mop = new AxisAlignedBB(
                Math.floor(xHit),
                Math.floor(yHit),
                Math.floor(zHit),
                Math.ceil(xHit),
                Math.ceil(yHit),
                Math.ceil(zHit)
        ).calculateIntercept(new Vec3d(xOrigin, yOrigin, zOrigin), new Vec3d(xHit, yHit, zHit));
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

}
