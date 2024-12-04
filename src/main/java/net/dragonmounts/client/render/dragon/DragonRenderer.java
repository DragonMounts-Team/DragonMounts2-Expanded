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

import net.dragonmounts.block.BlockDragonBreedEgg;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.model.dragon.DragonModelMode;
import net.dragonmounts.client.render.dragon.layer.DragonLayerRenderer;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStageHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
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
public class DragonRenderer extends RenderLiving<EntityTameableDragon> {

    public static final String TEX_BASE = "textures/entities/dragon/";

    public DragonRenderer(RenderManager renderManager) {
        super(renderManager, null, 2);
    }

    @Override
    public void doRender(EntityTameableDragon dragon, double x, double y, double z, float yaw, float partialTicks) {
        renderName(dragon, x, y, z);
        if (dragon.isEgg()) {
            renderEgg(dragon, x, y, z, yaw, partialTicks);
            return;
        }
        DragonModel breedModel = dragon.getVariant().appearance.model;
        breedModel.setMode(DragonModelMode.FULL);
        mainModel = breedModel;
        super.doRender(dragon, x, y, z, yaw, partialTicks);
        if (dragon.getAnimator().isInGui) return;
        EntityEnderCrystal crystal = dragon.healingEnderCrystal;
        if (crystal != null) {
            this.bindTexture(RenderDragon.ENDERCRYSTAL_BEAM_TEXTURES);
            float f = MathHelper.sin((crystal.ticksExisted + partialTicks) * 0.2F) / 2.0F + 0.5F;
            float l = 1.0F - partialTicks;
            RenderDragon.renderCrystalBeams(x, y, z, partialTicks, dragon.posX + (dragon.prevPosX - dragon.posX) * l, dragon.posY + (dragon.prevPosY - dragon.posY) * l, dragon.posZ + (dragon.prevPosZ - dragon.posZ) * l, dragon.ticksExisted, crystal.posX, (f * f + f) * 0.2F + crystal.posY, crystal.posZ);
        }
    }

    @Override
    protected void renderLayers(EntityTameableDragon dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        VariantAppearance appearance = dragon.getVariant().appearance;
        DragonModel model = appearance.model;
        for (DragonLayerRenderer layer : appearance.layers) {
            layer.bind(this, model);
            boolean changed = setBrightness(dragon, partialTicks, layer.shouldCombineTextures());
            layer.doRenderLayer(dragon, moveTime, moveSpeed, partialTicks, ticksExisted, lookYaw, lookPitch, scale);
            if (changed) {
                unsetBrightness();
            }
        }
    }

    /**
     * Renders the model in RenderLiving
     */
    @Override
    protected void renderModel(EntityTameableDragon dragon, float moveTime, float moveSpeed, float ticksExisted, float lookYaw, float lookPitch, float scale) {

        float death = dragon.getDeathTime() / (float) dragon.getMaxDeathTime();

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

    protected void renderEgg(EntityTameableDragon dragon, double x, double y, double z, float pitch, float partialTicks) {
        // apply egg wiggle
        DragonLifeStageHelper lifeStage = dragon.getLifeStageHelper();
        float tickX = lifeStage.getEggWiggleX();
        float tickZ = lifeStage.getEggWiggleZ();

        // prepare GL states
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y /*+ (lev ? l : 0)*/, z);
        GlStateManager.rotate(tickX > 0 ? MathHelper.sin(tickX - partialTicks) * 8 : 0, 1, 0, 0);
        GlStateManager.rotate(tickZ > 0 ? MathHelper.sin(tickZ - partialTicks) * 8 : 0, 0, 0, 1);
        GlStateManager.disableLighting();

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        // prepare egg rendering
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        vb.begin(GL_QUADS, DefaultVertexFormats.BLOCK);

        IBlockState state = BlockDragonBreedEgg.DRAGON_BREED_EGG.getDefaultState();//.withProperty(BlockDragonBreedEgg.BREED, dragon.getBreedType());TODO: use DragonType
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
    protected void applyRotations(EntityTameableDragon dragon, float par2, float par3, float par4) {
        GlStateManager.rotate(180 - par3, 0, 1, 0);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before
     * the model is rendered. Args: entityLiving, partialTickTime
     */
    @Override
    protected void preRenderCallback(EntityTameableDragon dragon, float partialTicks) {
        // a fully grown dragon is larger than the model by this amount
        float scale = dragon.getScale() * 1.6F;//TODO: update VariantAppearance
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTameableDragon dragon) {
        return dragon.getVariant().appearance.getBody(dragon);
    }
}

