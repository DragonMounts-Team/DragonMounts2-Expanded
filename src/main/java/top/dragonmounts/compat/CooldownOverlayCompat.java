package top.dragonmounts.compat;

import top.dragonmounts.registry.CooldownCategory;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import net.minecraft.item.Item;

public abstract class CooldownOverlayCompat {
    private static final Reference2ObjectOpenHashMap<CooldownCategory, ReferenceOpenHashSet<Item>> REGISTRY = new Reference2ObjectOpenHashMap<>();

    public static void register(CooldownCategory category, Item item) {
        REGISTRY.computeIfAbsent(category, $ -> new ReferenceOpenHashSet<>()).add(item);
    }

    public static ReferenceSet<Item> getItems(CooldownCategory category) {
        ReferenceOpenHashSet<Item> items = REGISTRY.get(category);
        return items == null ? ReferenceSets.emptySet() : ReferenceSets.unmodifiable(items);
    }

    private CooldownOverlayCompat() {}
}
