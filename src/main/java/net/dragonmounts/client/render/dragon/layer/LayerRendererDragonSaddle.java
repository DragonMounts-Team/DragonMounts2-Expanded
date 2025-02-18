package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.render.dragon.DragonRenderMode;
import net.dragonmounts.entity.TameableDragonEntity;

/**
 * Created by EveryoneElse on 14/06/2015.
 */
public class LayerRendererDragonSaddle extends DragonLayerRenderer {
    @Override
    public void doRenderLayer(TameableDragonEntity dragon, float moveTime,
                              float moveSpeed, float partialTicks, float ticksExisted, float lookYaw,
                              float lookPitch, float scale) {
        if (!dragon.isSaddled()) return;
        renderer.bindTexture(dragon.getVariant().appearance.getSaddle(dragon));
        model.render(DragonRenderMode.SADDLE, dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
