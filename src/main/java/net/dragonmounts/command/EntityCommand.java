package net.dragonmounts.command;

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

public abstract class EntityCommand extends CommandBase {
    public static final double SEARCH_WIDTH = 16;
    public static final double SEARCH_HEIGHT = 12;

    public static <T extends Entity> T getClosestEntity(ICommandSender sender, Class<T> clazz) throws CommandException {
        Entity entity = sender.getCommandSenderEntity();
        if (clazz.isInstance(entity)) return clazz.cast(entity);
        if (entity instanceof EntityPlayer) {
            double distance = Double.MAX_VALUE, temp;
            T closest = null;
            for (T candidate : entity.world.getEntitiesWithinAABB(
                    clazz,
                    entity.getEntityBoundingBox().grow(SEARCH_WIDTH, SEARCH_HEIGHT, SEARCH_WIDTH)
            )) {
                temp = entity.getDistanceSq(candidate);
                if (temp < distance) {
                    distance = temp;
                    closest = candidate;
                }
            }
            if (closest != null) return closest;
        }
        throw new EntityNotFoundException("commands.dragonmounts.unspecified", DMUtils.NO_ARGS);
    }

    public static <T extends Entity> List<T> getSelectedEntities(MinecraftServer server, ICommandSender sender, String selector, Class<T> clazz) throws CommandException {
        List<T> entities = EntitySelector.matchEntities(sender, selector, clazz);
        if (entities.isEmpty()) {
            try {
                Entity entity = server.getEntityFromUuid(UUID.fromString(selector));
                if (clazz.isInstance(entity)) return Collections.singletonList(clazz.cast(entity));
            } catch (IllegalArgumentException e) {
                if (selector.split("-").length == 5) {
                    throw new EntityNotFoundException("commands.generic.entity.invalidUuid", selector);
                }
            }
        }
        return entities;
    }

    /**
     * the index of the arg that require an entity
     */
    public final int pos;

    public EntityCommand(int pos) {
        this.pos = pos;
    }

    protected abstract boolean isValidTarget(Entity entity);

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
                        target -> this.isValidTarget(target) && target.canBeCollidedWith() && target.getLowestRidingEntity() != vehicle
                );
                if (hit != null) return getListOfStringsMatchingLastWord(args, hit.getCachedUniqueIdString());
            }
        }
        return Collections.emptyList();
    }
}
