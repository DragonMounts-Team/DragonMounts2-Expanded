package net.dragonmounts.compat;

import net.minecraft.nbt.NBTTagCompound;

public abstract class FixerCompat {
    public static void disableEntityFixers(NBTTagCompound entity) {
        if (entity.hasKey("ForgeData")) {
            NBTTagCompound compound = entity.getCompoundTag("ForgeData");
            compound.removeTag("Potion Core - Health Fix");
            if (compound.isEmpty()) {
                entity.removeTag("ForgeData");
            }
        }
        if (entity.hasKey("SurgeAABB")) {
            entity.removeTag("SurgeAABB");
        }
    }

    private FixerCompat() {}
}
