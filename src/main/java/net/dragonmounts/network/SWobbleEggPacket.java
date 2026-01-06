package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.client.ClientDragonEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static net.dragonmounts.util.ByteBufferUtil.readVarInt;
import static net.dragonmounts.util.ByteBufferUtil.writeVarInt;

public class SWobbleEggPacket implements IMessage {
    public int id;
    public int amplitude;
    public int axis;
    public int flag;

    public SWobbleEggPacket() {
        this.id = -1;
    }

    public SWobbleEggPacket(int id, int amplitude, int axis, int flag) {
        this.id = id;
        this.amplitude = amplitude;
        this.axis = axis;
        this.flag = flag;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.id = readVarInt(buffer);
        this.amplitude = buffer.readByte();
        this.axis = buffer.readByte();
        this.flag = buffer.readByte();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        writeVarInt(buffer, this.id);
        buffer.writeByte(this.amplitude)
                .writeByte(this.axis)
                .writeByte(this.flag);
    }

    public IMessage handle(MessageContext context) {
        WorldClient level = Minecraft.getMinecraft().world;
        if (level == null) return null;
        Entity entity = level.getEntityByID(this.id);
        if (entity instanceof ClientDragonEntity) {
            int flag = this.flag;
            ((ClientDragonEntity) entity).applyWobble(
                    this.amplitude,
                    // -0 == +0, so offset all non-negative numbers by +1
                    (flag & 0b10) == 0b10 ? -this.axis : this.axis + 1,
                    (flag & 0b01) == 0b01
            );
        }
        return null;
    }
}
