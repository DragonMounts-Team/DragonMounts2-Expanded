package net.dragonmounts.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FreeCommand extends EntityCommand {
    public FreeCommand() {
        super(1);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getName() {
        return "free";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.free.usage";
    }

    @Override
    protected boolean isValidTarget(Entity entity) {
        return entity instanceof EntityTameable;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<? extends EntityTameable> entities;
        switch (args.length) {
            case 0:
                entities = Collections.singletonList(getClosestEntity(sender, EntityTameable.class));
                break;
            case 1:
                entities = getSelectedEntities(server, sender, args[0], EntityTameable.class);
                if (entities.isEmpty()) throw new EntityNotFoundException("commands.dragonmounts.notFound", args[0]);
                break;
            default:
                throw new WrongUsageException("commands.dragonmounts.free.usage");
        }
        Iterator<? extends EntityTameable> iterator = entities.iterator();
        int succeed = 0;
        EntityTameable last = null;
        for (EntityTameable tameable : entities) {
            if (tameable.isTamed()) {
                tameable.setTamed(false);
                tameable.setOwnerId(null);
                last = tameable;
                ++succeed;
                EntityAISit ai = tameable.getAISit();
                //noinspection ConstantValue
                if (ai == null) continue;
                ai.setSitting(false);
            }
        }
        if (succeed == 1) {
            notifyCommandListener(sender, this, "commands.dragonmounts.free.single", last.getDisplayName());
        } else {
            notifyCommandListener(sender, this, "commands.dragonmounts.free.multiple", succeed);
        }
    }
}
