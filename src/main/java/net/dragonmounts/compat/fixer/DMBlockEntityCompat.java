package net.dragonmounts.compat.fixer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class DMBlockEntityCompat implements IFixableData {
    @Override
    public int getFixVersion() {
        return 0;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound tag) {
        if (tag.getString("id").equals("dragonmounts:dragon_shulker")) {
            tag.setString("id", "dragonmounts:dragon_core");
        }
        return tag;
    }
}
