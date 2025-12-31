package net.dragonmounts.compat;

import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.dragonmounts.registry.CooldownCategory;
import net.minecraft.item.Item;

@Deprecated
public abstract class CooldownOverlayCompat {
    public static void register(CooldownCategory category, Item item) {
        category.registerItem(item);
    }

    public static ReferenceSet<Item> getItems(CooldownCategory category) {
        return category.getItems();
    }

    private CooldownOverlayCompat() {}
}
