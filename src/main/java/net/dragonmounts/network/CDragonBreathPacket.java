package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static net.dragonmounts.util.ByteBufferUtil.readVarInt;
import static net.dragonmounts.util.ByteBufferUtil.writeVarInt;

public class CDragonBreathPacket implements IMessage {
    public int id;
    public boolean breathing;

    public CDragonBreathPacket() {}

    public CDragonBreathPacket(int id, boolean breathing) {
        this.id = id;
        this.breathing = breathing;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.id = readVarInt(buffer);
        this.breathing = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        writeVarInt(buffer, this.id);
        buffer.writeBoolean(this.breathing);
    }

    public IMessage handle(MessageContext context) {
        Entity entity = context.getServerHandler().player.world.getEntityByID(this.id);
        if (entity instanceof TameableDragonEntity) {
            ((TameableDragonEntity) entity).setUsingBreathWeapon(this.breathing);
        }
        return null;
    }
}
