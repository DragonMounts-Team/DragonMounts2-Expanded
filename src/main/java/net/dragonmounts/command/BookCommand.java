package net.dragonmounts.command;

import net.dragonmounts.compat.PatchouliCompat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class BookCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "book";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.book.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Entity entity = sender.getCommandSenderEntity();
        if (entity instanceof EntityPlayer) {
            PatchouliCompat.grantGuideBook((EntityPlayer) entity);
        }
    }
}
