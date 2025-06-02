package net.dragonmounts.client.render.dragon;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

public interface IDragonLayer {
    void renderLayer(TextureManager manager, DragonModel model, ClientDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale);

    /// @see LayerRenderer#shouldCombineTextures()
    default boolean shouldCombineTextures() {
        return false;
    }
}
