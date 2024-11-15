package com.TheRPGAdventurer.ROTD.cmd;

import com.TheRPGAdventurer.ROTD.inits.ModSounds;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collections;
import java.util.List;

public class CommandDragonGender extends DragonHandlerCommand {
	public CommandDragonGender() {
		super(1);
	}

	@Override
	public String getName() {
		return "gender";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.dragon.gender.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		List<EntityTameableDragon> dragons;
		switch (args.length) {
			case 0:
				dragons = Collections.singletonList(getClosestDragon(sender));
				break;
			case 1:
				dragons = getSelectedDragons(server, sender, args[0]);
				if (dragons.isEmpty()) throw new EntityNotFoundException("commands.dragon.notFound", args[0]);
				break;
			default:
				throw new WrongUsageException("commands.dragon.gender.usage");
		}
		for (EntityTameableDragon dragon : dragons) {
			dragon.setOppositeGender();
			dragon.world.playSound(null, dragon.getPosition(), ModSounds.DRAGON_SWITCH, SoundCategory.NEUTRAL, 1, 1);
			sender.sendMessage(new TextComponentTranslation(
					"commands.dragon.gender.success",
					dragon.getDisplayName(),
					new TextComponentTranslation(dragon.isMale() ? "male" : "female")
			));
		}
	}
}
