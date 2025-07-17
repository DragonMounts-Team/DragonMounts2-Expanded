package net.dragonmounts.capability;

import net.dragonmounts.api.IArmorEffect;
import net.dragonmounts.registry.CooldownCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IArmorEffectManager extends INBTSerializable<NBTTagCompound> {
    @SuppressWarnings("UnusedReturnValue")
    int stackLevel(IArmorEffect effect);

    @SuppressWarnings("UnusedReturnValue")
    int setLevel(IArmorEffect effect, int level);

    int getLevel(IArmorEffect effect, boolean filtered);

    boolean isActive(IArmorEffect effect);

    void setCooldown(CooldownCategory category, int cooldown);

    int getCooldown(CooldownCategory category);

    boolean isAvailable(CooldownCategory category);

    void tick();

    void sendInitPacket();
}
