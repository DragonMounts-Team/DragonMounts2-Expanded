package net.dragonmounts.util;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class DMUtils {
    /**
     * // 20 (ticks/real life second) * 60 (seconds / min) * 20 (real life minutes per minecraft day) / 24 (hours/day)
     */
    public static final int TICKS_PER_MINECRAFT_HOUR = 20 * 60 * 20 / 24;
    public static final Object[] NO_ARGS = new Object[0];

    public static String makeDescriptionId(String type, @Nullable ResourceLocation id) {
        return id == null ? type + ".unregistered_sadface" : type + '.' + id.getNamespace() + '.' + id.getPath().replace('/', '.');
    }

    public static <T> T[] makeArray(T[] array, Supplier<T> factory) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = factory.get();
        }
        return array;
    }
}