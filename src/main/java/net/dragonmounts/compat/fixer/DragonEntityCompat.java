package net.dragonmounts.compat.fixer;

import net.dragonmounts.util.DMUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class DragonEntityCompat implements IFixableData {
    @Override
    public int getFixVersion() {
        return 0;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound tag) {
        if (!tag.getString("id").equals("dragonmounts:dragon")) return tag;
        DMUtils.getLogger().info(tag.toString());
        return tag;
    }
}
