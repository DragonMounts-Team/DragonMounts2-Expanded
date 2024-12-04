package net.dragonmounts.item;

import net.dragonmounts.registry.DragonType;
import net.minecraft.item.Item;

public class DragonAmuletItem extends Item {
    public final DragonType type;

    public DragonAmuletItem(DragonType type) {
        this.type = type;
    }
}
