package net.dragonmounts.event;

import net.dragonmounts.init.DMItems;
import net.dragonmounts.inits.ModItems;
import net.dragonmounts.item.DragonSpawnEggItem;
import net.dragonmounts.items.IItemDragonOrbColour;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by TGG on 29/05/2019.
 */
public class IItemColorRegistration {
    @SubscribeEvent
    public static void registerItemHandlers(ColorHandlerEvent.Item event) {
        // Dragon Whistle String Color
        ItemColors colors = event.getItemColors();
        colors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 1 && stack.hasTagCompound() && stack.getTagCompound().hasKey("Color"))
                return stack.getTagCompound().getInteger("Color");
            return 0xFFFFFF;
        }, ModItems.dragon_whistle);

        colors.registerItemColorHandler((stack, tintIndex) -> {
            Item item = stack.getItem();
            return item instanceof DragonSpawnEggItem ? (
                    tintIndex == 0 ? ((DragonSpawnEggItem) item).backgroundColor : ((DragonSpawnEggItem) item).highlightColor
            ) : -1;
        }, DMItems.AETHER_DRAGON_SPAWN_EGG, DMItems.ENCHANT_DRAGON_SPAWN_EGG, DMItems.ENDER_DRAGON_SPAWN_EGG, DMItems.FIRE_DRAGON_SPAWN_EGG, DMItems.FOREST_DRAGON_SPAWN_EGG, DMItems.ICE_DRAGON_SPAWN_EGG, DMItems.MOONLIGHT_DRAGON_SPAWN_EGG, DMItems.NETHER_DRAGON_SPAWN_EGG, DMItems.SKELETON_DRAGON_SPAWN_EGG, DMItems.STORM_DRAGON_SPAWN_EGG, DMItems.SUNLIGHT_DRAGON_SPAWN_EGG, DMItems.TERRA_DRAGON_SPAWN_EGG, DMItems.WATER_DRAGON_SPAWN_EGG, DMItems.WITHER_DRAGON_SPAWN_EGG, DMItems.ZOMBIE_DRAGON_SPAWN_EGG);

        colors.registerItemColorHandler(new IItemDragonOrbColour(), ModItems.dragon_orb);
    }
}
