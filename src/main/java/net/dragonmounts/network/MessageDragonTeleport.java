package net.dragonmounts.network;

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
    public MessageDragonTeleport() {}

    public MessageDragonTeleport(UUID uuid) {
        super(uuid);
    }

    public static class MessageDragonTeleportHandler implements IMessageHandler<MessageDragonTeleport, IMessage> {
        @Override
        public IMessage onMessage(MessageDragonTeleport message, MessageContext ctx) {
            NetHandlerPlayServer handler = ctx.getServerHandler();
            Entity entity = handler.server.getEntityFromUuid(message.uuid);
            EntityPlayer player = handler.player;
            World world = player.world;
            if (entity instanceof TameableDragonEntity && world.isBlockLoaded(player.getPosition())) {
                TameableDragonEntity dragon = (TameableDragonEntity) entity;
                //Get block pos by raytracing from player for dragon teleport
                Vec3d start = new Vec3d(
                        player.prevPosX + (player.posX - player.prevPosX),
                        player.prevPosY + (player.posY - player.prevPosY) + 1.62D - player.getEyeHeight(),
                        player.prevPosZ + (player.posZ - player.prevPosZ)
                );
                float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * -0.017453292F - (float) Math.PI;
                float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * -0.017453292F;
                float forward = -MathHelper.cos(pitch);
                Vec3d end = start.add(
                        MathHelper.sin(yaw) * forward * 5.0,
                        MathHelper.sin(pitch) * 5.0,
                        MathHelper.cos(yaw) * forward * 5.0
                );
                RayTraceResult hit = world.rayTraceBlocks(start, end, true);
                if (hit == null) {
                    player.sendStatusMessage(new TextComponentTranslation("item.whistle.nullBlockPos"), true);
                    return null; //suppress null block pos warnings
                }
                if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos pos = hit.getBlockPos();
                    dragon.setPosition(pos.getX(), pos.getY() + 0.5, pos.getZ());
                    world.playSound(null, player.posX, player.posY, player.posZ, DMSounds.DRAGON_WHISTLE, SoundCategory.NEUTRAL, 1, 1);
                }
            }
            return null;
        }
    }
}