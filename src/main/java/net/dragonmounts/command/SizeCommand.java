package net.dragonmounts.command;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class SizeCommand extends EntityCommand {
    public SizeCommand() {
        super(2);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.size.usage";
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
                dragons = getSelectedEntities(server, sender, args[1], ServerDragonEntity.class);
                if (dragons.isEmpty()) throw new EntityNotFoundException("commands.dragonmounts.notFound", args[1]);
                break;
            default:
                throw new WrongUsageException("commands.dragonmounts.size.usage");
        }
        float size = (float) parseDouble(args[0], 0.25, TameableDragonEntity.MAX_SCALE);
        for (ServerDragonEntity dragon : dragons) {
            dragon.setBodySize(size);
            notifyCommandListener(sender, this, "commands.dragonmounts.size.success", dragon.getDisplayName(), size);
        }
    }
}
