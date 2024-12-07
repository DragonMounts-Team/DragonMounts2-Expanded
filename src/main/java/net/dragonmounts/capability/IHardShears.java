package net.dragonmounts.capability;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IHardShears {
    /**
     * @return cooldown for next shear
     */
    int onShear(ItemStack stack, EntityPlayer player, TameableDragonEntity dragon);
}
