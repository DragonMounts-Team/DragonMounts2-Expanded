package net.dragonmounts.command;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class StageCommand extends EntityCommand {
    private final Object2ObjectOpenHashMap<String, DragonLifeStage> stages;

    public StageCommand() {
        super(2);
        Object2ObjectOpenHashMap<String, DragonLifeStage> stages = new Object2ObjectOpenHashMap<>();
        for (DragonLifeStage stage : DragonLifeStage.values()) {
            stages.put(stage.name().toLowerCase(), stage);
        }
        this.stages = stages;
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
                dragons = getSelectedEntities(server, sender, args[1], ServerDragonEntity.class);
                if (dragons.isEmpty()) throw new EntityNotFoundException("commands.dragonmounts.notFound", args[1]);
                break;
            default:
                throw new WrongUsageException("commands.dragonmounts.stage.usage");
        }
        DragonLifeStage stage = this.stages.get(args[0]);
        if (stage == null) throw new CommandException("commands.dragonmounts.stage.invalid", args[0]);
        for (TameableDragonEntity dragon : dragons) {
            dragon.lifeStageHelper.setLifeStage(stage);
            notifyCommandListener(sender, this, "commands.dragonmounts.stage.success", dragon.getDisplayName(), stage.identifier);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1
                ? getListOfStringsMatchingLastWord(args, this.stages.keySet())
                : super.getTabCompletions(server, sender, args, pos);
    }
}
