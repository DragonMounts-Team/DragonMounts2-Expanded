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
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class DragonLayerRenderer implements LayerRenderer<TameableDragonEntity> {
    protected TextureManager manager;
    protected DragonModel model;

    public void bind(TextureManager manager, DragonModel model) {
        this.manager = manager;
        this.model = model;
    }
}
