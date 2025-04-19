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

public class ClientBreathNodeRenderer extends Render<ClientBreathNodeEntity> {
    public ClientBreathNodeRenderer(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(ClientBreathNodeEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        this.bindEntityTexture(entity);
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();
        float scale = entity.getRenderScale();
        GlStateManager.scale(scale, scale, scale);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        if (entity.extraRotation) {
            if (entity.getRandom().nextInt(4) == 0) {
                GlStateManager.rotate(entity.ticksExisted * 30, 0, 0, 1);
            }
            GlStateManager.rotate(this.renderManager.options.thirdPersonView == 2 ? this.renderManager.playerViewX : -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.ticksExisted * 40, 0, 0, 1);
        } else {
            GlStateManager.rotate(this.renderManager.options.thirdPersonView == 2 ? this.renderManager.playerViewX : -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        }
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        int i = entity.isDead ? entity.getBrightnessForRender() : 15728880;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        tessellator.draw();
        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    protected ResourceLocation getEntityTexture(ClientBreathNodeEntity entity) {
        return entity.getTexture();
    }
}
