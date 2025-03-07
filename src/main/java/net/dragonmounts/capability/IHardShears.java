package net.dragonmounts.capability;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.ServerDragonEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IHardShears {
    /**
     * @return cooldown for next shear, 0 means failure
     */
    int onShear(ItemStack stack, EntityPlayer player, ServerDragonEntity dragon);

    /**
     * @return whether {@link #onShear} returns non-zero value
     */
    boolean canShear(ItemStack stack, EntityPlayer player, ClientDragonEntity dragon);
}
