package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.render.dragon.DragonRenderMode;

public class LayerRendererDragonChest extends DragonLayerRenderer {
    @Override
    public void doRenderLayer(ClientDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        if (dragon.isChested()) {
            this.manager.bindTexture(dragon.getVariant().appearance.getChest(dragon));
            this.model.render(DragonRenderMode.CHEST, dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}