package net.dragonmounts.client.render;

import net.dragonmounts.block.DragonHeadBlock;
import net.dragonmounts.block.entity.DragonHeadBlockEntity;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.item.DragonHeadItem;
import net.dragonmounts.util.math.MathX;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

import static org.lwjgl.opengl.GL11.*;

public class DragonHeadBlockEntityRenderer extends TileEntitySpecialRenderer<DragonHeadBlockEntity> {
    public static void render(
            TextureManager manager,
            VariantAppearance appearance,
            float x,
            float y,
            float z,
            float yRot,
            int destroyStage,
            float animateTicks
    ) {
        DragonModel model = appearance.getModel(null);
        ModelRenderer head = model.head;
        boolean normal = destroyStage < 0;
        if (normal) {
            manager.bindTexture(appearance.getBody(null));
        } else {
            manager.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();
        model.head.jaw.rotateAngleX = 0.2F * (MathHelper.sin(animateTicks * MathX.PI_F * 0.2F) + 1.0F);
        head.rotateAngleY = yRot * 0.017453292F;
        head.rotationPointX = head.rotationPointY = head.rotationPointZ = head.rotateAngleX = 0.0F;
        GlStateManager.translate(0.0F, -0.374375F, 0.0F);
        GlStateManager.scale(0.75F, 0.75F, 0.75F);
        head.render(0.0625F);
        if (normal) {
            GlStateManager.depthMask(true);
            manager.bindTexture(appearance.getGlow(null));
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL_ONE, GL_ONE);
            GlStateManager.disableLighting();
            head.render(0.0625F);
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
        if (normal) return;
        GlStateManager.matrixMode(5890);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
    }

    @Override
    public void render(DragonHeadBlockEntity entity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TextureManager manager = this.rendererDispatcher.renderEngine;
        if (manager == null) return;
        Block block = entity.getBlockType();
        if (!(block instanceof DragonHeadBlock)) return;
        DragonHeadBlock head = (DragonHeadBlock) block;
        int meta = entity.getBlockMetadata();
        EnumFacing facing = head.getFacing(meta);
        float yRot;
        switch (facing) {
            case UP:
                x += 0.5;
                z += 0.5;
                yRot = 22.5F * meta;
                break;
            case NORTH:
                x += 0.5;
                y += 0.25;
                z += 0.74;
                yRot = 0;
                break;
            case WEST:
                x += 0.74;
                y += 0.25;
                z += 0.5;
                yRot = 270.0F;
                break;
            case EAST:
                x += 0.26;
                y += 0.25;
                z += 0.5;
                yRot = 90.0F;
                break;
            default:
                x += 0.5;
                y += 0.25;
                z += 0.26;
                yRot = 180.0F;
                break;
        }
        render(
                manager,
                head.variant.appearance,
                (float) x,
                (float) y,
                (float) z,
                yRot,
                destroyStage,
                entity.getAnimationProgress(partialTicks)
        );
    }

    public static class ItemStackRenderer extends TileEntityItemStackRenderer {
        public void renderByItem(ItemStack stack, float partialTicks) {
            Item item = stack.getItem();
            if (item instanceof DragonHeadItem) {
                TextureManager manager = Minecraft.getMinecraft().renderEngine;
                if (manager == null) return;
                GlStateManager.pushMatrix();
                GlStateManager.disableCull();
                render(
                        manager,
                        ((DragonHeadItem) item).variant.appearance,
                        0.5F,
                        0.0F,
                        0.5F,
                        180.0F,
                        -1,
                        0.0F
                );
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        }
    }

    public static void renderLayer(DragonHeadItem item, float limbSwing, boolean isVillager) {
        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        if (manager != null) {
            GlStateManager.scale(1.1875F, -1.1875F, -1.1875F);
            if (isVillager) GlStateManager.translate(0.0F, 0.0625F, 0.0F);
            render(
                    manager,
                    item.variant.appearance,
                    0.0F,
                    0.0F,
                    0.0F,
                    180.0F,
                    -1,
                    limbSwing
            );
        }
        GlStateManager.popMatrix();
    }
}
