package net.dragonmounts.compat;

import baubles.api.BaubleType;
import baubles.api.cap.BaublesCapabilities;
import net.dragonmounts.item.DragonAmuletItem;

public abstract class BaublesCompat {
    public static void load() {
        DragonAmuletItem.registerCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, stack -> BaubleType.AMULET);
    }

    private BaublesCompat() {}
}
