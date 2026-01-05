package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.capability.ArmorEffectManager;
import net.dragonmounts.registry.CooldownCategory;
import net.minecraft.item.Item;
import net.minecraft.util.CooldownTracker;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import static net.dragonmounts.util.ByteBufferUtil.readVarInt;
import static net.dragonmounts.util.ByteBufferUtil.writeVarInt;

public class SSyncCooldownPacket implements IMessage, Runnable {
    public int id;
    public int cd;

    public SSyncCooldownPacket() {
        this.id = -1;
        this.cd = 0;
    }

    public SSyncCooldownPacket(int id, int cd) {
        this.id = id;
        this.cd = cd;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.id = readVarInt(buffer);
        this.cd = readVarInt(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        writeVarInt(buffer, this.id);
        writeVarInt(buffer, this.cd);
    }

    @Override
    public void run() {
        CooldownCategory category = CooldownCategory.REGISTRY.getValue(this.id);
        if (category == null) return;
        ArmorEffectManager manager = ArmorEffectManager.getLocal();
        if (manager == null) return;
        int cd = this.cd;
        manager.setCooldown(category, cd);
        CooldownTracker vanilla = manager.player.getCooldownTracker();
        for (Item item : category.getItems()) {
            vanilla.setCooldown(item, cd);
        }
    }
}
