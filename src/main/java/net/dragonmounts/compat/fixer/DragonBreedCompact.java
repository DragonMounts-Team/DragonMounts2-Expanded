package net.dragonmounts.compat.fixer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class DragonBreedCompact implements IFixableData {
    @Override
    public int getFixVersion() {
        return 0;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound tag) {
        return tag;
    }
}
