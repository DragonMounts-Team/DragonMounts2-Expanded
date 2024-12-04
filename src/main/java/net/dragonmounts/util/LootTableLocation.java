package net.dragonmounts.util;

import net.minecraft.util.ResourceLocation;

/**
 * Define a class so that it can be used as a key in {@link net.dragonmounts.registry.DragonType#bindInstance(Class, Object)} and {@link net.dragonmounts.registry.DragonType#getInstance(Class, Object)}
 */
public class LootTableLocation extends ResourceLocation {
    public LootTableLocation(String namespace, String path) {
        super(0, namespace, path);
    }
}
