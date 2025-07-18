package net.dragonmounts.compat;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.IStringSerializable;

public enum DragonTypeCompat implements IStringSerializable {
    AETHER(DragonTypes.AETHER),
    FIRE(DragonTypes.FIRE),
    FOREST(DragonTypes.FOREST),
    SYLPHID(DragonTypes.WATER),
    ICE(DragonTypes.ICE),
    END(DragonTypes.ENDER),
    NETHER(DragonTypes.NETHER),
    SKELETON(DragonTypes.SKELETON),
    WITHER(DragonTypes.WITHER),
    ENCHANT(DragonTypes.ENCHANTED),
    SUNLIGHT(DragonTypes.SUNLIGHT),
    STORM(DragonTypes.STORM),
    ZOMBIE(DragonTypes.ZOMBIE),
    TERRA(DragonTypes.TERRA),
    MOONLIGHT(DragonTypes.MOONLIGHT);

    public static final Object2ObjectMap<String, DragonType> MAPPING;

    public static DragonType byId(int id) {
        DragonTypeCompat[] types = DragonTypeCompat.values();
        return id < 0 || id >= types.length ? DragonTypes.ENDER : types[id].type;
    }

    public final DragonType type;
    public final String identifier;

    DragonTypeCompat(DragonType type) {
        this.type = type;
        this.identifier = this.name().toLowerCase();
    }

    @Override
    public String getName() {
        return this.identifier;
    }

    static {
        Object2ObjectOpenHashMap<String, DragonType> mapping = new Object2ObjectOpenHashMap<>();
        for (DragonTypeCompat type : values()) {
            mapping.put(type.identifier, type.type);
        }
        MAPPING = Object2ObjectMaps.unmodifiable(mapping);
    }
}
