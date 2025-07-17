package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Random;

import static net.dragonmounts.util.ByteBufferUtil.readVarInt;
import static net.dragonmounts.util.ByteBufferUtil.writeVarInt;

public class SRiposteEffectPacket implements IMessage {
    public int id;
    public int flag;

    public SRiposteEffectPacket() {
        this.id = -1;
    }

    public SRiposteEffectPacket(int id, int flag) {
        this.id = id;
        this.flag = flag;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.id = readVarInt(buffer);
        this.flag = readVarInt(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        writeVarInt(buffer, this.id, this.flag);
    }

    public static class Handler implements IMessageHandler<SRiposteEffectPacket, IMessage> {
        @Override
        public IMessage onMessage(SRiposteEffectPacket packet, MessageContext context) {
            WorldClient level = Minecraft.getMinecraft().world;
            if (level == null) return null;
            Entity entity = level.getEntityByID(packet.id);
            if (entity == null) return null;
            double x = entity.posX;
            double z = entity.posZ;
            if ((packet.flag & 0b01) == 0b01) {
                Random random = level.rand;
                double y = entity.posY + 0.1;
                double px = x + random.nextDouble() - 0.3;
                double py = y + random.nextDouble() + 0.8;
                double pz = z + random.nextDouble() - 0.3;
                double ox = random.nextDouble() * 2 - 0.6;
                double oy = random.nextDouble() - 0.3;
                double oz = random.nextDouble() * 2 - 0.6;
                for (int i = -30; i < 31; ++i) {
                    level.spawnParticle(EnumParticleTypes.BLOCK_DUST, px, py, pz, ox, oy, oz, 79); //79
                    level.spawnParticle(EnumParticleTypes.CLOUD, false, x, y, z, Math.sin(i), 0, Math.cos(i));
                }
                level.playSound(entity.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.46F, 1.0F, false);
            }
            if ((packet.flag & 0b10) == 0b10) {
                double y = entity.posY + 1;
                for (int i = -27; i < 28; ++i) {
                    level.spawnParticle(EnumParticleTypes.FLAME, x, y, z, Math.sin(i) / 3, 0, Math.cos(i) / 3);
                }
                level.playSound(entity.getPosition(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.46F, 1.0F, false);
            }
            return null;
        }
    }
}
