package net.dragonmounts.command;

import com.google.common.base.Predicates;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.world.DMWorldGenerator;
import net.dragonmounts.world.DragonNest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nullable;
import java.util.List;


public class LocateCommand extends CommandBase {
    @Override
    public String getName() {
        return "locate";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.locate.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        BlockPos source = sender.getPosition();
        switch (args.length) {
            case 0: {
                ImmutablePair<BlockPos, DragonNest> result = DMWorldGenerator.DRAGON_NESTS.findNearestNest(sender.getEntityWorld(), sender.getPosition(), 100, false, Predicates.alwaysTrue());
                if (result == null) {
                    sender.sendMessage(new TextComponentTranslation("commands.locate.structure.not_found", "#draonmounts:dragon_nest"));
                } else {
                    BlockPos pos = result.getLeft();
                    sender.sendMessage(formatResult(
                            pos,
                            "#draonmounts:dragon_nests (" + result.getRight().name + ")",
                            (int) Math.sqrt(squaredDiff(source.getX(), pos.getX()) + squaredDiff(source.getZ(), pos.getZ()))
                    ));
                }
                break;
            }
            case 1: {
                DragonNest nest = DMWorldGenerator.DRAGON_NESTS.byName(DMUtils.parseIdentifier(args[0]));
                if (nest == null) throw new CommandException("commands.locate.invalid", args[0]);
                ImmutablePair<BlockPos, DragonNest> result = DMWorldGenerator.DRAGON_NESTS.findNearestNest(sender.getEntityWorld(), sender.getPosition(), 100, false, nest::equals);
                if (result == null) {
                    sender.sendMessage(new TextComponentTranslation("commands.locate.structure.not_found", nest.name));
                } else {
                    BlockPos pos = result.getLeft();
                    sender.sendMessage(formatResult(
                            pos,
                            nest.name.toString(),
                            (int) Math.sqrt(squaredDiff(source.getX(), pos.getX()) + squaredDiff(source.getZ(), pos.getZ()))
                    ));
                }
                break;
            }
            default:
                throw new WrongUsageException("commands.dragonmounts.locate.usage");
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
                .setColor(TextFormatting.GREEN)
                .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " ~ " + pos.getZ()))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("chat.coordinates.tooltip")));
        return new TextComponentTranslation("commands.locate.structure.success", new TextComponentTranslation(name), coordinates, distance);
    }
}
