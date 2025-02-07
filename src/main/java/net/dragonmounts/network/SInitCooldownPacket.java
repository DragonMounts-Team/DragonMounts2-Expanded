package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.capability.ArmorEffectManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static net.dragonmounts.util.ByteBufferUtil.readVarInt;
import static net.dragonmounts.util.ByteBufferUtil.writeVarInt;

public class SInitCooldownPacket implements IMessage {
    public int size;
    public int[] data;

    public SInitCooldownPacket() {
        this.size = 0;
        this.data = new int[0];
    }

    public SInitCooldownPacket(int size, int[] data) {
        this.size = size;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        final int maxSize = readVarInt(buffer);
        final int[] data = new int[maxSize];
        int i = 0;
        while (i < maxSize && buffer.isReadable()) {
            data[i++] = readVarInt(buffer);
        }
        this.size = i;
        this.data = data;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        writeVarInt(buffer, this.size);
        writeVarInt(buffer, this.data);
    }

    public static class Handler implements IMessageHandler<SInitCooldownPacket, IMessage> {
        @Override
        public IMessage onMessage(SInitCooldownPacket packet, MessageContext context) {
            ArmorEffectManager.init(packet);
            return null;
        }
    }
}
