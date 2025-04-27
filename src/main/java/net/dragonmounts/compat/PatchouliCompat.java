package net.dragonmounts.compat;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import static net.dragonmounts.DragonMountsTags.MOD_ID;

public abstract class PatchouliCompat {
    public static void grantGuideBook(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) return;
        EntityPlayerMP $player = (EntityPlayerMP) player;
        Advancement advancement = $player.getServerWorld()
                .getAdvancementManager()
                .getAdvancement(new ResourceLocation(MOD_ID, "grant_guide_book"));
        if (advancement == null) return;
        PlayerAdvancements advancements = $player.getAdvancements();
        AdvancementProgress progress = advancements.getProgress(advancement);
        if (progress.isDone()) return;
        Item item = Item.REGISTRY.getObject(new ResourceLocation("patchouli", "guide_book"));
        if (item == null) return;
        ItemStack book = new ItemStack(item);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("patchouli:book", "dragonmounts:guide_book");
        book.setTagCompound(tag);
        if (!$player.addItemStackToInventory(book)) {
            EntityItem dropped = $player.dropItem(book, false);
            if (dropped != null) {
                dropped.setNoPickupDelay();
                dropped.setOwner($player.getName());
            }
        }
        for (String criteria : progress.getRemaningCriteria()) {
            advancements.grantCriterion(advancement, criteria);
        }
    }

    private PatchouliCompat() {}
}
