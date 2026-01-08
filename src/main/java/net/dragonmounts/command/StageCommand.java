package net.dragonmounts.command;

import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class StageCommand extends EntityCommand {
    public StageCommand() {
        super(2);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getName() {
        return "stage";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragonmounts.stage.usage";
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
                throw new WrongUsageException("commands.dragonmounts.stage.usage");
        }
        DragonLifeStage stage = DragonLifeStage.BY_NAME.get(args[0].toLowerCase());
        if (stage == null) throw new CommandException("commands.dragonmounts.stage.invalid", args[0]);
        for (TameableDragonEntity dragon : dragons) {
            dragon.setLifeStage(stage, true, true);
            notifyCommandListener(sender, this, "commands.dragonmounts.stage.success", dragon.getDisplayName(), new TextComponentTranslation(stage.translationKey));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1
                ? getListOfStringsMatchingLastWord(args, DragonLifeStage.BY_NAME.keySet())
                : super.getTabCompletions(server, sender, args, pos);
    }
}
