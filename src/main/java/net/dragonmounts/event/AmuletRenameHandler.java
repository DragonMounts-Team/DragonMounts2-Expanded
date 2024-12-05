package net.dragonmounts.event;

import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.item.DragonAmuletItem;
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
        if (!DragonMountsConfig.forcedRename) return;
        ItemStack output = event.getItemResult();
        if (!output.isEmpty() && output.getItem() instanceof DragonAmuletItem) {
            NBTTagCompound root = output.getTagCompound();
            if (root == null) return;
            String result = getDisplayName(root);
            if (result.equals(getDisplayName(event.getItemInput().getTagCompound()))) return;
            NBTTagCompound data = root.getCompoundTag("EntityTag");
            if (result.isEmpty()) {
                if (!data.getString("CustomName").isEmpty()) {
                    data.removeTag("CustomName");
                    root.setString("LocName", "entity.dragon." + ((DragonAmuletItem) output.getItem()).type.identifier);
                }
            } else {
                data.setString("CustomName", result);
            }
        }
    }
}
