package com.TheRPGAdventurer.ROTD.network;

import com.TheRPGAdventurer.ROTD.capability.ArmorEffectManager;
import com.TheRPGAdventurer.ROTD.registry.CooldownCategory;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.TheRPGAdventurer.ROTD.util.VarInt.readVarInt;
import static com.TheRPGAdventurer.ROTD.util.VarInt.writeVarInt;

public class SSyncCooldownPacket implements IMessage {
    public int id;
    public int cd;

    public SSyncCooldownPacket() {
        this.id = -1;
        this.cd = 0;
    }

    public SSyncCooldownPacket(int id, int cd) {
        this.id = id;
        this.cd = cd;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.id = readVarInt(buffer);
        this.cd = readVarInt(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        writeVarInt(writeVarInt(buffer, this.id), this.cd);
    }

    public static class Handler implements IMessageHandler<SSyncCooldownPacket, IMessage> {
        @Override
        public IMessage onMessage(SSyncCooldownPacket packet, MessageContext context) {
            ArmorEffectManager manager = ArmorEffectManager.getLocal();
            if (manager == null) return null;
            CooldownCategory category = CooldownCategory.REGISTRY.getValue(packet.id);
            if (category == null) return null;
            manager.setCooldown(category, packet.cd);
            return null;
        }
    }
}
