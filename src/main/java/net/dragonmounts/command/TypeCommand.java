package net.dragonmounts.command;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.DMUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TypeCommand extends EntityCommand {
    public TypeCommand() {
        super(2);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getName() {
        return "type";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.type.usage";
    }

    @Override
    protected boolean isValidTarget(Entity entity) {
        return entity instanceof ServerDragonEntity;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<ServerDragonEntity> dragons;
        switch (args.length) {
            case 1:
                dragons = Collections.singletonList(getClosestEntity(sender, ServerDragonEntity.class));
                break;
            case 2:
                dragons = getSelectedEntities(server, sender, args[1], ServerDragonEntity.class, "commands.dragonmounts.notFound");
                break;
            default:
                throw new WrongUsageException("commands.dragonmounts.type.usage");
        }
        DragonType type = DragonType.REGISTRY.getIfPresent(DMUtils.parseIdentifier(args[0]));
        if (type == null) throw new CommandException("commands.dragonmounts.type.invalid", args[0]);
        for (TameableDragonEntity dragon : dragons) {
            ITextComponent name = dragon.getDisplayName();
            dragon.convertTo(type);
            notifyCommandListener(sender, this, "commands.dragonmounts.type.success", name, new TextComponentTranslation(type.translationKey).setStyle(new Style().setColor(type.formatting)));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1
                ? getListOfStringsMatchingLastWord(args, DragonType.REGISTRY.getKeys())
                : super.getTabCompletions(server, sender, args, pos);
    }
}
