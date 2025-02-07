package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageDragonTeleport extends CUUIDPacket {
    public float pitch;
    public float yaw;

    public MessageDragonTeleport() {}

    public MessageDragonTeleport(UUID uuid, float pitch, float yaw) {
        super(uuid);
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        this.pitch = buf.readFloat();
        this.yaw = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeFloat(this.pitch).writeFloat(this.yaw);
    }

    public static class MessageDragonTeleportHandler implements IMessageHandler<MessageDragonTeleport, IMessage> {
        @Override
        public IMessage onMessage(MessageDragonTeleport message, MessageContext ctx) {
            NetHandlerPlayServer handler = ctx.getServerHandler();
            EntityPlayer player = handler.player;
            Entity entity = handler.server.getEntityFromUuid(message.uuid);
            if (entity instanceof TameableDragonEntity) {
                TameableDragonEntity dragon = (TameableDragonEntity) entity;
                World world = player.world;
                if (!dragon.isOwner(player) || !world.isBlockLoaded(player.getPosition())) {
                    player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.whistle.failed"), true);
                    return null;
                }
                //Get block pos by raytracing from player for dragon teleport
                Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                float pitch = message.pitch * -0.017453292F;
                float yaw = message.yaw * -0.017453292F - (float) Math.PI;
                float forward = -MathHelper.cos(pitch);
                Vec3d end = start.add(
                        MathHelper.sin(yaw) * forward * 5.0,
                        MathHelper.sin(pitch) * 5.0,
                        MathHelper.cos(yaw) * forward * 5.0
                );
                RayTraceResult hit = world.rayTraceBlocks(start, end, true);
                if (hit == null) {
                    player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.whistle.invalidPos"), true);
                    return null; //suppress null block pos warnings
                }
                if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos pos = hit.getBlockPos();
                    dragon.getNavigator().clearPath();
                    dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    world.playSound(null, player.posX, player.posY, player.posZ, DMSounds.DRAGON_WHISTLE, SoundCategory.NEUTRAL, 1, 1);
                }
            } else {
                player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.whistle.failed"), true);
            }
            return null;
        }
    }
}