/*
 ** 2016 February 23
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.render.dragon.DragonRenderer;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class DragonLayerRenderer implements LayerRenderer<TameableDragonEntity> {
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    protected DragonRenderer renderer;
    protected DragonModel model;

    public void bind(DragonRenderer renderer, DragonModel model) {
        this.renderer = renderer;
        this.model = model;
    }

    protected void disableLighting() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GlStateManager.disableLighting();
    }

    protected void enableLighting(int b) {
        int u = b % 65536;
        int v = b / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, u, v);
        GlStateManager.enableLighting();
    }
}
