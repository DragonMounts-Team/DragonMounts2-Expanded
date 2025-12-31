package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.entity.ServerDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class COpenInventoryPacket implements IMessage {
    @Override
    public void fromBytes(ByteBuf buffer) {}

    @Override
    public void toBytes(ByteBuf buffer) {}

    public IMessage handle(MessageContext context) {
        EntityPlayerMP player = context.getServerHandler().player;
        Entity vehicle = player.getRidingEntity();
        if (vehicle instanceof ServerDragonEntity) {
            ((ServerDragonEntity) vehicle).openInventory(player);
        }
        return null;
    }
}