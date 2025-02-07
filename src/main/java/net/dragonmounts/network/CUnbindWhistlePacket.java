package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.init.DMItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class CUnbindWhistlePacket implements IMessage {
    public EntityEquipmentSlot slot;

    public CUnbindWhistlePacket() {}

    public CUnbindWhistlePacket(@Nullable EnumHand hand) {
        this.slot = hand == EnumHand.OFF_HAND ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.slot = buf.readBoolean() ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.slot == EntityEquipmentSlot.OFFHAND);
    }

    public static class Handler implements IMessageHandler<CUnbindWhistlePacket, IMessage> {
        @Override
        public IMessage onMessage(CUnbindWhistlePacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            ItemStack stack = player.getItemStackFromSlot(message.slot);
            if (!stack.isEmpty() && stack.getItem() == DMItems.DRAGON_WHISTLE) {
                stack.setTagCompound(null);
                player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.whistle.cleared"), true);
            }
            return null;
        }
    }
}
