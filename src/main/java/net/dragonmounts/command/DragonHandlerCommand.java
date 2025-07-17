package net.dragonmounts.command;

import net.dragonmounts.entity.TameableDragonEntity;
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

    public static TameableDragonEntity getClosestDragon(ICommandSender sender) throws CommandException {
        Entity entity = sender.getCommandSenderEntity();
        if (entity instanceof TameableDragonEntity) return (TameableDragonEntity) entity;
        if (entity instanceof EntityPlayer) {
            List<TameableDragonEntity> dragons = entity.world.getEntitiesWithinAABB(
                    TameableDragonEntity.class,
                    entity.getEntityBoundingBox().grow(SEARCH_WIDTH, SEARCH_HEIGHT, SEARCH_WIDTH)
            );
            double distance = Double.MAX_VALUE, temp;
            TameableDragonEntity closest = null;
            for (TameableDragonEntity dragon : dragons) {
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

    public static List<TameableDragonEntity> getSelectedDragons(MinecraftServer server, ICommandSender sender, String selector) throws CommandException {
        if (selector.isEmpty()) throw new EntityNotFoundException("commands.dragonmounts.unspecified", DMUtils.NO_ARGS);
        List<TameableDragonEntity> dragons = EntitySelector.matchEntities(sender, selector, TameableDragonEntity.class);
        if (dragons.isEmpty()) {
            try {
                Entity entity = server.getEntityFromUuid(UUID.fromString(selector));
                if (entity instanceof TameableDragonEntity) {
                    return Collections.singletonList((TameableDragonEntity) entity);
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
                        target -> target instanceof TameableDragonEntity && target.canBeCollidedWith() && target.getLowestRidingEntity() != vehicle
                );
                if (hit != null) {
                    return getListOfStringsMatchingLastWord(args, hit.getCachedUniqueIdString());
                }
            }
        }
        return Collections.emptyList();
    }
}
