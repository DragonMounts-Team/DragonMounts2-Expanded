package net.dragonmounts.network;

import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static net.dragonmounts.util.VarInt.readVarInt;
import static net.dragonmounts.util.VarInt.writeVarInt;

public class MessageDragonGui implements IMessage {

    public int dragonId;
    /**
     * 1 -> sit
     * 2 -> lock
     */
    public int state;

    public MessageDragonGui() {}

    public MessageDragonGui(int dragonId, int state) {
        this.dragonId = dragonId;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dragonId = readVarInt(buf);
        this.state = readVarInt(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeVarInt(buf, this.dragonId, this.state);
    }

    public static class MessageDragonGuiHandler implements IMessageHandler<MessageDragonGui, IMessage> {
        @Override
        public IMessage onMessage(MessageDragonGui message, MessageContext ctx) {
            Entity entity = ctx.getServerHandler().player.world.getEntityByID(message.dragonId);
            if (entity instanceof EntityTameableDragon) {
                EntityTameableDragon dragon = (EntityTameableDragon) entity;
                switch (message.state) {
                    case 1:
                        dragon.getAISit().setSitting(!dragon.isSitting());
                        break;
                    case 2:
                        dragon.setToAllowedOtherPlayers(!dragon.allowedOtherPlayers());
                        break;
                }
            }
            return null;
        }
    }
}