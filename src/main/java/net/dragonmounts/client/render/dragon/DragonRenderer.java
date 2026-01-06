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

import java.util.Iterator;

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
            float f = MathHelper.sin((crystal.ticksExisted + partialTicks) * 0.2F) * 0.5F + 0.5F;
            float l = 1.0F - partialTicks;
            RenderDragon.renderCrystalBeams(x, y, z, partialTicks, dragon.posX + (dragon.prevPosX - dragon.posX) * l, dragon.posY + (dragon.prevPosY - dragon.posY) * l, dragon.posZ + (dragon.prevPosZ - dragon.posZ) * l, dragon.ticksExisted, crystal.posX, (f * f + f) * 0.2F + crystal.posY, crystal.posZ);
        }
    }

    @Override
    protected void renderLayers(ClientDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        VariantAppearance appearance = dragon.getVariant().appearance;
        TextureManager manager = this.renderManager.renderEngine;
        DragonModel model = appearance.getModel(dragon);
        Iterator<IDragonLayer> iterator = appearance.layers.iterator();
        if (iterator.hasNext()) {
            IDragonLayer layer = iterator.next();
            boolean combined = layer.shouldCombineTextures();
            boolean changed = this.setBrightness(dragon, partialTicks, combined);
            layer.renderLayer(manager, model, dragon, moveTime, moveSpeed, partialTicks, ticksExisted, lookYaw, lookPitch, scale);
            while (iterator.hasNext()) {
                layer = iterator.next();
                if (combined != layer.shouldCombineTextures() && this.setBrightness(dragon, partialTicks, !combined)) {
                    combined = !combined;
                    changed = true;
                }
                layer.renderLayer(manager, model, dragon, moveTime, moveSpeed, partialTicks, ticksExisted, lookYaw, lookPitch, scale);
            }
            if (changed) {
                this.unsetBrightness();
            }
        }
    }

    /**
     * Renders the model in RenderLiving
     */
    @Override
    protected void renderModel(ClientDragonEntity dragon, float moveTime, float moveSpeed, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        boolean visible = this.isVisible(dragon);
        boolean transparent = !visible && !dragon.isInvisibleToPlayer(Minecraft.getMinecraft().player);
        if (visible || transparent) {
            if (transparent) {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
            if (dragon.deathTime > 0) {
                VariantAppearance appearance = dragon.getVariant().appearance;
                GlStateManager.enableAlpha();
                GlStateManager.alphaFunc(GL_GREATER, dragon.deathTime / (float) dragon.getMaxDeathTime());
                GlStateManager.depthFunc(GL_LEQUAL);
                GlStateManager.colorMask(false, false, false, false);
                this.bindTexture(appearance.getDissolve(dragon));
                this.mainModel.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.depthFunc(GL_EQUAL);
                GlStateManager.alphaFunc(GL_GREATER, 0.1F);
                this.bindTexture(appearance.getBody(dragon));
                this.mainModel.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
                GlStateManager.depthFunc(GL_LEQUAL);
            } else {
                this.bindTexture(dragon.getVariant().appearance.getBody(dragon));
                this.mainModel.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
            }
            if (transparent) {
                GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
        }
    }

    protected void renderEgg(ClientDragonEntity dragon, double x, double y, double z, float pitch, float partialTicks) {
        this.renderName(dragon, x, y, z);

        // prepare GL states
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        //  apply egg wobble
        float amplitude = dragon.getAmplitude(partialTicks);
        if (amplitude != 0.0F) {
            float axis = dragon.getWobbleAxis();
            GlStateManager.rotate(amplitude, MathHelper.cos(axis), 0.0F, MathHelper.sin(axis));
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
        float scale = dragon.getAdjustedSize();
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected ResourceLocation getEntityTexture(ClientDragonEntity dragon) {
        return dragon.getVariant().appearance.getBody(dragon);
    }
}

