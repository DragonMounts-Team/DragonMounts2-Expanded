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
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class CommandDragonTame extends DragonHandlerCommand {
    public CommandDragonTame() {
        super(1);
    }

    @Override
    public String getName() {
        return "tame";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragon.tame.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Entity entity;
        EntityPlayer player;
        List<EntityTameableDragon> dragons;
        switch (args.length) {
            case 0:
                entity = sender.getCommandSenderEntity();
                if (entity instanceof EntityPlayer) {
                    player = (EntityPlayer) entity;
                } else throw new PlayerNotFoundException("commands.generic.player.unspecified");
                dragons = Collections.singletonList(getClosestDragon(sender));
                break;
            case 1:
                entity = sender.getCommandSenderEntity();
                if (entity instanceof EntityPlayer) {
                    player = (EntityPlayer) entity;
                } else throw new PlayerNotFoundException("commands.generic.player.unspecified");
                dragons = getSelectedDragons(server, sender, args[0]);
                if (dragons.isEmpty()) throw new EntityNotFoundException("commands.dragon.notFound", args[0]);
                break;
            case 2:
                dragons = getSelectedDragons(server, sender, args[0]);
                if (dragons.isEmpty()) throw new EntityNotFoundException("commands.dragon.notFound", args[0]);
                player = EntitySelector.matchOnePlayer(sender, args[1]);
                if (player == null) throw new PlayerNotFoundException("argument.player.toomany");
                break;
            default:
                throw new WrongUsageException("commands.dragon.tame.usage");
        }
        for (EntityTameableDragon dragon : dragons) {
            dragon.tamedFor(player, true);
            sender.sendMessage(new TextComponentTranslation("commands.dragon.tame.success", dragon.getDisplayName(), player.getDisplayName()));
        }
    }
}
