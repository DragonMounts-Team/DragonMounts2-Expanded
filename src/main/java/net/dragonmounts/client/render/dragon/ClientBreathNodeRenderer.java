package net.dragonmounts.client.render.dragon;

import net.dragonmounts.client.breath.ClientBreathNodeEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ClientBreathNodeRenderer extends Render<ClientBreathNodeEntity> {
    public ClientBreathNodeRenderer(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(@Nonnull ClientBreathNodeEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        this.bindEntityTexture(entity);
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();
        float scale = entity.getRenderScale();
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(this.renderManager.options.thirdPersonView == 2 ? this.renderManager.playerViewX : -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        if (entity.rollSpeed != 0.0F) {
            GlStateManager.rotate((entity.ticksExisted + partialTicks) * entity.rollSpeed, 0, 0, 1);
        }
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }
        int brightness = entity.isDead ? 15728880 : entity.getBrightnessForRender();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (brightness & 0xFF), (float) (brightness >> 16 & 0xFF));
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(-0.5, -0.25, 0.0).tex(0.0, 1.0).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5, -0.25, 0.0).tex(1.0, 1.0).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5, 0.75, 0.0).tex(1.0, 0.0).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(-0.5, 0.75, 0.0).tex(0.0, 0.0).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(ClientBreathNodeEntity entity) {
        return entity.getTexture();
    }
}
