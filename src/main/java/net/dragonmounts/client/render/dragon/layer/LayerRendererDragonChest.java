package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;

public class LayerRendererDragonChest extends DragonLayerRenderer {
    @Override
    public void doRenderLayer(EntityTameableDragon dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        if (dragon.isChested()) {
            renderer.bindTexture(dragon.getVariant().appearance.getChest(dragon));
            model.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}