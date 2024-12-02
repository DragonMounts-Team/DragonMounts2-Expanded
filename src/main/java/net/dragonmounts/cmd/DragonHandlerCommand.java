package net.dragonmounts.cmd;

import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.util.DMUtils;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static net.dragonmounts.util.RayTraceServer.rayTraceEntity;

public abstract class DragonHandlerCommand extends CommandBase {
    public static final double SEARCH_WIDTH = 16;
    public static final double SEARCH_HEIGHT = 12;

    public static EntityTameableDragon getClosestDragon(ICommandSender sender) throws CommandException {
        Entity entity = sender.getCommandSenderEntity();
        if (entity instanceof EntityTameableDragon) return (EntityTameableDragon) entity;
        if (entity instanceof EntityPlayer) {
            List<EntityTameableDragon> dragons = entity.world.getEntitiesWithinAABB(
                    EntityTameableDragon.class,
                    entity.getEntityBoundingBox().grow(SEARCH_WIDTH, SEARCH_HEIGHT, SEARCH_WIDTH)
            );
            double distance = Double.MAX_VALUE, temp;
            EntityTameableDragon closest = null;
            for (EntityTameableDragon dragon : dragons) {
                temp = entity.getDistanceSq(dragon);
                if (temp < distance) {
                    distance = temp;
                    closest = dragon;
                }
            }
            if (closest != null) return closest;

        }
        throw new EntityNotFoundException("commands.dragon.unspecified", DMUtils.NO_ARGS);
    }

    public static List<EntityTameableDragon> getSelectedDragons(MinecraftServer server, ICommandSender sender, String selector) throws CommandException {
        if (selector.isEmpty()) throw new EntityNotFoundException("commands.dragon.unspecified", DMUtils.NO_ARGS);
        List<EntityTameableDragon> dragons = EntitySelector.matchEntities(sender, selector, EntityTameableDragon.class);
        if (dragons.isEmpty()) {
            try {
                Entity entity = server.getEntityFromUuid(UUID.fromString(selector));
                if (entity instanceof EntityTameableDragon) {
                    return Collections.singletonList((EntityTameableDragon) entity);
                }
            } catch (IllegalArgumentException e) {
                if (selector.split("-").length == 5) {
                    throw new EntityNotFoundException("commands.generic.entity.invalidUuid", selector);
                }
            }
        }
        return dragons;
    }

    /**
     * the index of the arg that require a dragon
     */
    public final int pos;

    public DragonHandlerCommand(int pos) {
        this.pos = pos;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == this.pos) {
            Entity entity = sender.getCommandSenderEntity();
            if (entity instanceof EntityPlayer) {
                Entity vehicle = entity.getLowestRidingEntity();
                Entity hit = rayTraceEntity(
                        entity.world,
                        entity,
                        ((EntityPlayer) entity).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue(),
                        target -> target instanceof EntityTameableDragon && target.canBeCollidedWith() && target.getLowestRidingEntity() != vehicle
                );
                if (hit != null) {
                    return getListOfStringsMatchingLastWord(args, hit.getCachedUniqueIdString());
                }
            }
        }
        return Collections.emptyList();
    }
}
