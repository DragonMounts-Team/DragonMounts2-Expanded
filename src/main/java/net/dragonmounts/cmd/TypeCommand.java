package net.dragonmounts.cmd;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.registry.DragonType;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TypeCommand extends DragonHandlerCommand {
    private final Object2ObjectOpenHashMap<String, DragonType> types;

    public TypeCommand() {
        super(2);
        Object2ObjectOpenHashMap<String, DragonType> types = new Object2ObjectOpenHashMap<>();
        for (Map.Entry<ResourceLocation, DragonType> entry : DragonType.REGISTRY.getEntries()) {
            types.put(entry.getKey().toString(), entry.getValue());
        }
        this.types = types;
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<TameableDragonEntity> dragons;
        switch (args.length) {
            case 1:
                dragons = Collections.singletonList(getClosestDragon(sender));
                break;
            case 2:
                dragons = getSelectedDragons(server, sender, args[1]);
                if (dragons.isEmpty()) throw new EntityNotFoundException("commands.dragonmounts.notFound", args[1]);
                break;
            default:
                throw new WrongUsageException("commands.dragonmounts.type.usage");
        }
        DragonType type = this.types.get(args[0]);
        if (type == null) throw new CommandException("commands.dragonmounts.type.invalid");
        for (TameableDragonEntity dragon : dragons) {
            ITextComponent name = dragon.getDisplayName();
            dragon.setVariant(type.variants.draw(dragon.getRNG(), dragon.getVariant()));
            sender.sendMessage(new TextComponentTranslation("commands.dragonmounts.type.success", name, new TextComponentTranslation(type.translationKey).setStyle(new Style().setColor(type.formatting))));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1
                ? getListOfStringsMatchingLastWord(args, this.types.keySet())
                : super.getTabCompletions(server, sender, args, pos);
    }
}
