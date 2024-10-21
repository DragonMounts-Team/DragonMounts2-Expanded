package com.TheRPGAdventurer.ROTD.registry;

import net.minecraftforge.registries.RegistryBuilder;

import static com.TheRPGAdventurer.ROTD.DragonMounts.makeId;

public class CooldownCategory extends NumericIdentifiedEntry<CooldownCategory> {
    public static final Registry<CooldownCategory> REGISTRY = new Registry<>(
            makeId("cooldown_category"),
            CooldownCategory.class,
            new RegistryBuilder<>()
    );
}
