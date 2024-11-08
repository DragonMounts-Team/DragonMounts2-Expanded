package com.TheRPGAdventurer.ROTD.network;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageDragonSit extends CUUIDPacket {
    public MessageDragonSit() {}

    public MessageDragonSit(UUID uuid) {
        super(uuid);
    }

    public static class MessageDragonSitHandler implements IMessageHandler<MessageDragonSit, IMessage> {
        @Override
        public IMessage onMessage(MessageDragonSit message, MessageContext ctx) {
            //        player.world.playSound(null, player.getPosition(), ModSounds.DRAGON_WHISTLE, SoundCategory.PLAYERS, 1, 1);
            Entity entity = ctx.getServerHandler().server.getEntityFromUuid(message.uuid);
            if (entity instanceof EntityTameableDragon) {
                EntityTameableDragon dragon = (EntityTameableDragon) entity;
                dragon.getAISit().setSitting(!dragon.isSitting());
            }
            return null;
        }
    }
}