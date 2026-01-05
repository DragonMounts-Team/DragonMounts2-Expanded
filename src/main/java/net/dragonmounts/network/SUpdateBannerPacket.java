package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

import static net.dragonmounts.util.ByteBufferUtil.readStackSilently;

public class SUpdateBannerPacket implements IMessage {
    public int id;
    public int slot;
    public @Nonnull ItemStack banner;

    public SUpdateBannerPacket() {
        this.id = -1;
        this.banner = ItemStack.EMPTY;
    }

    public SUpdateBannerPacket(int id, int slot, @Nonnull ItemStack banner) {
        this.id = id;
        this.slot = slot;
        this.banner = banner;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        PacketBuffer wrapped = new PacketBuffer(buffer);
        this.id = wrapped.readVarInt();
        this.slot = wrapped.readVarInt();
        this.banner = readStackSilently(wrapped);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        new PacketBuffer(buffer)
                .writeVarInt(this.id)
                .writeVarInt(this.slot)
                .writeItemStack(this.banner);
    }

    public IMessage handle(MessageContext context) {
        WorldClient level = Minecraft.getMinecraft().world;
        if (level == null) return null;
        Entity entity = level.getEntityByID(this.id);
        if (entity instanceof TameableDragonEntity) {
            ((TameableDragonEntity) entity).inventory.setBanner(this.slot, this.banner);
        }
        return null;
    }
}
