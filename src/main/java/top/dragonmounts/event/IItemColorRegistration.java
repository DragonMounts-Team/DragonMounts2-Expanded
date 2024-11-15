package top.dragonmounts.event;

import top.dragonmounts.inits.ModItems;
import top.dragonmounts.items.IItemDragonOrbColour;
import top.dragonmounts.objects.items.ItemDragonSpawner;
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
            return item instanceof ItemDragonSpawner ? (
                    tintIndex == 0 ? ((ItemDragonSpawner) item).backgroundColor : ((ItemDragonSpawner) item).highlightColor
            ) : -1;
        }, ModItems.SpawnAether, ModItems.SpawnEnchant, ModItems.SpawnEnd, ModItems.SpawnFire, ModItems.SpawnForest, ModItems.SpawnIce, ModItems.SpawnMoonlight, ModItems.SpawnNether, ModItems.SpawnSkeleton, ModItems.SpawnStorm, ModItems.SpawnSunlight, ModItems.SpawnTerra, ModItems.SpawnWater, ModItems.SpawnWither, ModItems.SpawnZombie);

        colors.registerItemColorHandler(new IItemDragonOrbColour(), ModItems.dragon_orb);
    }
}
