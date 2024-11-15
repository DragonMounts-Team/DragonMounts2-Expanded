package top.dragonmounts.registry;

import net.minecraftforge.registries.RegistryBuilder;

import static top.dragonmounts.DragonMounts.makeId;

public class CooldownCategory extends NumericIdentifiedEntry<CooldownCategory> {
    public static final Registry<CooldownCategory> REGISTRY = new Registry<>(
            makeId("cooldown_category"),
            CooldownCategory.class,
            new RegistryBuilder<>()
    );
}
