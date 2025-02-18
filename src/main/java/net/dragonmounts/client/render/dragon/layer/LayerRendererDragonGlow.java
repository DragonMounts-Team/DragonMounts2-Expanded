package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.render.dragon.DragonRenderMode;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.GL_ONE;

/**
 * Created by EveryoneElse on 14/06/2015.
 */
public class LayerRendererDragonGlow extends DragonLayerRenderer {
    @Override
    public void doRenderLayer(TameableDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        this.renderer.bindTexture(dragon.getVariant().appearance.getGlow(dragon));
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_ONE, GL_ONE);
        GlStateManager.color(1, 1, 1, 1);

        /*if (!dragon.isAsleep)*/
        disableLighting();
        model.render(DragonRenderMode.FULL, dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
        enableLighting(dragon.getBrightnessForRender());

        GlStateManager.disableBlend();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
