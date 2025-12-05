package net.dragonmounts.util;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.dragonmounts.DragonMountsTags;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

public class ItemUtil {
    public static float isUsingItem(ItemStack stack, @Nullable World ignored, @Nullable EntityLivingBase holder) {
        return holder != null && holder.isHandActive() && holder.getActiveItemStack() == stack ? 1.0F : 0.0F;
    }

    public static boolean anyMatches(ItemStack stack, String... names) {
        if (stack.isEmpty()) return false;
        IntOpenHashSet tags = new IntOpenHashSet();
        for (String name : names) {
            tags.add(OreDictionary.getOreID(name));
        }
        for (int tag : OreDictionary.getOreIDs(stack)) {
            if (tags.contains(tag)) return true;
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

    public static Item localize(Item item, String name) {
        return item.setTranslationKey(DragonMountsTags.TRANSLATION_KEY_PREFIX + name);
    }
}
