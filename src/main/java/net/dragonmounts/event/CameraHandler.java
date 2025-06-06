package net.dragonmounts.event;

import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.CarriageEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CameraHandler {
    private static int position;

    public static void toggleCamera() {
        if (++position > 2) position = 0;
    }

    /**
     * Copied from EntityRenderer.orientCamera, uses raytraceresult to reduce thirdPersonView and unable xRay
     */
    public static void applyZoom(Entity camera, boolean direct, float scale, float yaw, float pitch, double partialTicks) {
        int view = Minecraft.getMinecraft().gameSettings.thirdPersonView;
        if (view == 0) return;
        double x = camera.prevPosX + (camera.posX - camera.prevPosX) * partialTicks;
        double y = camera.prevPosY + (camera.posY - camera.prevPosY) * partialTicks + camera.getEyeHeight();
        double z = camera.prevPosZ + (camera.posZ - camera.prevPosZ) * partialTicks;
        World world = Minecraft.getMinecraft().world;
        Vec3d start = new Vec3d(x, y, z);
        yaw = view == 2 ? yaw * 0.017453292F : (yaw + 180.F) * 0.017453292F;
        pitch = view == 2 ? (pitch + 180.F) * 0.017453292F : pitch * 0.017453292F;
        double distance = DMConfig.CAMERA_DISTANCE.value * scale;
        double temp = MathHelper.cos(pitch) * distance;
        double d4 = -MathHelper.sin(yaw) * temp;
        double d5 = MathHelper.cos(yaw) * temp;
        temp = -MathHelper.sin(pitch) * distance;
        double end = distance * 2;
        for (int i = 0; i < end; ++i) {
            float f3 = ((i & 1) * 2 - 1) * 0.1F;
            float f4 = ((i >> 1 & 1) * 2 - 1) * 0.1F;
            float f5 = ((i >> 2 & 1) * 2 - 1) * 0.1F;
            RayTraceResult hit = world.rayTraceBlocks(
                    new Vec3d(x + f3, y + f4, z + f5),
                    new Vec3d(x - d4 + f3 + f5, y - temp + f4, z - d5 + f5)
            );
            if (hit != null) {
                double dist = hit.hitVec.distanceTo(start);
                if (dist < distance) {
                    distance = dist;
                }
            }
        }
        if ((distance -= 4) <= 0) return;
        switch (position) {
            case 1:
                GlStateManager.translate(-4.7F, -0.08F * scale, view == 2 ? distance : -distance);
                break;
            case 2:
                GlStateManager.translate(4.7F, -0.08F * scale, view == 2 ? distance : -distance);
                break;
            default:
                GlStateManager.translate(0F, -1.3F * scale, view == 2 ? distance : -distance);
        }
    }

    /**
     * Credit to AlexThe666 : iceandfire
     */
    @SubscribeEvent
    public static void extendZoom(EntityViewRenderEvent.CameraSetup event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer) {
            Entity vehicle = entity.getRidingEntity();
            if (vehicle instanceof TameableDragonEntity) {
                applyZoom(
                        entity,
                        true,
                        ((TameableDragonEntity) vehicle).getAdjustedSize(),
                        event.getYaw(),
                        event.getPitch(),
                        event.getRenderPartialTicks()
                );
            } else if (vehicle instanceof CarriageEntity) {
                vehicle = vehicle.getRidingEntity();
                if (vehicle instanceof TameableDragonEntity) {
                    applyZoom(
                            entity,
                            false,
                            ((TameableDragonEntity) vehicle).getAdjustedSize(),
                            event.getYaw(),
                            event.getPitch(),
                            event.getRenderPartialTicks()
                    );
                }
            }
        }
    }
}
