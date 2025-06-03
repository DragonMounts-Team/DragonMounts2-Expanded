package net.dragonmounts.world;

import com.google.common.base.Functions;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum NestPlacement implements IStringSerializable {
    ON_LAND_SURFACE("on_land_surface"),
    PARTLY_BURIED("partly_buried"),
    ON_OCEAN_FLOOR("on_ocean_floor"),
    IN_MOUNTAIN("in_mountain"),
    UNDERGROUND("underground"),
    IN_CLOUDS("in_clouds"),
    IN_NETHER("in_nether");
    private static final Map<String, NestPlacement> BY_NAME =
            Arrays.stream(values()).collect(Collectors.toMap(NestPlacement::getName, Functions.identity()));

    public static @Nullable NestPlacement byName(String name) {
        return BY_NAME.get(name);
    }

    public final String name;

    NestPlacement(final String name) {
        this.name = name;
    }

    @Override
    public @Nonnull String getName() {
        return this.name;
    }
}
