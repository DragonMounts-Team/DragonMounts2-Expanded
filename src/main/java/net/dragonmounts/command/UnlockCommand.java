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
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class UnlockCommand extends DragonHandlerCommand {
    public UnlockCommand() {
        super(1);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getName() {
        return "unlock";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.unlock.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<ServerDragonEntity> dragons;
        switch (args.length) {
            case 0:
                dragons = Collections.singletonList(getClosestDragon(sender));
                break;
            case 1:
                dragons = getSelectedDragons(server, sender, args[0]);
                if (dragons.isEmpty()) throw new EntityNotFoundException("commands.dragonmounts.notFound", args[0]);
                break;
            default:
                throw new WrongUsageException("commands.dragonmounts.unlock.usage");
        }
        for (TameableDragonEntity dragon : dragons) {
            dragon.setToAllowedOtherPlayers(true);
            notifyCommandListener(sender, this, "commands.dragonmounts.unlock.success", dragon.getDisplayName());
        }
    }
}
