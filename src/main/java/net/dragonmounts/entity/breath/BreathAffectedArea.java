package net.dragonmounts.entity.breath;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.dragonmounts.util.MutableBlockPosEx;
import net.dragonmounts.util.math.MathX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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

    private final ObjectArrayList<BreathNodeEntity> entityBreathNodes = new ObjectArrayList<>();
    private final Long2ObjectMap<BreathAffectedBlock> blocksAffectedByBeam = new Long2ObjectOpenHashMap<>();
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
        this.entityBreathNodes.add(BreathNodeEntity.createEntityBreathNodeServer(world, origin, destination.subtract(origin), power));
    }

    /**
     * updates the BreathAffectedArea, called once per tick
     */
    public void updateTick(World world, DragonBreath weapon) {
        if (this.entityBreathNodes.isEmpty()) return;
        updateBlockAndEntityHitDensities(world, weapon, entityBreathNodes, blocksAffectedByBeam, affectedEntities);
        implementEffectsOnBlocksTick(world, weapon, blocksAffectedByBeam);
        implementEffectsOnEntitiesTick(world, weapon, affectedEntities);
        // decay the hit densities of the affected blocks and entities (eg for flame weapon - cools down)
        Predicate<Map.Entry<?, ? extends IBreathEffectHandler>> predicate = entry -> entry.getValue().decayEffectTick();
        this.blocksAffectedByBeam.entrySet().removeIf(predicate);
        this.affectedEntities.entrySet().removeIf(predicate);
    }

    private static void implementEffectsOnBlocksTick(World world, DragonBreath weapon, Long2ObjectMap<BreathAffectedBlock> affectedBlocks) {
        for (Long2ObjectMap.Entry<BreathAffectedBlock> blockInfo : affectedBlocks.long2ObjectEntrySet()) {
            blockInfo.setValue(weapon.affectBlock(world, blockInfo.getLongKey(), blockInfo.getValue()));
        }
    }

    private static void implementEffectsOnEntitiesTick(World world, DragonBreath weapon, Map<EntityLivingBase, BreathAffectedEntity> affectedEntities) {
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
     * @param entityBreathNodes the breathnodes in the breath weapon beam  - parallel to nodeLineSegments, must correspond 1:1
     * @param affectedBlocks    each block touched by the beam has an entry in this map.  The hitDensity (float) is increased
     *                          every time a node touches it.  blocks without an entry haven't been touched.
     * @param affectedEntities  every entity touched by the beam has an entry in this map (entityID).  The hitDensity (float)
     *                          for an entity is increased every time a node touches it.  entities without an entry haven't
     *                          been touched.
     */
    private static void updateBlockAndEntityHitDensities(
            World world,
            DragonBreath weapon,
            List<BreathNodeEntity> entityBreathNodes,
            Long2ObjectMap<BreathAffectedBlock> affectedBlocks,
            Map<EntityLivingBase, BreathAffectedEntity> affectedEntities
    ) {
        // create a list of NodeLineSegments from the motion path of the BreathNodes
        AxisAlignedBB fullBox = MathX.ZERO_AABB;
        for (Iterator<BreathNodeEntity> it = entityBreathNodes.iterator(); it.hasNext(); ) {
            BreathNodeEntity entity = it.next();
            if (entity.isDead) {
                it.remove();
                continue;
            }
            entity.onUpdate();
            NodeLineSegment segment = entity.getSegment();
            segment.addBlockCollisionsAndStochasticCloud(
                    world.rand,
                    affectedBlocks,
                    entity.getIntensityAtCollision(),
                    10
            );
            fullBox = fullBox.union(segment.box);
        }

        Object2ObjectOpenHashMap<Vec3i, ObjectArrayList<EntityLivingBase>> occupiedByEntities = new Object2ObjectOpenHashMap<>();
        for (EntityLivingBase candidate : world.getEntitiesWithinAABB(EntityLivingBase.class, fullBox, weapon::canAffect)) {
            AxisAlignedBB aabb = candidate.getEntityBoundingBox();
            for (int x = (int) aabb.minX, maxX = (int) aabb.maxX; x <= maxX; ++x) {
                for (int y = (int) aabb.minY, maxY = (int) aabb.maxY; y <= maxY; ++y) {
                    for (int z = (int) aabb.minZ, maxZ = (int) aabb.maxZ; z <= maxZ; ++z) {
                        computeIfAbsent(occupiedByEntities, new Vec3i(x, y, z)).add(candidate);
                    }
                }
            }
        }

        MutableBlockPosEx pos = new MutableBlockPosEx(0, 0, 0);
        final int NUMBER_OF_ENTITY_CLOUD_POINTS = 10;
        for (BreathNodeEntity node : entityBreathNodes) {
            NodeLineSegment segment = node.getSegment();
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
                                        node.getCurrentIntensity(),
                                        NUMBER_OF_ENTITY_CLOUD_POINTS
                                );
                                if (hitDensity > 0.0) {
                                    computeIfAbsent(affectedEntities, entity)
                                            .addHitDensity(segment.getSegmentDirection(), hitDensity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /// to avoid using lambda
    static ObjectArrayList<EntityLivingBase> computeIfAbsent(Object2ObjectOpenHashMap<Vec3i, ObjectArrayList<EntityLivingBase>> map, Vec3i pos) {
        ObjectArrayList<EntityLivingBase> instance;
        if ((instance = map.get(pos)) == null) {
            instance = new ObjectArrayList<>();
            map.put(pos, instance);
        }
        return instance;
    }

    /// to avoid using lambda
    static BreathAffectedEntity computeIfAbsent(Map<EntityLivingBase, BreathAffectedEntity> map, EntityLivingBase entity) {
        BreathAffectedEntity instance;
        if ((instance = map.get(entity)) == null) {
            instance = new BreathAffectedEntity();
            map.put(entity, instance);
        }
        return instance;
    }
}