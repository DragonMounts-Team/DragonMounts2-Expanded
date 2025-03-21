/*
 ** 2011 December 10
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.client.render.dragon;

import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.entity.helper.DragonLifeStageHelper;
import net.dragonmounts.init.DMBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import static org.lwjgl.opengl.GL11.*;

/**
 * Generic renderer for all dragons.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonRenderer extends RenderLiving<ClientDragonEntity> {
    public DragonRenderer(RenderManager renderManager) {
        super(renderManager, null, 2);
    }

    @Override
    public void doRender(ClientDragonEntity dragon, double x, double y, double z, float yaw, float partialTicks) {
        if (dragon.isEgg()) {
            renderEgg(dragon, x, y, z, yaw, partialTicks);
            return;
        }
        this.mainModel = dragon.getVariant().appearance.getModel(dragon);
        super.doRender(dragon, x, y, z, yaw, partialTicks);
        if (dragon.isInGui) return;
        EntityEnderCrystal crystal = dragon.healingEnderCrystal;
        if (crystal != null) {
            this.bindTexture(RenderDragon.ENDERCRYSTAL_BEAM_TEXTURES);
            float f = MathHelper.sin((crystal.ticksExisted + partialTicks) * 0.2F) / 2.0F + 0.5F;
            float l = 1.0F - partialTicks;
            RenderDragon.renderCrystalBeams(x, y, z, partialTicks, dragon.posX + (dragon.prevPosX - dragon.posX) * l, dragon.posY + (dragon.prevPosY - dragon.posY) * l, dragon.posZ + (dragon.prevPosZ - dragon.posZ) * l, dragon.ticksExisted, crystal.posX, (f * f + f) * 0.2F + crystal.posY, crystal.posZ);
        }
    }

    @Override
    protected void renderLayers(ClientDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        VariantAppearance appearance = dragon.getVariant().appearance;
        TextureManager manager = this.renderManager.renderEngine;
        DragonModel model = appearance.getModel(dragon);
        for (IDragonLayer layer : appearance.layers) {
            boolean changed = setBrightness(dragon, partialTicks, layer.shouldCombineTextures());
            layer.renderLayer(manager, model, dragon, moveTime, moveSpeed, partialTicks, ticksExisted, lookYaw, lookPitch, scale);
            if (changed) {
                unsetBrightness();
            }
        }
    }

    /**
     * Renders the model in RenderLiving
     */
    @Override
    protected void renderModel(ClientDragonEntity dragon, float moveTime, float moveSpeed, float ticksExisted, float lookYaw, float lookPitch, float scale) {

        float death = dragon.deathTime / (float) dragon.getMaxDeathTime();

        if (death > 0) {
            glPushAttrib(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

            GlStateManager.depthFunc(GL_LEQUAL);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL_GREATER, death);

            bindTexture(dragon.getVariant().appearance.getDissolve(dragon));
            mainModel.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);

            GlStateManager.alphaFunc(GL_GREATER, 0.1f);
            GlStateManager.depthFunc(GL_EQUAL);
        }

        super.renderModel(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);

        if (death > 0) {
            GlStateManager.popAttrib();
        }
    }

    protected void renderEgg(ClientDragonEntity dragon, double x, double y, double z, float pitch, float partialTicks) {
        // apply egg wiggle
        DragonLifeStageHelper lifeStage = dragon.lifeStageHelper;
        this.renderName(dragon, x, y, z);
        float tickX = lifeStage.getEggWiggleX();
        float tickZ = lifeStage.getEggWiggleZ();

        // prepare GL states
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y /*+ (lev ? l : 0)*/, z);
        if (tickX > 0.0F) {
            GlStateManager.rotate(MathHelper.sin(tickX - partialTicks) * 8, 1, 0, 0);
        }
        if (tickZ > 0.0F) {
            GlStateManager.rotate(MathHelper.sin(tickZ - partialTicks) * 8, 0, 0, 1);
        }
        GlStateManager.disableLighting();

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        // prepare egg rendering
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        vb.begin(GL_QUADS, DefaultVertexFormats.BLOCK);

        IBlockState state = dragon.getVariant().type.getInstance(HatchableDragonEggBlock.class, DMBlocks.ENDER_DRAGON_EGG).getDefaultState();
        BlockPos pos = dragon.getPosition();
        vb.setTranslation(-pos.getX() - 0.5, -pos.getY(), -pos.getZ() - 0.5);

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

        // render egg
        dispatcher.getBlockModelRenderer().renderModel(dragon.world, dispatcher.getModelForState(state), state, pos, vb, false);
        vb.setTranslation(0, 0, 0);

        tessellator.draw();

        // restore GL state
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    protected void applyRotations(ClientDragonEntity dragon, float ageInTicks, float rotationYaw, float partialTicks) {
        GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before
     * the model is rendered. Args: entityLiving, partialTickTime
     */
    @Override
    protected void preRenderCallback(ClientDragonEntity dragon, float partialTicks) {
        // a fully grown dragon is larger than the model by this amount
        float scale = dragon.getScale() * dragon.getVariant().appearance.renderScale;
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected ResourceLocation getEntityTexture(ClientDragonEntity dragon) {
        return dragon.getVariant().appearance.getBody(dragon);
    }
}

