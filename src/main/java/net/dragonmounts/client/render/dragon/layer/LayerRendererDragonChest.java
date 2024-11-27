package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.render.dragon.DragonRenderer;
import net.dragonmounts.client.render.dragon.breeds.DefaultDragonBreedRenderer;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;

public class LayerRendererDragonChest extends LayerRendererDragon {
    public LayerRendererDragonChest(DragonRenderer renderer, DefaultDragonBreedRenderer breedRenderer, DragonModel model) {
        super(renderer, breedRenderer, model);
    }

    @Override
    public void doRenderLayer(EntityTameableDragon dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        if (dragon.isChested()) {
            renderer.bindTexture(breedRenderer.getChestTexture());
            model.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}