package net.dragonmounts.network;

import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.init.DMSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class CFollowOrderPacket extends CUUIDPacket {
    public CFollowOrderPacket() {}

    public CFollowOrderPacket(UUID uuid) {
        super(uuid);
    }

    public IMessage handle(MessageContext context) {
        NetHandlerPlayServer handler = context.getServerHandler();
        Entity entity = handler.server.getEntityFromUuid(this.uuid);
        if (entity instanceof ServerDragonEntity) {
            ServerDragonEntity dragon = (ServerDragonEntity) entity;
            EntityPlayer player = handler.player;
            if (dragon.dimension == player.dimension && Relation.checkRelation(dragon, player).isTrusted) {
                dragon.followOwner = !dragon.followOwner;
                player.world.playSound(null, player.posX, player.posY, player.posZ, DMSounds.FLUTE_BLOW_SHORT, SoundCategory.PLAYERS, 1, 1);
                return null;
            }
        }
        handler.player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.flute.failed"), true);
        return null;
    }
}
