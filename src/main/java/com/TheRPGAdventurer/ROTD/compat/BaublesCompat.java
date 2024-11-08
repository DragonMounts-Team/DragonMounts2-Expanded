package com.TheRPGAdventurer.ROTD.compat;

import baubles.api.BaubleType;
import baubles.api.cap.BaublesCapabilities;
import com.TheRPGAdventurer.ROTD.objects.items.ItemDragonAmuletNEW;

public abstract class BaublesCompat {
    public static void load() {
        ItemDragonAmuletNEW.registerCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, stack -> BaubleType.AMULET);
    }

    private BaublesCompat() {}
}
