package net.dragonmounts.util;

import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static net.dragonmounts.DragonMountsTags.MOD_ID;

public class DMUtils {
    /**
     * 20 (ticks/real life second) * 60 (seconds / min) * 20 (real life minutes per minecraft day) / 24 (hours/day)
     */
    public static final int TICKS_PER_MINECRAFT_HOUR = 20 * 60 * 20 / 24;

    public static <T> T nil() {
        return null;
    }

    public static String makeDescriptionId(String type, @Nullable ResourceLocation id) {
        return id == null
                ? type + ".unregistered_sadface"
                : type + '.' + id.getNamespace() + '.' + id.getPath().replace('/', '.');
    }

    public static <T> T[] fillArray(T[] array, Supplier<? extends T> factory) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = factory.get();
        }
        return array;
    }

    public static <T extends Slot> T applyBackground(T slot, String name) {
        slot.setBackgroundName(name);
        return slot;
    }

    public static ResourceLocation parseIdentifier(String identifier) {
        String[] result = new String[]{MOD_ID, identifier};
        int i = identifier.indexOf(':');
        if (i >= 0) {
            result[1] = identifier.substring(i + 1);
            if (i != 0) {
                result[0] = identifier.substring(0, i);
            }
        }
        return new ResourceLocation(result[0], result[1]);
    }
}