package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath;

import com.TheRPGAdventurer.ROTD.DragonMountsConfig;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.nodes.BreathNodeFactory;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.nodes.BreathNodeP;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.nodes.EntityBreathNodeP;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponP;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.util.Pair;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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

    private ArrayList<EntityBreathNode> entityBreathNodes = new ArrayList<>();
    private HashMap<Vec3i, BreathAffectedBlock> blocksAffectedByBeam = new HashMap<Vec3i, BreathAffectedBlock>();
    private final Int2ObjectOpenHashMap<BreathAffectedEntity> affectedEntities = new Int2ObjectOpenHashMap<>();
    private ArrayList<EntityBreathNodeP> entityBreathNodesP = new ArrayList<>();
    private BreathWeaponP breathWeaponP;
    private DragonBreathMode dragonBreathMode;


    public BreathAffectedArea() {}

    public BreathAffectedArea(BreathWeaponP i_breathWeapon) {  // dummy to enable compilation
        breathWeaponP = i_breathWeapon;
        throw new UnsupportedOperationException();
    }

    /**
     * Tell BreathAffectedArea that breathing is ongoing.  Call once per tick before updateTick()
     *
     * @param world
     * @param origin      the origin of the beam
     * @param destination the destination of the beam, used to calculate direction
     * @param power
     */
    public void continueBreathing(World world, Vec3d origin, Vec3d destination, BreathNode.Power power) {
        Vec3d direction = destination.subtract(origin).normalize();
        this.entityBreathNodes.add(EntityBreathNode.createEntityBreathNodeServer(
                world,
                origin.x,
                origin.y,
                origin.z,
                direction.x,
                direction.y,
                direction.z,
                power
        ));
    }

    /**
     * Tell BreathAffectedArea that breathing is ongoing.  Call once per tick before updateTick()
     *
     * @param world
     * @param origin      the origin of the beam
     * @param destination the destination of the beam, used to calculate direction
     * @param power
     */
    public void continueBreathing(World world, Vec3d origin, Vec3d destination,
                                  BreathNodeFactory breathNodeFactory, BreathNodeP.Power power, DragonBreathMode breathMode) {
        Vec3d direction = destination.subtract(origin).normalize();

        EntityBreathNodeP newNode = EntityBreathNodeP.createEntityBreathNodeServer(
                world, origin.x, origin.y, origin.z, direction.x, direction.y, direction.z,
                breathNodeFactory, power, breathMode);

        entityBreathNodesP.add(newNode);
        throw new UnsupportedOperationException();
    }


    /**
     * updates the BreathAffectedArea, called once per tick
     */
    public void updateTick(World world, BreathWeapon weapon) {
        ObjectArrayList<NodeLineSegment> segments = new ObjectArrayList<>(this.entityBreathNodes.size());

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

        updateBlockAndEntityHitDensities(world, segments, entityBreathNodes, blocksAffectedByBeam, affectedEntities);
        implementEffectsOnBlocksTick(world, weapon, blocksAffectedByBeam);
        implementEffectsOnEntitiesTick(world, weapon, affectedEntities);
        decayBlockAndEntityHitDensities(blocksAffectedByBeam, affectedEntities);
    }

    /**
     * updates the BreathAffectedArea, called once per tick
     */
    public void updateTick(World world, BreathWeapon weapon, DragonBreathMode mode) {
        if (!mode.equals(dragonBreathMode)) {
            dragonBreathMode = mode;
            if (breathWeaponP.shouldResetOnBreathModeChange(dragonBreathMode)) {
                entityBreathNodes.clear();
                blocksAffectedByBeam.clear();
                affectedEntities.clear();
            }
        }

        ArrayList<NodeLineSegment> segments = new ArrayList<>();

        // create a list of NodeLineSegments from the motion path of the BreathNodes
        Iterator<EntityBreathNodeP> it = entityBreathNodesP.iterator();
        while (it.hasNext()) {
            EntityBreathNodeP entity = it.next();
            if (entity.isDead) {
                it.remove();
            } else {
                float radius = entity.getCurrentRadius();
                Vec3d initialPosition = entity.getPositionVector();
                entity.updateBreathMode(dragonBreathMode);
                entity.onUpdate();
                Collection<Pair<EnumFacing, AxisAlignedBB>> recentCollisions = entity.getRecentCollisions();
                Vec3d finalPosition = entity.getPositionVector();
                segments.add(new NodeLineSegment(initialPosition, finalPosition, radius, recentCollisions));
            }
        }

        updateBlockAndEntityHitDensities(world, segments, entityBreathNodes, blocksAffectedByBeam, affectedEntities);

        implementEffectsOnBlocksTick(world, weapon, blocksAffectedByBeam);
        implementEffectsOnEntitiesTick(world, weapon, affectedEntities);

        decayBlockAndEntityHitDensities(blocksAffectedByBeam, affectedEntities);
    }

    private void implementEffectsOnBlocksTick(World world, BreathWeapon weapon, HashMap<Vec3i, BreathAffectedBlock> affectedBlocks) {
        if (!DragonMountsConfig.doBreathweaponsAffectBlocks()) return;
        for (Map.Entry<Vec3i, BreathAffectedBlock> blockInfo : affectedBlocks.entrySet()) {
            blockInfo.setValue(weapon.affectBlock(world, blockInfo.getKey(), blockInfo.getValue()));
        }
    }

    private void implementEffectsOnEntitiesTick(World world, BreathWeapon weapon, Int2ObjectMap<BreathAffectedEntity> affectedEntities) {
        ObjectIterator<Int2ObjectMap.Entry<BreathAffectedEntity>> iterator = affectedEntities.int2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            Int2ObjectMap.Entry<BreathAffectedEntity> entry = iterator.next();
            BreathAffectedEntity newHitDensity = weapon.affectEntity(world, entry.getIntKey(), entry.getValue());
            if (newHitDensity == null) {
                iterator.remove();
            } else {
                entry.setValue(newHitDensity);
            }
        }
    }

    /**
     * decay the hit densities of the affected blocks and entities (eg for flame weapon - cools down)
     */
    private void decayBlockAndEntityHitDensities(
            HashMap<Vec3i, BreathAffectedBlock> affectedBlocks,
            Int2ObjectMap<BreathAffectedEntity> affectedEntities
    ) {
        Predicate<Map.Entry<?, ? extends IBreathEffectHandler>> predicate = entry -> entry.getValue().decayEffectTick();
        affectedBlocks.entrySet().removeIf(predicate);
        affectedEntities.entrySet().removeIf(predicate);
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
    private void updateBlockAndEntityHitDensities(World world,
                                                  List<NodeLineSegment> nodeLineSegments,
                                                  List<EntityBreathNode> entityBreathNodes,
                                                  HashMap<Vec3i, BreathAffectedBlock> affectedBlocks,
                                                  Int2ObjectMap<BreathAffectedEntity> affectedEntities) {
        checkNotNull(nodeLineSegments);
        checkNotNull(entityBreathNodes);
        checkNotNull(affectedBlocks);
        checkNotNull(affectedEntities);
        checkArgument(nodeLineSegments.size() == entityBreathNodes.size());

        if (entityBreathNodes.isEmpty()) return;
        final int segmentsSize = nodeLineSegments.size();

        final int NUMBER_OF_CLOUD_POINTS = 10;
        for (int i = 0; i < segmentsSize; ++i) {
            nodeLineSegments.get(i).addBlockCollisionsAndStochasticCloud(
                    affectedBlocks,
                    entityBreathNodes.get(i).getIntensityAtCollision(),
                    NUMBER_OF_CLOUD_POINTS
            );
        }

        AxisAlignedBB allAABB = NodeLineSegment.getAxisAlignedBoundingBoxForAll(nodeLineSegments);

        Object2ObjectOpenHashMap<Vec3i, IntArrayList> occupiedByEntities = new Object2ObjectOpenHashMap<>();
        Function<Vec3i, IntArrayList> factory = $ -> new IntArrayList();
        for (EntityLivingBase entityLivingBase : world.getEntitiesWithinAABB(EntityLivingBase.class, allAABB)) {
            AxisAlignedBB aabb = entityLivingBase.getEntityBoundingBox();
            for (int x = (int) aabb.minX, maxX = (int) aabb.maxX; x <= maxX; ++x) {
                for (int y = (int) aabb.minY, maxY = (int) aabb.maxY; y <= maxY; ++y) {
                    for (int z = (int) aabb.minZ, maxZ = (int) aabb.maxZ; z <= maxZ; ++z) {
                        occupiedByEntities.computeIfAbsent(new Vec3i(x, y, z), factory).add(entityLivingBase.getEntityId());
                    }
                }
            }
        }

        final int NUMBER_OF_ENTITY_CLOUD_POINTS = 10;
        for (int i = 0; i < segmentsSize; ++i) {
            NodeLineSegment segment = nodeLineSegments.get(i);
            AxisAlignedBB aabb = segment.getAxisAlignedBoundingBox();
            IntOpenHashSet checkedEntities = new IntOpenHashSet();
            for (int x = (int) aabb.minX, maxX = (int) aabb.maxX; x <= maxX; ++x) {
                for (int y = (int) aabb.minY, maxY = (int) aabb.maxY; y <= maxY; ++y) {
                    for (int z = (int) aabb.minZ, maxZ = (int) aabb.maxZ; z <= maxZ; ++z) {
                        IntArrayList entitiesHere = occupiedByEntities.get(new Vec3i(x, y, z));
                        if (entitiesHere == null) continue;
                        IntIterator iterator = entitiesHere.iterator();
                        while (iterator.hasNext()) {
                            int id = iterator.nextInt();
                            if (checkedEntities.add(id)) {
                                Entity entityToCheck = world.getEntityByID(id);
                                if (entityToCheck != null) {
                                    float hitDensity = segment.collisionCheckAABB(entityToCheck.getEntityBoundingBox(), entityBreathNodes.get(i).getCurrentIntensity(), NUMBER_OF_ENTITY_CLOUD_POINTS);
                                    if (hitDensity > 0.0) {
                                        BreathAffectedEntity currentDensity = affectedEntities.get(id);
                                        if (currentDensity == null) {
                                            currentDensity = new BreathAffectedEntity();
                                            affectedEntities.put(id, currentDensity);
                                        }
                                        currentDensity.addHitDensity(segment.getSegmentDirection(), hitDensity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}