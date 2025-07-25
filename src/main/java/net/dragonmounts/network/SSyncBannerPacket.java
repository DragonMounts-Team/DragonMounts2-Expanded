package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.inventory.DragonInventory;
import net.dragonmounts.util.LogUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

import static net.dragonmounts.util.ByteBufferUtil.readVarInt;
import static net.dragonmounts.util.ByteBufferUtil.writeVarInt;

public class SSyncBannerPacket implements IMessage {
    public static void writeBanners(ByteBuf raw, int flag, ItemStack[] banners) {
        int flagIndex = raw.writerIndex();
        raw.writeByte(flag);
        assert banners.length == 4;
        PacketBuffer buffer = new PacketBuffer(raw);
        if (flag == -1) {
            flag = 0;
            for (int i = 0; i < 4; ++i) {
                ItemStack banner = banners[i];
                if (banners[i] == null) continue;
                buffer.writeItemStack(banner);
                flag |= 1 << i;
            }
            int finalIndex = raw.writerIndex();
            raw.setByte(flagIndex, (byte) flag);
            raw.writerIndex(finalIndex);
        } else {
            for (ItemStack banner : banners) {
                if (banner == null) continue;
                buffer.writeItemStack(banner);
            }
        }
    }

    public static int readBanners(ByteBuf raw, ItemStack[] banners) {
        int flag = raw.readByte();
        PacketBuffer buffer = new PacketBuffer(raw);
        for (int i = 0; i < 4; ++i) {
            if ((flag & (1 << i)) == 0) continue;
            try {
                banners[i] = buffer.readItemStack();
            } catch (IOException e) {
                LogUtil.LOGGER.error("Error reading item stack", e);
                banners[i] = ItemStack.EMPTY;
            }
        }
        return flag;
    }

    public int id;
    public int flag;
    public ItemStack[] banners;

    public SSyncBannerPacket() {
        this.id = -1;
        this.banners = new ItemStack[4];
    }

    public SSyncBannerPacket(int id, int flag, ItemStack[] banners) {
        this.id = id;
        this.flag = flag;
        this.banners = banners;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.id = readVarInt(buffer);
        this.flag = readBanners(buffer, this.banners);
    }

    @Override
    public void toBytes(ByteBuf raw) {
        writeVarInt(raw, this.id);
        writeBanners(raw, this.flag, this.banners);
    }

    public static class Handler implements IMessageHandler<SSyncBannerPacket, IMessage> {
        @Override
        public IMessage onMessage(SSyncBannerPacket packet, MessageContext context) {
            WorldClient level = Minecraft.getMinecraft().world;
            if (level == null) return null;
            Entity entity = level.getEntityByID(packet.id);
            if (entity instanceof TameableDragonEntity) {
                DragonInventory inventory = ((TameableDragonEntity) entity).inventory;
                ItemStack[] banners = packet.banners;
                for (int i = 0; i < 4; ++i) {
                    ItemStack stack = banners[i];
                    if (stack == null) continue;
                    inventory.setBanner(i, packet.banners[i]);
                }
            }
            return null;
        }
    }
}
