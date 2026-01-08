/*
** 2016 April 27
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package net.dragonmounts.command;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class TameCommand extends EntityCommand {
    public TameCommand() {
        super(1);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getName() {
        return "tame";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.tame.usage";
    }

    @Override
    protected boolean isValidTarget(Entity entity) {
        return entity instanceof ServerDragonEntity;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Entity entity;
        EntityPlayer player;
        List<ServerDragonEntity> dragons;
        switch (args.length) {
            case 0:
                entity = sender.getCommandSenderEntity();
                if (entity instanceof EntityPlayer) {
                    player = (EntityPlayer) entity;
                } else throw new PlayerNotFoundException("commands.generic.player.unspecified");
                dragons = Collections.singletonList(getClosestEntity(sender, ServerDragonEntity.class));
                break;
            case 1:
                entity = sender.getCommandSenderEntity();
                if (entity instanceof EntityPlayer) {
                    player = (EntityPlayer) entity;
                } else throw new PlayerNotFoundException("commands.generic.player.unspecified");
                dragons = getSelectedEntities(server, sender, args[0], ServerDragonEntity.class, "commands.dragonmounts.notFound");
                break;
            case 2:
                dragons = getSelectedEntities(server, sender, args[0], ServerDragonEntity.class, "commands.dragonmounts.notFound");
                player = getPlayer(server, sender, args[1]);
                break;
            default:
                throw new WrongUsageException("commands.dragonmounts.tame.usage");
        }
        for (TameableDragonEntity dragon : dragons) {
            dragon.tame(player);
            notifyCommandListener(sender, this, "commands.dragonmounts.tame.success", dragon.getDisplayName(), player.getDisplayName());
        }
    }
}
