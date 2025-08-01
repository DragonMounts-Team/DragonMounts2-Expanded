package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.inventory.DragonContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

import static net.dragonmounts.util.ByteBufferUtil.readString;
import static net.dragonmounts.util.ByteBufferUtil.writeString;

public class CRenameFlutePacket implements IMessage {
    public @Nonnull String name;

    public CRenameFlutePacket() {
        this.name = "";
    }

    public CRenameFlutePacket(String name) {
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        if (buffer.readableBytes() >= 1) {
            String name = ChatAllowedCharacters.filterAllowedCharacters(readString(buffer, 32767));
            if (name.length() <= 35) {
                this.name = name;
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        writeString(buffer, this.name);
    }

    public static class Handler implements IMessageHandler<CRenameFlutePacket, IMessage> {
        @Override
        public IMessage onMessage(CRenameFlutePacket message, MessageContext ctx) {
            Container container = ctx.getServerHandler().player.openContainer;
            if (container instanceof DragonContainer) {
                ((DragonContainer<?>) container).flute.applyName(message.name);
            }
            return null;
        }
    }
}