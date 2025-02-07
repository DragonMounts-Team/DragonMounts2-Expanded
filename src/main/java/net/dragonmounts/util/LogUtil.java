package net.dragonmounts.util;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.dragonmounts.DragonMountsTags;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {
    private static final ObjectOpenHashSet<String> LOGGED = new ObjectOpenHashSet<>();
    public static final Logger LOGGER = LogManager.getLogger(DragonMountsTags.MOD_ID);

    public static void once(Level level, String message) {
        once(level, message, message);
    }

    public static void once(Level level, String message, String key) {
        if (LOGGED.contains(key)) return;
        LOGGED.add(key);
        LOGGER.log(level, message);
    }
}
