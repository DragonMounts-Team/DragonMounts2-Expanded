package net.dragonmounts.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

public class ItemUtil {
    public static boolean anyMatches(ItemStack stack, String... names) {
        if (stack.isEmpty()) return false;
        int[] ores = OreDictionary.getOreIDs(stack);
        int len = ores.length, match = names.length;
        while (--match >= 0) {
            int id = OreDictionary.getOreID(names[match]);
            int i = len;
            while (--i >= 0) {
                if (ores[i] == id) return true;
            }
        }
        return false;
    }

    public static NBTTagList writeToNBT(ItemStack[] stacks) {
        NBTTagList list = new NBTTagList();
        for (byte i = 0, j = (byte) stacks.length; i < j; i++) {
            ItemStack stack = stacks[i];
            if (stack == null || stack.isEmpty()) continue;
            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);
            tag.setByte("Slot", i);
            list.appendTag(tag);
        }
        return list;
    }

    public static void readFromNBT(ItemStack[] stacks, NBTTagList list) {
        for (int i = 0, size = list.tagCount(); i < size; ++i) {
            NBTTagCompound stack = list.getCompoundTagAt(i);
            int slot = stack.getByte("Slot");
            if (slot < 0 || slot >= stacks.length) continue;
            stacks[slot] = new ItemStack(stack);
        }
    }
}
