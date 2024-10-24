package com.TheRPGAdventurer.ROTD.api;

import com.TheRPGAdventurer.ROTD.capability.IArmorEffectManager;
import net.minecraft.entity.player.EntityPlayer;

@FunctionalInterface
public interface IArmorEffect {
    boolean activate(IArmorEffectManager manager, EntityPlayer player, int level);
}
