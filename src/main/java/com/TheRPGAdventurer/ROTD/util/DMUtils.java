package com.TheRPGAdventurer.ROTD.util;

import com.TheRPGAdventurer.ROTD.dragonmounts.Tags;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class DMUtils {
    public static final Object[] NO_ARGS = new Object[0];

    private static Logger logger;

    public static Logger getLogger() {
        if (logger == null) {
            logger = LogManager.getFormatterLogger(Tags.MOD_ID);
        }
        return logger;
    }

    public static String translateToLocal(String s) {
        return I18n.format(s, NO_ARGS);
    }

    public static boolean hasEquipped(EntityPlayer player, Item item) {
        ItemStack stack = player.getHeldItemMainhand();
        return !stack.isEmpty() && stack.getItem() == item;
    }

    /**
     * @return True if fish is found in player hand
     * @WolfShotz Checks if held item is any kind of Fish (Registered under listAllfishraw in OreDict)
     * This allows other mods' fishes to be used with dragon taming
     */
    public static boolean hasEquippedOreDicFish(EntityPlayer player) {
        ItemStack stack = player.getHeldItemMainhand();
        return !stack.isEmpty() && OreDictionary.getOres("listAllfishraw").stream().anyMatch(stack::isItemEqualIgnoreDurability);
    }

    /**
     * Checks if a player has items equipped that can be used with a right-click.
     * Typically applies for weapons, food and tools.
     *
     * @param player player to check
     * @return true if the player has an usable item equipped
     */
    public static boolean hasEquippedUsable(EntityPlayer player) {
        ItemStack stack = player.getHeldItemMainhand();
        return !stack.isEmpty() && stack.getItemUseAction() != EnumAction.NONE;
    }

    /**
     * taken from stackoverflow
     *
     * @param rnd
     * @param start
     * @param end
     * @param exclude
     * @return
     */
    public static int getRandomWithExclusionstatic(Random rnd, int start, int end, int... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }

    /**
     * taken from stackoverflow
     *
     * @param rnd
     * @param start
     * @param end
     * @param exclude
     * @return
     */
    public int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
}