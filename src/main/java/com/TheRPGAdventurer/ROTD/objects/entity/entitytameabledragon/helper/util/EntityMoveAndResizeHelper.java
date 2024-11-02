package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.util;

import com.TheRPGAdventurer.ROTD.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Collection;

/**
 * Created by TGG on 16/08/2015.
 * Utility class to resize an entity
 * 1) resizes the entity around its centre
 * 2) takes into account any nearby objects that the entity might collide with
 */
@Deprecated
public class EntityMoveAndResizeHelper {

    public EntityMoveAndResizeHelper(Entity parentEntity) {
        entity=parentEntity;
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: dx, dy, dz
     * Copied from vanilla; irrelevant parts deleted; modify to accommodate a change in size
     * expands the entity around the centre position:
     * if the expansion causes it to bump against another collision box, temporarily ignore the expansion on
     * that side.  bumping into x also constrains z because width is common to both.
     *
     * @param dx        dx, dy, dz are the desired movement/displacement of the entity
     * @param newHeight the new entity height
     * @param newWidth  the new entity width
     * @return returns a collection showing which parts of the entity collided with an object- eg
     * (WEST, [3,2,6]-->[3.5, 2, 6] means the west face of the entity collided; the entity tried to move to
     * x = 3, but got pushed back to x=3.5
     */
    public Collection<Pair<EnumFacing, AxisAlignedBB>> moveAndResizeEntity(double dx, double dy, double dz, float newWidth, float newHeight) {
        return EntityUtil.moveAndResize(entity, dx, dy, dz, newWidth, newHeight);
    }

    private Entity entity;
}
