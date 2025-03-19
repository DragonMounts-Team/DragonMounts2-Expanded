package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static net.dragonmounts.util.ByteBufferUtil.compressFlags;
import static net.dragonmounts.util.ByteBufferUtil.readFlags;

public class CDragonControlPacket implements IMessage {
    private boolean breathing;
    private boolean boosting;
    private boolean descent;
    private boolean toggleHovering;
    private boolean toggleYawAlignment;
    private boolean togglePitchAlignment;
    private int flags = -1;

    public CDragonControlPacket() {}

    public CDragonControlPacket(
            boolean breathing,
            boolean boosting,
            boolean descent,
            boolean toggleHovering,
            boolean toggleYawAlignment,
            boolean togglePitchAlignment
    ) {
        this.breathing = breathing;
        this.boosting = boosting;
        this.descent = descent;
        this.toggleHovering = toggleHovering;
        this.toggleYawAlignment = toggleYawAlignment;
        this.togglePitchAlignment = togglePitchAlignment;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean[] flags = readFlags(buf);
        this.breathing = flags[0];
        this.boosting = flags[1];
        this.descent = flags[2];
        this.toggleHovering = flags[3];
        this.toggleYawAlignment = flags[4];
        this.togglePitchAlignment = flags[5];
    }

    public int getFlags() {
        if (this.flags == -1) {
            this.flags = compressFlags(
                    this.breathing,
                    this.boosting,
                    this.descent,
                    this.toggleHovering,
                    this.toggleYawAlignment,
                    this.togglePitchAlignment
            );
        }
        return this.flags;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.getFlags());
    }

    public static class Handler implements IMessageHandler<CDragonControlPacket, IMessage> {
        @Override
        public IMessage onMessage(CDragonControlPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            Entity entity = player.getRidingEntity();
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
