package net.dragonmounts.event;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.init.DMKeyBindings;
import net.dragonmounts.item.DragonSpawnEggItem;
import net.dragonmounts.network.CDragonControlPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static net.dragonmounts.DragonMountsTags.MOD_ID;

public class ClientMisc {
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MOD_ID)) {
            DMConfig.load();
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;
        Entity vehicle = player.getRidingEntity();
        if (vehicle instanceof ClientDragonEntity) {
            if (DMKeyBindings.TOGGLE_CAMERA_POS.isPressed()) {
                CameraHandler.toggleCamera();
            }
            if (player != vehicle.getControllingPassenger()) return;
            ClientDragonEntity dragon = (ClientDragonEntity) vehicle;
            CDragonControlPacket packet = new CDragonControlPacket(
                    DMKeyBindings.KEY_BREATHE.isKeyDown(),
                    mc.gameSettings.keyBindSprint.isKeyDown(),
                    DMKeyBindings.KEY_DESCEND.isKeyDown(),
                    DMKeyBindings.TOGGLE_HOVERING.isPressed(),
                    DMKeyBindings.TOGGLE_YAW_ALIGNMENT.isPressed(),
                    DMKeyBindings.TOGGLE_PITCH_ALIGNMENT.isPressed()
            );
            if (dragon.controlFlags == packet.getFlags()) return;
            dragon.controlFlags = packet.getFlags();
            DragonMounts.NETWORK_WRAPPER.sendToServer(packet);
        }
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        // Dragon Whistle String Color
        ItemColors colors = event.getItemColors();
        colors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 1) {
                NBTTagCompound root = stack.getTagCompound();
                if (root != null && root.hasKey("Color")) {
                    return root.getInteger("Color");
                }
            }
            return 0xFFFFFF;
        }, DMItems.DRAGON_WHISTLE);

        colors.registerItemColorHandler((stack, tintIndex) -> {
            Item item = stack.getItem();
            return item instanceof DragonSpawnEggItem ? (
                    tintIndex == 0 ? ((DragonSpawnEggItem) item).backgroundColor : ((DragonSpawnEggItem) item).highlightColor
            ) : -1;
        }, DMItems.AETHER_DRAGON_SPAWN_EGG, DMItems.DARK_DRAGON_SPAWN_EGG, DMItems.ENCHANTED_DRAGON_SPAWN_EGG, DMItems.ENDER_DRAGON_SPAWN_EGG, DMItems.FIRE_DRAGON_SPAWN_EGG, DMItems.FOREST_DRAGON_SPAWN_EGG, DMItems.ICE_DRAGON_SPAWN_EGG, DMItems.MOONLIGHT_DRAGON_SPAWN_EGG, DMItems.NETHER_DRAGON_SPAWN_EGG, DMItems.SKELETON_DRAGON_SPAWN_EGG, DMItems.STORM_DRAGON_SPAWN_EGG, DMItems.SUNLIGHT_DRAGON_SPAWN_EGG, DMItems.TERRA_DRAGON_SPAWN_EGG, DMItems.WATER_DRAGON_SPAWN_EGG, DMItems.WITHER_DRAGON_SPAWN_EGG, DMItems.ZOMBIE_DRAGON_SPAWN_EGG);

        colors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex != 0) return 0xFFFFFF;// claw
            // orb jewel
            final long GLOW_CYCLE_PERIOD_SECONDS = 4;
            final float MIN_GLOW_BRIGHTNESS = 0.4F;
            final float MAX_GLOW_BRIGHTNESS = 1.0F;
            final long NANO_SEC_PER_SEC = 1000L * 1000L * 1000L;
            long cyclePosition = System.nanoTime() % (GLOW_CYCLE_PERIOD_SECONDS * NANO_SEC_PER_SEC);
            double cyclePosRadians = 2 * Math.PI * cyclePosition / (GLOW_CYCLE_PERIOD_SECONDS * NANO_SEC_PER_SEC);
            final float BRIGHTNESS_MIDPOINT = (MIN_GLOW_BRIGHTNESS + MAX_GLOW_BRIGHTNESS) * 0.5F;
            final float BRIGHTNESS_AMPLITUDE = (MAX_GLOW_BRIGHTNESS - BRIGHTNESS_MIDPOINT);
            int brightness = MathHelper.clamp((int) (255 * (BRIGHTNESS_MIDPOINT + BRIGHTNESS_AMPLITUDE * MathHelper.sin((float) cyclePosRadians))), 0, 255);
            return ((brightness & 0xFF) << 16) | ((brightness & 0xFF) << 8) | (brightness & 0xFF);
        }, DMItems.DRAGON_ORB);
    }

    @SubscribeEvent
    public static void registerSprites(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        map.registerSprite(new ResourceLocation(MOD_ID, "items/slot/empty_banner"));
        map.registerSprite(new ResourceLocation(MOD_ID, "items/slot/empty_chest"));
        map.registerSprite(new ResourceLocation(MOD_ID, "items/slot/empty_dragon_armor"));
        map.registerSprite(new ResourceLocation(MOD_ID, "items/slot/empty_essence"));
        map.registerSprite(new ResourceLocation(MOD_ID, "items/slot/empty_saddle"));
        map.registerSprite(new ResourceLocation(MOD_ID, "items/slot/empty_whistle"));
    }
}
