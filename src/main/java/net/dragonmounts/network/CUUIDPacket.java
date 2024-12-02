package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;
import java.util.UUID;

public class CUUIDPacket implements IMessage {
    public UUID uuid;

    public CUUIDPacket() {}

    public CUUIDPacket(@Nullable UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.uuid = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (this.uuid == null) throw new NullPointerException();
        buf.writeLong(this.uuid.getMostSignificantBits());
        buf.writeLong(this.uuid.getLeastSignificantBits());
    }
}
