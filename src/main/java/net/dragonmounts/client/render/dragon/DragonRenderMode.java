package net.dragonmounts.client.render.dragon;

import net.dragonmounts.client.model.dragon.DragonModel;
import net.minecraft.client.renderer.GlStateManager;

public enum DragonRenderMode {
    DRAGON {
        @Override
        public void render(DragonModel model, float scale) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.92F, 0.92F, 0.92F);
            model.head.render(scale);
            GlStateManager.popMatrix();
            model.neck.renderSegments(scale);
            model.body.render(scale);
            model.tail.renderSegments(scale);
            model.renderLegs(scale);
            model.renderWings(scale);
        }
    },
    CHEST {
        @Override
        public void render(DragonModel model, float scale) {
            model.body.postRender(scale);
            model.chest.showModel = true;
            model.chest.render(scale);
            model.chest.showModel = false;
        }
    },
    SADDLE {
        @Override
        public void render(DragonModel model, float scale) {
            model.saddle.showModel = true;
            model.body.render(scale);
            model.saddle.showModel = false;
        }
    };

    public abstract void render(DragonModel model, float scale);
}
