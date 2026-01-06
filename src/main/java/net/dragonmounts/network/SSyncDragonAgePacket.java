package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static net.dragonmounts.util.ByteBufferUtil.readVarInt;
import static net.dragonmounts.util.ByteBufferUtil.writeVarInt;
import static net.dragonmounts.util.DMUtils.TICKS_PER_MINECRAFT_HOUR;

public class SSyncDragonAgePacket implements IMessage {
    public static SSyncDragonAgePacket fromTotalTicks(int ticks) {
        DragonLifeStage stage;
        if (ticks < 36 * TICKS_PER_MINECRAFT_HOUR) {
            return new SSyncDragonAgePacket(0, ticks, DragonLifeStage.EGG);
        }
        ticks -= 36 * TICKS_PER_MINECRAFT_HOUR;
        if (ticks < 48 * TICKS_PER_MINECRAFT_HOUR) {
            return new SSyncDragonAgePacket(0, ticks, DragonLifeStage.HATCHLING);
        }
        ticks -= 48 * TICKS_PER_MINECRAFT_HOUR;
        if (ticks < 24 * TICKS_PER_MINECRAFT_HOUR) {
            return new SSyncDragonAgePacket(0, ticks, DragonLifeStage.INFANT);
        }
        ticks -= 24 * TICKS_PER_MINECRAFT_HOUR;
        if (ticks < 32 * TICKS_PER_MINECRAFT_HOUR) {
            return new SSyncDragonAgePacket(0, ticks, DragonLifeStage.FLEDGLING);
        }
        ticks -= 32 * TICKS_PER_MINECRAFT_HOUR;
        return ticks < 60 * TICKS_PER_MINECRAFT_HOUR
                ? new SSyncDragonAgePacket(0, ticks, DragonLifeStage.JUVENILE)
                : new SSyncDragonAgePacket(0, ticks - 60 * TICKS_PER_MINECRAFT_HOUR, DragonLifeStage.ADULT);
    }

    public int id;
    public int age;
    public DragonLifeStage stage;

    public SSyncDragonAgePacket() {
        this.id = -1;
        this.age = 0;
        this.stage = DragonLifeStage.ADULT;
    }

    public SSyncDragonAgePacket(int id, int age, DragonLifeStage stage) {
        this.id = id;
        this.age = age;
        this.stage = stage;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.id = readVarInt(buffer);
        this.age = readVarInt(buffer);
        this.stage = DragonLifeStage.byId(readVarInt(buffer));
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        writeVarInt(buffer, this.id);
        writeVarInt(buffer, this.age);
        writeVarInt(buffer, this.stage.ordinal());
    }

    public IMessage handle(MessageContext context) {
        WorldClient level = Minecraft.getMinecraft().world;
        if (level == null) return null;
        Entity entity = level.getEntityByID(this.id);
        if (entity instanceof TameableDragonEntity) {
            TameableDragonEntity dragon = (TameableDragonEntity) entity;
            dragon.setGrowingAge(this.age);
            dragon.setLifeStage(this.stage, false, false);
        }
        return null;
    }
}