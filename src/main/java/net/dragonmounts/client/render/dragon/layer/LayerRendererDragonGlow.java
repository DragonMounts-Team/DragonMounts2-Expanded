package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.model.dragon.DragonModelMode;
import net.dragonmounts.client.render.dragon.DragonRenderer;
import net.dragonmounts.client.render.dragon.breeds.DefaultDragonBreedRenderer;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.GL_ONE;

/**
 * Created by EveryoneElse on 14/06/2015.
 */
public class LayerRendererDragonGlow extends LayerRendererDragon {

    public LayerRendererDragonGlow(DragonRenderer renderer, DefaultDragonBreedRenderer breedRenderer, DragonModel model) {
        super(renderer, breedRenderer, model);
    }

    @Override
    public void doRenderLayer(EntityTameableDragon dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        if(dragon.getBreedType()==EnumDragonBreed.FOREST) {
            renderer.bindTexture(dragon.isMale() ? breedRenderer.getMaleForestGlowTexture(dragon.isBaby(), dragon.getForestType().identifier) : breedRenderer.getFemaleForestGlowTexture(dragon.isBaby(), dragon.getForestType().identifier));
        } else {
            renderer.bindTexture(dragon.isMale() ? breedRenderer.getMaleGlowTexture(dragon.isBaby(), dragon.altTextures()) : breedRenderer.getFemaleGlowTexture(dragon.isBaby(), dragon.altTextures()));
        }
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
