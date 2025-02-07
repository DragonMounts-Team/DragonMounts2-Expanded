package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static net.dragonmounts.util.ByteBufferUtil.*;

public class CDragonControlPacket extends CDragonBreathPacket {
    public boolean boosting;
    public boolean descent;
    public boolean toggleHovering;
    public boolean toggleYawAlignment;
    public boolean togglePitchAlignment;

    public CDragonControlPacket() {}

    public CDragonControlPacket(
            int id,
            boolean breathing,
            boolean boosting,
            boolean descent,
            boolean toggleHovering,
            boolean toggleYawAlignment,
            boolean togglePitchAlignment
    ) {
        super(id, breathing);
        this.boosting = boosting;
        this.descent = descent;
        this.toggleHovering = toggleHovering;
        this.toggleYawAlignment = toggleYawAlignment;
        this.togglePitchAlignment = togglePitchAlignment;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.id = readVarInt(buf);
        boolean[] flags = readFlags(buf);
        this.breathing = flags[0];
        this.boosting = flags[1];
        this.descent = flags[2];
        this.toggleHovering = flags[3];
        this.toggleYawAlignment = flags[4];
        this.togglePitchAlignment = flags[5];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeVarInt(buf, this.id);
        writeFlags(
                buf,
                this.breathing,
                this.boosting,
                this.descent,
                this.toggleHovering,
                this.toggleYawAlignment,
                this.togglePitchAlignment
        );
    }

    public static class Handler implements IMessageHandler<CDragonControlPacket, IMessage> {
        @Override
        public IMessage onMessage(CDragonControlPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            Entity entity = player.world.getEntityByID(message.id);
            if (entity instanceof TameableDragonEntity) {
                TameableDragonEntity dragon = (TameableDragonEntity) entity;
                dragon.setUsingBreathWeapon(message.breathing);
                if (message.toggleHovering) {
                    dragon.setUnHovered(!dragon.isUnHovered());
                    player.sendStatusMessage(new TextComponentTranslation(
                            "message.dragonmounts.control.toggleHovering",
                            new TextComponentTranslation(dragon.isUnHovered() ? "options.off" : "options.on")
                    ), false);
                }
                if (message.toggleYawAlignment) {
                    dragon.setFollowYaw(!dragon.followYaw());
                    player.sendStatusMessage(new TextComponentTranslation(
                            "message.dragonmounts.control.toggleYawAlignment",
                            new TextComponentTranslation(dragon.followYaw() ? "options.on" : "options.off")
                    ), false);
                }
                if (message.togglePitchAlignment) {
                    dragon.setYLocked(!dragon.isYLocked());
                    player.sendStatusMessage(new TextComponentTranslation(
                            "message.dragonmounts.control.togglePitchAlignment",
                            new TextComponentTranslation(dragon.isYLocked() ? "options.off" : "options.on")
                    ), false);
                }
                dragon.setGoingDown(message.descent);
                dragon.setBoosting(message.boosting);
            }
            return null;
        }
    }
}
