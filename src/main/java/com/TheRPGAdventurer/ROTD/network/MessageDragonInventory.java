package com.TheRPGAdventurer.ROTD.network;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.TheRPGAdventurer.ROTD.util.VarInt.readVarInt;
import static com.TheRPGAdventurer.ROTD.util.VarInt.writeVarInt;

public class MessageDragonInventory implements IMessage {

    public int dragonId;
    public int slot;
    public int state;

    public MessageDragonInventory() {}

    public MessageDragonInventory(int dragonId, int slot, int state) {
        this.dragonId = dragonId;
        this.slot = slot;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dragonId = readVarInt(buf);
        this.slot = readVarInt(buf);
        this.state = readVarInt(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeVarInt(buf, this.dragonId, this.slot, this.state);
    }

    public static class MessageDragonInventoryHandler implements IMessageHandler<MessageDragonInventory, IMessage> {
        @Override
        public IMessage onMessage(MessageDragonInventory message, MessageContext ctx) {
            Entity entity = ctx.getServerHandler().player.world.getEntityByID(message.dragonId);
            if (entity instanceof EntityTameableDragon) {
                EntityTameableDragon dragon = (EntityTameableDragon) entity;
                switch (message.slot) {
                    case 0:
                        dragon.setSaddled(message.state == 1);
                        break;
                    case 1:
                        dragon.setChested(message.state == 1);
                        break;
                    case 2:
                        dragon.setArmor(message.state);
                        break;
                    case 31: // ?
                        dragon.setBanner1(dragon.dragonInv.getStackInSlot(31));
                        break;
                    case 32: // ?
                        dragon.setBanner2(dragon.dragonInv.getStackInSlot(32));
                        break;
                    case 33: // ?
                        dragon.setBanner3(dragon.dragonInv.getStackInSlot(33));
                        break;
                    case 34: // ?
                        dragon.setBanner4(dragon.dragonInv.getStackInSlot(34));
                        break;
                }
            }
            return null;
        }
    }
}