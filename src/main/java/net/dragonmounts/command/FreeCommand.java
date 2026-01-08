package net.dragonmounts.command;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FreeCommand extends EntityCommand {
    public static UUID resolveOwner(MinecraftServer server, ICommandSender sender, String selector) throws CommandException {
        if (EntitySelector.isSelector(selector)) {
            List<EntityPlayerMP> player = EntitySelector.getPlayers(sender, selector);
            switch (player.size()) {
                case 0:
                    break;
                case 1:
                    return player.get(0).getUniqueID();
                default:
                    throw new PlayerNotFoundException("argument.player.toomany");
            }
        }
        if (selector.length() == 32
                && selector.charAt(8) == '-'
                && selector.charAt(13) == '-'
                && selector.charAt(18) == '-'
                && selector.charAt(23) == '-') {
            try {
                return UUID.fromString(selector);
            } catch (Exception ignored) {}
        }
        GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(selector);
        if (profile != null) return profile.getId();
        throw new PlayerNotFoundException("commands.generic.player.notFound", selector);
    }

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
        boolean forced;
        List<? extends EntityTameable> entities;
        UUID owner = null;
        switch (args.length) {
            case 0:
                entities = Collections.singletonList(getClosestEntity(sender, EntityTameable.class));
                forced = true;
                break;
            case 1:
                entities = getSelectedEntities(server, sender, args[0], EntityTameable.class, "commands.dragonmounts.notFound");
                forced = entities.size() == 1;
                break;
            case 2:
                if ("forced".equalsIgnoreCase(args[1])) {
                    entities = getSelectedEntities(server, sender, args[0], EntityTameable.class, "commands.dragonmounts.notFound");
                    forced = true;
                    break;
                }
                throw new WrongUsageException("commands.dragonmounts.free.usage");
            case 3:
                if ("owned_by".equalsIgnoreCase(args[1])) {
                    owner = resolveOwner(server, sender, args[2]);
                    entities = getSelectedEntities(server, sender, args[0], EntityTameable.class, "commands.dragonmounts.notFound");
                    forced = false;
                    break;
                }
                /* no break */
            default:
                throw new WrongUsageException("commands.dragonmounts.free.usage");
        }
        int succeed = 0;
        EntityTameable last = null;
        for (EntityTameable tameable : entities) {
            if (tameable.isTamed() && (forced || (owner != null && owner.equals(tameable.getOwnerId())))) {
                tameable.setTamed(false);
                tameable.setOwnerId(null);
                last = tameable;
                ++succeed;
                EntityAISit ai = tameable.getAISit();
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

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        switch (args.length) {
            case 2:
                return getListOfStringsMatchingLastWord(args, "forced", "owned_by");
            case 3:
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            default:
                return super.getTabCompletions(server, sender, args, pos);
        }
    }
}
