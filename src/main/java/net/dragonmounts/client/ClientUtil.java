package net.dragonmounts.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * to avoid {@link ClassNotFoundException}
 */
public abstract class ClientUtil {
    public static EntityPlayer getLocalPlayer() {
        return Minecraft.getMinecraft().player;
    }

    private ClientUtil() {}
}
