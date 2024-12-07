package net.dragonmounts.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ITestCase {
    boolean run(World level, EntityPlayer player, ItemStack stack);
}