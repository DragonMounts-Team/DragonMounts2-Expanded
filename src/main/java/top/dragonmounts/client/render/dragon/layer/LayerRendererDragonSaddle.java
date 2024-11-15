package top.dragonmounts.client.render.dragon.layer;

import top.dragonmounts.client.model.dragon.DragonModel;
import top.dragonmounts.client.render.dragon.DragonRenderer;
import top.dragonmounts.client.render.dragon.breeds.DefaultDragonBreedRenderer;
import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;

/**
 * Created by EveryoneElse on 14/06/2015.
 */
public class LayerRendererDragonSaddle extends LayerRendererDragon {

    public LayerRendererDragonSaddle(DragonRenderer renderer,
                                     DefaultDragonBreedRenderer breedRenderer, DragonModel model) {
        super(renderer, breedRenderer, model);
    }

    @Override
    public void doRenderLayer(EntityTameableDragon dragon, float moveTime,
                              float moveSpeed, float partialTicks, float ticksExisted, float lookYaw,
                              float lookPitch, float scale) {
        if (!dragon.isSaddled()) return;
        renderer.bindTexture(breedRenderer.getSaddleTexture());
        model.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
