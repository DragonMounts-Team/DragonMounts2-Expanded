package top.dragonmounts.api;

import top.dragonmounts.capability.IArmorEffectManager;
import net.minecraft.entity.player.EntityPlayer;

@FunctionalInterface
public interface IArmorEffect {
    boolean activate(IArmorEffectManager manager, EntityPlayer player, int level);
}
