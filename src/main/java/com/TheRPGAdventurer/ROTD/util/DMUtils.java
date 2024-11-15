package com.TheRPGAdventurer.ROTD.util;

import com.TheRPGAdventurer.ROTD.DragonMountsTags;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DMUtils {
    /**
     * // 20 (ticks/real life second) * 60 (seconds / min) * 20 (real life minutes per minecraft day) / 24 (hours/day)
     */
    public static final int TICKS_PER_MINECRAFT_HOUR = 20 * 60 * 20 / 24;
    public static final Object[] NO_ARGS = new Object[0];

    private static Logger logger;

    public static Logger getLogger() {
        if (logger == null) {
            logger = LogManager.getFormatterLogger(DragonMountsTags.MOD_ID);
        }
        return logger;
    }

    @SideOnly(Side.CLIENT)
    public static String translateToLocal(String key) {
        return I18n.format(key, NO_ARGS);
    }

    @SideOnly(Side.CLIENT)
    public static String translateToLocal(String key, NBTTagCompound fallbackSrc, String fallbackKey) {
        return I18n.hasKey(key) ? I18n.format(key, NO_ARGS) : fallbackSrc.getString(fallbackKey);
    }

    public static boolean isAir(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos);
    }

    /**
     * @param value raw time (in ticks)
     * @return formatted time (in seconds)
     */
    @SideOnly(Side.CLIENT)
    public static String quickFormatAsFloat(String key, int value) {
        if (value < 19) return I18n.format(key, "0." + ((value + 1) >> 1));
        StringBuilder builder = new StringBuilder().append((value + 1) >> 1);//value: ticks
        builder.append(builder.charAt(value = builder.length() - 1)).setCharAt(value, '.');//value: index
        return I18n.format(key, builder.toString());
    }

    @Deprecated
    public static boolean hasEquipped(EntityPlayer player, Item item) {
        ItemStack stack = player.getHeldItemMainhand();
        return !stack.isEmpty() && stack.getItem() == item;
    }

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

    public static BlockPos getSurface(World level, int x, int z) {
        return new BlockPos(x, level.getHeight(x, z), z);
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

    public static void dropItems(Entity entity, ItemStack[] stacks) {
        for (int i = 0, j = stacks.length; i < j; ++i) {
            if (entity.entityDropItem(stacks[i], 0.5F) != null) {
                stacks[i] = ItemStack.EMPTY;
            }
        }
    }
}