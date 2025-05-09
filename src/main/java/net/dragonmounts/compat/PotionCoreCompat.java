package net.dragonmounts.compat;

import net.minecraft.nbt.NBTTagCompound;

public abstract class PotionCoreCompat {
    public static void fixEntityData(NBTTagCompound entity) {
        if (entity.hasKey("ForgeData")) {
            entity.getCompoundTag("ForgeData").removeTag("Potion Core - Health Fix");
        }
    }

    private PotionCoreCompat() {}
}
