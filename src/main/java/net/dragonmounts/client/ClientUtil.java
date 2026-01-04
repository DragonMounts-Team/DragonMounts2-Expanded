package net.dragonmounts.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

@SideOnly(Side.CLIENT)
public abstract class ClientUtil {
    /// to avoid {@link ClassNotFoundException}
    public static EntityPlayer getLocalPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public static String translateToLocal(String key) {
        return I18n.format(key, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    public static String translateBothToLocal(String major, String minor) {
        return I18n.format(major, I18n.format(minor, ArrayUtils.EMPTY_OBJECT_ARRAY));
    }

    /**
     * @param value raw time (in ticks)
     * @return formatted time (in seconds)
     */
    public static String quickFormatAsFloat(String key, int value) {
        if (value < 19) return I18n.format(key, "0." + ((value + 1) >> 1));
        StringBuilder builder = new StringBuilder().append((value + 1) >> 1);//value: ticks
        builder.append(builder.charAt(value = builder.length() - 1)).setCharAt(value, '.');//value: index
        return I18n.format(key, builder.toString());
    }

    private ClientUtil() {}
}
