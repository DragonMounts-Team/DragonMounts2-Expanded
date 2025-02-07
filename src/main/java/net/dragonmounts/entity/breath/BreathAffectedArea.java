package net.dragonmounts.entity.breath;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.entity.breath.weapons.BreathWeapon;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by TGG on 30/07/2015.
 * BreathAffectedArea base class
 * Represents the area of the world (blocks, entities) affected by the breathweapon.
 * Usage:
 * (1) Construct from a BreathWeapon
 * (2) continueBreathing() once per tick whenever the dragon is breathing
 * (3) updateTick() every tick to update the area of effect, and implement breathweapon effects on the blocks &
 *     entities within the area of effect
 */
public class BreathAffectedArea {

    private final ObjectArrayList<EntityBreathNode> entityBreathNodes = new ObjectArrayList<>();
    private final Object2ObjectOpenHashMap<BlockPos, BreathAffectedBlock> blocksAffectedByBeam = new Object2ObjectOpenHashMap<>();
    private final Reference2ObjectOpenHashMap<EntityLivingBase, BreathAffectedEntity> affectedEntities = new Reference2ObjectOpenHashMap<>();

    public BreathAffectedArea() {}

    /**
     * Tell BreathAffectedArea that breathing is ongoing.  Call once per tick before updateTick()
     *
     * @param world
     * @param origin      the origin of the beam
     * @param destination the destination of the beam, used to calculate direction
     * @param power
     */
    public void continueBreathing(World world, Vec3d origin, Vec3d destination, BreathPower power) {
        this.entityBreathNodes.add(EntityBreathNode.createEntityBreathNodeServer(world, origin, destination.subtract(origin), power));
    }

    /**
     * updates the BreathAffectedArea, called once per tick
     */
    public void updateTick(World world, BreathWeapon weapon) {
        int size = this.entityBreathNodes.size();
        if (size == 0) return;
        ObjectArrayList<NodeLineSegment> segments = new ObjectArrayList<>(size);

        // create a list of NodeLineSegments from the motion path of the BreathNodes
        Iterator<EntityBreathNode> it = this.entityBreathNodes.iterator();
        while (it.hasNext()) {
            EntityBreathNode entity = it.next();
            if (entity.isDead) {
                it.remove();
            } else {
                segments.add(entity.onServerTick());
            }
        }

        updateBlockAndEntityHitDensities(world, weapon, segments, entityBreathNodes, blocksAffectedByBeam, affectedEntities);
        implementEffectsOnBlocksTick(world, weapon, blocksAffectedByBeam);
        implementEffectsOnEntitiesTick(world, weapon, affectedEntities);
        // decay the hit densities of the affected blocks and entities (eg for flame weapon - cools down)
        Predicate<Map.Entry<?, ? extends IBreathEffectHandler>> predicate = entry -> entry.getValue().decayEffectTick();
        this.blocksAffectedByBeam.entrySet().removeIf(predicate);
        this.affectedEntities.entrySet().removeIf(predicate);
    }

    private static void implementEffectsOnBlocksTick(World world, BreathWeapon weapon, Map<BlockPos, BreathAffectedBlock> affectedBlocks) {
        if (!DragonMountsConfig.doBreathweaponsAffectBlocks()) return;
        for (Map.Entry<BlockPos, BreathAffectedBlock> blockInfo : affectedBlocks.entrySet()) {
            blockInfo.setValue(weapon.affectBlock(world, blockInfo.getKey(), blockInfo.getValue()));
        }
    }

    private static void implementEffectsOnEntitiesTick(World world, BreathWeapon weapon, Map<EntityLivingBase, BreathAffectedEntity> affectedEntities) {
        Iterator<Map.Entry<EntityLivingBase, BreathAffectedEntity>> iterator = affectedEntities.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<EntityLivingBase, BreathAffectedEntity> entry = iterator.next();
            EntityLivingBase target = entry.getKey();
            if (target.isDead) {
                iterator.remove();
                continue;
            }
            weapon.affectEntity(world, target, entry.getValue());
        }
    }

    /**
     * Models the collision of the breath nodes on the world blocks and entities:
     * Each breathnode which contacts a world block will increase the corresponding 'hit density' by an amount proportional
     * to the intensity of the node and the degree of overlap between the node and the block.
     * Likewise for the entities contacted by the breathnode
     *
     * @param world
     * @param nodeLineSegments  the nodeLineSegments in the breath weapon beam
     * @param entityBreathNodes the breathnodes in the breath weapon beam  - parallel to nodeLineSegments, must correspond 1:1
     * @param affectedBlocks    each block touched by the beam has an entry in this map.  The hitDensity (float) is increased
     *                          every time a node touches it.  blocks without an entry haven't been touched.
     * @param affectedEntities  every entity touched by the beam has an entry in this map (entityID).  The hitDensity (float)
     *                          for an entity is increased every time a node touches it.  entities without an entry haven't
     *                          been touched.
     */
    private static void updateBlockAndEntityHitDensities(
            World world,
            BreathWeapon weapon,
            List<NodeLineSegment> nodeLineSegments,
            List<EntityBreathNode> entityBreathNodes,
            Map<BlockPos, BreathAffectedBlock> affectedBlocks,
            Map<EntityLivingBase, BreathAffectedEntity> affectedEntities
    ) {
        checkArgument(nodeLineSegments.size() == entityBreathNodes.size());

        if (entityBreathNodes.isEmpty()) return;
        final int segmentsSize = nodeLineSegments.size();

        for (int i = 0; i < segmentsSize; ++i) {
            nodeLineSegments.get(i).addBlockCollisionsAndStochasticCloud(
                    world.rand,
                    affectedBlocks,
                    entityBreathNodes.get(i).getIntensityAtCollision(),
                    10
            );
        }

        Object2ObjectOpenHashMap<Vec3i, ObjectArrayList<EntityLivingBase>> occupiedByEntities = new Object2ObjectOpenHashMap<>();
        Function<Vec3i, ObjectArrayList<EntityLivingBase>> list = ignored -> new ObjectArrayList<>();
        for (EntityLivingBase candidate : world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                NodeLineSegment.getAxisAlignedBoundingBoxForAll(nodeLineSegments),
                weapon::canAffect
        )) {
            AxisAlignedBB aabb = candidate.getEntityBoundingBox();
            for (int x = (int) aabb.minX, maxX = (int) aabb.maxX; x <= maxX; ++x) {
                for (int y = (int) aabb.minY, maxY = (int) aabb.maxY; y <= maxY; ++y) {
                    for (int z = (int) aabb.minZ, maxZ = (int) aabb.maxZ; z <= maxZ; ++z) {
                        occupiedByEntities.computeIfAbsent(new BlockPos(x, y, z), list).add(candidate);
                    }
                }
            }
        }

        MutableBlockPosEx pos = new MutableBlockPosEx(0, 0, 0);
        Function<EntityLivingBase, BreathAffectedEntity> fallback = ignored -> new BreathAffectedEntity();
        final int NUMBER_OF_ENTITY_CLOUD_POINTS = 10;
        for (int i = 0; i < segmentsSize; ++i) {
            NodeLineSegment segment = nodeLineSegments.get(i);
            AxisAlignedBB aabb = segment.box;
            ReferenceOpenHashSet<EntityLivingBase> checkedEntities = new ReferenceOpenHashSet<>();
            for (int x = (int) aabb.minX, maxX = (int) aabb.maxX; x <= maxX; ++x) {
                for (int y = (int) aabb.minY, maxY = (int) aabb.maxY; y <= maxY; ++y) {
                    for (int z = (int) aabb.minZ, maxZ = (int) aabb.maxZ; z <= maxZ; ++z) {
                        ObjectArrayList<EntityLivingBase> entitiesHere = occupiedByEntities.get(pos.with(x, y, z));
                        if (entitiesHere == null) continue;
                        for (EntityLivingBase entity : entitiesHere) {
                            if (checkedEntities.add(entity)) {
                                float hitDensity = segment.collisionCheckAABB(
                                        entity.getEntityBoundingBox(),
                                        entityBreathNodes.get(i).getCurrentIntensity(),
                                        NUMBER_OF_ENTITY_CLOUD_POINTS
                                );
                                if (hitDensity > 0.0) {
                                    affectedEntities.computeIfAbsent(entity, fallback)
                                            .addHitDensity(segment.getSegmentDirection(), hitDensity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}