package net.dragonmounts.compat.fixer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
            NBTTagCompound stack = null;
            if (!tag.hasKey("Item", 10)) {
                NBTTagList list = tag.getTagList("Items", 10);
                for (int i = 0; i < list.tagCount(); ++i) {
                    stack = list.getCompoundTagAt(i);
                    if ((stack.getByte("Slot") & 255) == 0) {
                        stack.removeTag("Slot");
                        tag.setTag("Item", stack);
                        stack = null;
                    }
                }
                if (stack != null) {
                    stack.removeTag("Slot");
                    tag.setTag("Item", stack);
                }
            }
        }
        return tag;
    }
}
