package net.dragonmounts.command;

import net.dragonmounts.util.DMUtils;
import net.dragonmounts.world.DMWorldGenerator;
import net.dragonmounts.world.DragonNestImpl;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.util.List;


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
        BlockPos source = sender.getPosition(), result;
        switch (args.length) {
            case 0: {
                result = DMWorldGenerator.DRAGON_NESTS.getNearestStructurePos(sender.getEntityWorld(), sender.getPosition(), false);
                break;
            }
            case 1: {
                DragonNestImpl nest = DMWorldGenerator.DRAGON_NESTS.byName(DMUtils.parseIdentifier(args[0]));
                if (nest == null) throw new CommandException("commands.locate.dragonmounts.nest.invalid", args[0]);
                result = nest.getNearestStructurePos(sender.getEntityWorld(), sender.getPosition(), false);
                break;
            }
            default:
                throw new WrongUsageException("commands.dragonmounts.locate.usage");
        }
        if (result == null) {
            sender.sendMessage(new TextComponentString("No dragon nest found"));
        } else {
            sender.sendMessage(formatResult(
                    result,
                    "dragon nest",
                    (int) Math.sqrt(squaredDiff(source.getX(), result.getX()) + squaredDiff(source.getZ(), result.getZ()))
            ));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1
                ? getListOfStringsMatchingLastWord(args, DMWorldGenerator.DRAGON_NESTS.keys())
                : super.getTabCompletions(server, sender, args, pos);
    }

    public static int squaredDiff(int a, int b) {
        int dist = a - b;
        return dist * dist;
    }

    public static ITextComponent formatResult(BlockPos pos, String name, int distance) {
        ITextComponent coordinates = new TextComponentTranslation(
                "chat.square_brackets",
                new TextComponentTranslation("chat.coordinates", pos.getX(), "~", pos.getZ())
        );
        coordinates.getStyle()
                .setColor(TextFormatting.DARK_GREEN)
                .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " ~ " + pos.getZ()))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("chat.coordinates.tooltip")));
        return new TextComponentTranslation("commands.locate.structure.success", name, coordinates, distance);
    }
}
