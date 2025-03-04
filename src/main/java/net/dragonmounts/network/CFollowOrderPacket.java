package net.dragonmounts.network;

import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class CFollowOrderPacket extends CUUIDPacket {
    public CFollowOrderPacket() {}

    public CFollowOrderPacket(UUID uuid) {
        super(uuid);
    }

    public static class Handler implements IMessageHandler<CFollowOrderPacket, IMessage> {
        @Override
        public IMessage onMessage(CFollowOrderPacket message, MessageContext ctx) {
            Entity entity = ctx.getServerHandler().server.getEntityFromUuid(message.uuid);
            if (entity instanceof TameableDragonEntity) {
                TameableDragonEntity dragon = (TameableDragonEntity) entity;
                EntityPlayer player = ctx.getServerHandler().player;
                if (Relation.checkRelation(dragon, player) != Relation.STRANGER) {
                    dragon.followOwner = !dragon.followOwner;
                    player.world.playSound(null, player.posX, player.posY, player.posZ, DMSounds.WHISTLE_BLOW, SoundCategory.PLAYERS, 1, 1);
                    return null;
                }
            }
            ctx.getServerHandler().player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.whistle.failed"), true);
            return null;
        }
    }
}
