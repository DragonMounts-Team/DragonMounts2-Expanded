package net.dragonmounts.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

public class ItemUtil {
    public static boolean anyMatches(ItemStack stack, int... ore) {
        if (stack.isEmpty()) return false;
        int len = ore.length;
        int[] ores = OreDictionary.getOreIDs(stack);
        int i = ores.length;
        while (--i >= 0) {
            int v = ores[i];
            int j = len;
            while (--j >= 0) {
                if (v == ore[j]) {
                    return true;
                }
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
            if (slot < 0 || slot >= size) continue;
            stacks[slot] = new ItemStack(stack);
        }
    }
}
