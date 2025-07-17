package net.dragonmounts.command;

import net.dragonmounts.entity.ServerDragonEntity;
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

    public static ServerDragonEntity getClosestDragon(ICommandSender sender) throws CommandException {
        Entity entity = sender.getCommandSenderEntity();
        if (entity instanceof ServerDragonEntity) return (ServerDragonEntity) entity;
        if (entity instanceof EntityPlayer) {
            List<ServerDragonEntity> dragons = entity.world.getEntitiesWithinAABB(
                    ServerDragonEntity.class,
                    entity.getEntityBoundingBox().grow(SEARCH_WIDTH, SEARCH_HEIGHT, SEARCH_WIDTH)
            );
            double distance = Double.MAX_VALUE, temp;
            ServerDragonEntity closest = null;
            for (ServerDragonEntity dragon : dragons) {
                temp = entity.getDistanceSq(dragon);
                if (temp < distance) {
                    distance = temp;
                    closest = dragon;
                }
            }
            if (closest != null) return closest;

        }
        throw new EntityNotFoundException("commands.dragonmounts.unspecified", DMUtils.NO_ARGS);
    }

    public static List<ServerDragonEntity> getSelectedDragons(MinecraftServer server, ICommandSender sender, String selector) throws CommandException {
        if (selector.isEmpty()) throw new EntityNotFoundException("commands.dragonmounts.unspecified", DMUtils.NO_ARGS);
        List<ServerDragonEntity> dragons = EntitySelector.matchEntities(sender, selector, ServerDragonEntity.class);
        if (dragons.isEmpty()) {
            try {
                Entity entity = server.getEntityFromUuid(UUID.fromString(selector));
                if (entity instanceof ServerDragonEntity) {
                    return Collections.singletonList((ServerDragonEntity) entity);
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
                        target -> target instanceof ServerDragonEntity && target.canBeCollidedWith() && target.getLowestRidingEntity() != vehicle
                );
                if (hit != null) {
                    return getListOfStringsMatchingLastWord(args, hit.getCachedUniqueIdString());
                }
            }
        }
        return Collections.emptyList();
    }
}
