package net.dragonmounts.event;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMKeyBindings;
import net.dragonmounts.network.CDragonControlPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class KeyBindingHandler {
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;
        Entity vehicle = player.getRidingEntity();
        if (vehicle instanceof TameableDragonEntity) {
            if (DMKeyBindings.TOGGLE_CAMERA_POS.isPressed()) {
                CameraHandler.toggleCamera();
            }
            if (player == vehicle.getControllingPassenger()) {
                DragonMounts.NETWORK_WRAPPER.sendToServer(new CDragonControlPacket(
                        vehicle.getEntityId(),
                        DMKeyBindings.KEY_BREATH.isKeyDown(),
                        DMKeyBindings.KEY_BOOST.isKeyDown(),
                        DMKeyBindings.KEY_DESCENT.isKeyDown(),
                        DMKeyBindings.TOGGLE_HOVERING.isPressed(),
                        DMKeyBindings.TOGGLE_YAW_ALIGNMENT.isPressed(),
                        DMKeyBindings.TOGGLE_PITCH_ALIGNMENT.isPressed()
                ));
            }
        }
    }
}
