package top.dragonmounts.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ClientUtil {
    /**
     * to avoid {@link ClassNotFoundException}
     */
    public static EntityPlayer getLocalPlayer() {
        return Minecraft.getMinecraft().player;
    }
}
