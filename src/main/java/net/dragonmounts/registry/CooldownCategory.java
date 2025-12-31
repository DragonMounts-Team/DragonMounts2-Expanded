package net.dragonmounts.registry;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.item.Item;
import net.minecraftforge.registries.RegistryBuilder;

import static net.dragonmounts.DragonMounts.makeId;

public class CooldownCategory extends NumericIdentifiedEntry<CooldownCategory> {
    public static final Registry<CooldownCategory> REGISTRY = new Registry<>(
            makeId("cooldown_category"),
            CooldownCategory.class,
            new RegistryBuilder<>()
    );

    private final ReferenceOpenHashSet<Item> items = new ReferenceOpenHashSet<>();

    public void registerItem(Item item) {
        this.items.add(item);
    }

    public ReferenceSet<Item> getItems() {
        return this.items;
    }
}
