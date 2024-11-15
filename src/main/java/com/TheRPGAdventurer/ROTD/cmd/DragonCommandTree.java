/*
 ** 2012 August 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD.cmd;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonCommandTree extends CommandTreeBase {
    public DragonCommandTree() {
        super.addSubcommand(new BreedCommand());
        super.addSubcommand(new StageCommand());
        super.addSubcommand(new CommandDragonTame());
        super.addSubcommand(new CommandDragonGender());
        super.addSubcommand(new CommandDragonUnlock());
        super.addSubcommand(new CommandTreeHelp(this));
    }

    @Override
    public String getName() {
        return "dragon";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragon.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public void addSubcommand(ICommand command) {
        throw new UnsupportedOperationException("Don't add sub-commands to /dragon, create your own command.");
    }
}
