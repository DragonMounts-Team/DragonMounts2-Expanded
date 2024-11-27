package net.dragonmounts.objects.entity.entitytameabledragon.breeds;

import net.dragonmounts.util.EnumSerializer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.IStringSerializable;

import java.util.function.Function;
import java.util.function.Supplier;

public enum EnumDragonBreed implements IStringSerializable {
    AETHER(0, DragonBreedAir::new),
    FIRE(1, DragonBreedFire::new),
    FOREST(2, DragonBreedForest::new),
    SYLPHID(3, DragonBreedWater::new),
    ICE(4, DragonBreedIce::new),
    END(5, DragonBreedEnd::new),
    NETHER(6, DragonBreedNether::new),
    SKELETON(7, DragonBreedSkeleton::new),
    WITHER(8, DragonBreedWither::new),
    ENCHANT(9, DragonBreedEnchant::new),
    SUNLIGHT(10, DragonBreedSunlight::new),
    STORM(11, DragonBreedStorm::new),
    ZOMBIE(12, DragonBreedZombie::new),
    TERRA(13, DragonBreedTerra::new),
    MOONLIGHT(14, DragonBreedMoonlight::new);
//	LIGHT(15, DragonBreedLight::new);
//	DARK(16, DragonBreedDark::new);
//	SPECTER(17, DragonBreedSpecter::new);

    public static final EnumSerializer<EnumDragonBreed> SERIALIZER = new EnumSerializer<>(EnumDragonBreed.class, EnumDragonBreed.END);
    public static final Function<String, EnumDragonBreed> NAME_MAPPING;
    private static final Int2ObjectOpenHashMap<EnumDragonBreed> META_MAPPING;

    public static EnumDragonBreed byMeta(int meta) {
        return META_MAPPING.get(meta);
    }

    private final DragonBreed breed;
    public final String identifier;

    // this field is used for block metadata and is technically the same as
    // ordinal(), but it is saved separately to make sure the values stay
    // constant after adding more breeds in unexpected orders
    public final int meta;

    EnumDragonBreed(int meta, Supplier<DragonBreed> factory) {
        this.breed = factory.get();
        this.meta = meta;
        this.identifier = this.name().toLowerCase();
    }


    public DragonBreed getBreed() {
        return breed;
    }

    public int getMeta() {
        return meta;
    }

    @Override
    public String getName() {
        return this.identifier;
    }

    public int getNumberOfNeckSegments() {
        return 7;
    }

    public int getNumberOfTailSegments() {
        return 12;
    }

    public int getNumberOfWingFingers() {
        return 4;
    }

    static {
        Object2ObjectOpenHashMap<String, EnumDragonBreed> name = new Object2ObjectOpenHashMap<>();
        Int2ObjectOpenHashMap<EnumDragonBreed> meta = new Int2ObjectOpenHashMap<>();
        for (EnumDragonBreed breed : EnumDragonBreed.values()) {
            name.put(breed.identifier, breed);
            meta.put(breed.meta, breed);
        }
        NAME_MAPPING = name::get;
        META_MAPPING = meta;
    }
}

