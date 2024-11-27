package net.dragonmounts.cmd;

import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
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

public class BreedCommand extends DragonHandlerCommand {
    private final Object2ObjectOpenHashMap<String, EnumDragonBreed> breeds;

    public BreedCommand() {
        super(2);
        Object2ObjectOpenHashMap<String, EnumDragonBreed> breeds = new Object2ObjectOpenHashMap<>();
        for (EnumDragonBreed breed : EnumDragonBreed.values()) {
            breeds.put(breed.name().toLowerCase(), breed);
        }
        this.breeds = breeds;
    }

    @Override
    public String getName() {
        return "breed";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dragon.breed.usage";
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
                throw new WrongUsageException("commands.dragon.breed.usage");
        }
        EnumDragonBreed breed = this.breeds.get(args[0]);
        if (breed == null) throw new CommandException("commands.dragon.breed.invalid");
        for (EntityTameableDragon dragon : dragons) {
            dragon.setBreedType(breed);
            sender.sendMessage(new TextComponentTranslation("commands.dragon.breed.success", dragon.getDisplayName(), breed.identifier));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1
                ? getListOfStringsMatchingLastWord(args, this.breeds.keySet())
                : super.getTabCompletions(server, sender, args, pos);
    }
}
