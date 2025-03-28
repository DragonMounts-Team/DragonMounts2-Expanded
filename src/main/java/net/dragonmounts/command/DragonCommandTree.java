/*
 ** 2012 August 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonCommandTree extends CommandTreeBase {
    public DragonCommandTree() {
        super.addSubcommand(new TypeCommand());
        super.addSubcommand(new SizeCommand());
        super.addSubcommand(new StageCommand());
        super.addSubcommand(new TameCommand());
        super.addSubcommand(new UnlockCommand());
        super.addSubcommand(new CommandTreeHelp(this));
    }

    @Override
    public String getName() {
        return "dragonmounts";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public void addSubcommand(ICommand command) {
        throw new UnsupportedOperationException("Don't add sub-commands to /dragonmounts, create your own command.");
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("dragon");
    }
}
