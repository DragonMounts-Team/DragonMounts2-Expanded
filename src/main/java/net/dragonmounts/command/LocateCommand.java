package top.dragonmounts.cmd;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import static top.dragonmounts.world.DMStructures.ENCHANT_DRAGON_NEST;

public class LocateCommand extends CommandBase {
    @Override
    public String getName() {
        return "locate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.locate.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        BlockPos pos = ENCHANT_DRAGON_NEST.getNearestStructurePos(sender.getEntityWorld(), sender.getPosition(), false);
        if (pos == null) {
            sender.sendMessage(new TextComponentString("No dragon nest found"));
        } else {
            sender.sendMessage(new TextComponentString("Nearest enchant dragon nest: " +
                    pos.getX() + ", " +
                    pos.getY() + ", " +
                    pos.getZ()
            ));
        }
    }
}
