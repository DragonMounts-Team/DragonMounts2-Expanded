package net.dragonmounts.client.render.dragon;

import net.dragonmounts.client.model.dragon.DragonModel;
import net.minecraft.client.renderer.GlStateManager;

public enum DragonRenderMode {
    FULL {
        @Override
        public void render(DragonModel model, float scale) {
            model.head.render(scale * 0.92F);
            model.neck.render(scale);
            model.body.render(scale);
            model.renderLegs(scale);
            model.tail.render(scale);
            model.renderWings(scale);
        }
    },
    CHEST {
        @Override
        public void render(DragonModel model, float scale) {
            GlStateManager.pushMatrix();
            model.body.postRender(scale);
            model.chest.render(scale);
            GlStateManager.popMatrix();
        }
    },
    SADDLE {
        @Override
        public void render(DragonModel model, float scale) {
            model.neck.render(scale);
            model.body.render(scale);
            GlStateManager.scale(-1, 1, 1);
            model.renderWings(scale);
        }
    };

    public abstract void render(DragonModel model, float scale);
}
