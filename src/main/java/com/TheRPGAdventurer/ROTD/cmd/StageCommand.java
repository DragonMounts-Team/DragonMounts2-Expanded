package com.TheRPGAdventurer.ROTD.cmd;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class StageCommand extends DragonHandlerCommand {
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
    public String getName() {
        return "stage";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragon.stage.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<EntityTameableDragon> dragons;
        switch (args.length) {
            case 1:
                dragons = Collections.singletonList(getClosestDragon(sender));
                break;
            case 2:
                dragons = getSelectedDragons(server, sender, args[1]);
                if (dragons.isEmpty()) throw new EntityNotFoundException("commands.dragon.notFound", args[1]);
                break;
            default:
                throw new WrongUsageException("commands.dragon.stage.usage");
        }
        DragonLifeStage stage = this.stages.get(args[0]);
        if (stage == null) throw new CommandException("commands.dragon.stage.invalid");
        for (EntityTameableDragon dragon : dragons) {
            dragon.getLifeStageHelper().setLifeStage(stage);
            sender.sendMessage(new TextComponentTranslation("commands.dragon.breed.success", dragon.getDisplayName(), stage.identifier));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1
                ? getListOfStringsMatchingLastWord(args, this.stages.keySet())
                : super.getTabCompletions(server, sender, args, pos);
    }
}
