/*
** 2016 April 27
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD.cmd;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class CommandDragonUnlock extends DragonHandlerCommand {
    public CommandDragonUnlock() {
        super(1);
    }

    @Override
    public String getName() {
        return "unlock";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragon.unlock.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<EntityTameableDragon> dragons;
        switch (args.length) {
            case 0:
                dragons = Collections.singletonList(getClosestDragon(sender));
                break;
            case 1:
                dragons = getSelectedDragons(server, sender, args[0]);
                if (dragons.isEmpty()) throw new EntityNotFoundException("commands.dragon.notFound", args[0]);
                break;
            default:
                throw new WrongUsageException("commands.dragon.unlock.usage");
        }
        for (EntityTameableDragon dragon : dragons) {
            dragon.setToAllowedOtherPlayers(true);
            sender.sendMessage(new TextComponentTranslation("commands.dragon.unlock.success", dragon.getDisplayName()));
        }
    }
}
