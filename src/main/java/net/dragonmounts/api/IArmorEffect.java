package net.dragonmounts.api;

import net.dragonmounts.capability.IArmorEffectManager;
import net.minecraft.entity.player.EntityPlayer;

@FunctionalInterface
public interface IArmorEffect {
    boolean activate(IArmorEffectManager manager, EntityPlayer player, int level);
}
