package net.dragonmounts.compat.fixer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.datafix.IFixableData;

public class DragonNestCompat implements IFixableData {
    @Override
    public int getFixVersion() {
        return 0;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound tag) {
        if (tag.hasKey("palette", 9)) {
            NBTTagList states = tag.getTagList("palette", 10);
            for (int i = 0, len = states.tagCount(); i < len; ++i) {
                NBTTagCompound state = (NBTTagCompound) states.get(i);
                if ("dragonmounts:pileofsticks".equals(state.getString("Name"))) {
                    state.setString("Name", "dragonmounts:dragon_nest");
                }
            }
        }
        return tag;
    }
}
