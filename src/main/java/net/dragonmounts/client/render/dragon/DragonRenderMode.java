package net.dragonmounts.client.render.dragon;

import net.dragonmounts.client.model.dragon.DragonModel;
import net.minecraft.client.renderer.GlStateManager;

public enum DragonRenderMode {
    FULL {
        @Override
        public void render(DragonModel model, float scale) {
            model.renderHead(scale);
            model.renderNeck(scale);
            model.renderBody(scale);
            model.renderLegs(scale);
            model.renderTail(scale);
            model.renderWings(scale);
        }
    },
    CHEST {
        @Override
        public void render(DragonModel model, float scale) {
            model.renderBody(scale);
        }
    },
    SADDLE {
        @Override
        public void render(DragonModel model, float scale) {
            model.renderNeck(scale);
            model.renderBody(scale);
            GlStateManager.scale(-1, 1, 1);
            model.renderWings(scale);
        }
    };

    public abstract void render(DragonModel model, float scale);
}
