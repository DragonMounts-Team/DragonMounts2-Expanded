package net.dragonmounts.util;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class DMUtils {
    /**
     * // 20 (ticks/real life second) * 60 (seconds / min) * 20 (real life minutes per minecraft day) / 24 (hours/day)
     */
    public static final int TICKS_PER_MINECRAFT_HOUR = 20 * 60 * 20 / 24;
    public static final Object[] NO_ARGS = new Object[0];

    public static String makeDescriptionId(String type, @Nullable ResourceLocation id) {
        return id == null ? type + ".unregistered_sadface" : type + '.' + id.getNamespace() + '.' + id.getPath().replace('/', '.');
    }
}