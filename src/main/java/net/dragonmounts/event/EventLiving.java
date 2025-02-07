package net.dragonmounts.event;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventLiving {
    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (event.isMounting()) return;
        Entity passenger = event.getEntityMounting();
        if (passenger instanceof EntityPlayerMP) {
            Entity vehicle = event.getEntityBeingMounted();
            if (vehicle instanceof TameableDragonEntity) {
                passenger.setPositionAndUpdate(
                        vehicle.posX,
                        vehicle.posY - ((TameableDragonEntity) vehicle).getScale() * 0.2,
                        vehicle.posZ
                );
            }
        }
    }
}

