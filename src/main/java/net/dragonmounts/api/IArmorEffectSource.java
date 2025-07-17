package net.dragonmounts.api;

import net.dragonmounts.capability.IArmorEffectManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

// TODO: capability
public interface IArmorEffectSource {
    void affect(IArmorEffectManager manager, EntityPlayer player, ItemStack stack);
}
