package net.dragonmounts.client.render;

import net.minecraft.client.renderer.texture.TextureManager;

public interface IDragonCoreRenderer {
    void bindTexture(TextureManager manager, int destroyStage);
}
