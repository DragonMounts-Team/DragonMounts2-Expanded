package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static net.dragonmounts.util.ByteBufferUtil.readVarInt;
import static net.dragonmounts.util.ByteBufferUtil.writeVarInt;

public class CDragonConfigPacket implements IMessage {
    public int dragonId;
    /**
     * 1 -> sit
     * 2 -> lock
     */
    public int option;

    public CDragonConfigPacket() {}

    public CDragonConfigPacket(int dragonId, int option) {
        this.dragonId = dragonId;
        this.option = option;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dragonId = readVarInt(buf);
        this.option = readVarInt(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeVarInt(buf, this.dragonId, this.option);
    }

    public static class Handler implements IMessageHandler<CDragonConfigPacket, IMessage> {
        @Override
        public IMessage onMessage(CDragonConfigPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            Entity entity = player.world.getEntityByID(message.dragonId);
            if (entity instanceof TameableDragonEntity) {
                TameableDragonEntity dragon = (TameableDragonEntity) entity;
                Relation relation = Relation.checkRelation(dragon, player);
                switch (message.option) {
                    case 1:
                        if (Relation.STRANGER == relation) {
                            relation.onDeny(player);
                            return null;
                        }
                        dragon.getAISit().setSitting(!dragon.isSitting());
                        break;
                    case 2:
                        if (Relation.OWNER != relation) {
                            relation.onDeny(player);
                            return null;
                        }
                        dragon.setToAllowedOtherPlayers(!dragon.allowedOtherPlayers());
                        break;
                }
            }
            return null;
        }
    }
}