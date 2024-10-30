package com.TheRPGAdventurer.ROTD.event;

import com.TheRPGAdventurer.ROTD.objects.items.ItemDragonAmuletNEW;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class AmuletRenameHandler {
    private static String getDisplayName(NBTTagCompound root) {
        return root == null ? "" : root.getCompoundTag("display").getString("Name");
    }

    @SubscribeEvent
    public static void onTake(AnvilRepairEvent event) {
        ItemStack output = event.getItemResult();
        if (!output.isEmpty() && output.getItem() instanceof ItemDragonAmuletNEW) {
            NBTTagCompound root = output.getTagCompound();
            if (root == null) return;
            String breed = root.getString("breed");
            if (breed.isEmpty()) return;
            String result = getDisplayName(root);
            if (result.equals(getDisplayName(event.getItemInput().getTagCompound()))) return;
            if (result.isEmpty()) {
                if (!root.getString("CustomName").isEmpty()) {
                    root.removeTag("CustomName");
                    root.removeTag("Name");
                    root.setString("LocName", "entity.DragonMount." + breed + ".name");
                }
            } else {
                root.setString("CustomName", result);
            }
        }
    }
}
