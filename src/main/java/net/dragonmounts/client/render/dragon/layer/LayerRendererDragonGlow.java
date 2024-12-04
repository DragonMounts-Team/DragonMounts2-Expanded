package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.model.dragon.DragonModelMode;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.GL_ONE;

/**
 * Created by EveryoneElse on 14/06/2015.
 */
public class LayerRendererDragonGlow extends DragonLayerRenderer {
    @Override
    public void doRenderLayer(EntityTameableDragon dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        this.renderer.bindTexture(dragon.getVariant().appearance.getGlow(dragon));
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_ONE, GL_ONE);
        GlStateManager.color(1, 1, 1, 1);

        /*if (!dragon.isAsleep)*/
        disableLighting();
        model.setMode(DragonModelMode.FULL);
        model.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
        enableLighting(dragon.getBrightnessForRender());

        GlStateManager.disableBlend();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
