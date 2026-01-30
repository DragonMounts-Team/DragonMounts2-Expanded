package net.dragonmounts.client.model;

import net.dragonmounts.client.render.IDragonCoreRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DragonCoreModel extends ModelBase {
    public final ModelRenderer core;
    public final ModelRenderer base;
    public final ModelRenderer lid;

    public DragonCoreModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.lid = new ModelRenderer(this, 0, 0)
                .addBox(-8.0F, -16.0F, -8.0F, 16, 12, 16)
        ).rotationPointY = 24.0F;
        (this.base = new ModelRenderer(this, 0, 28)
                .addBox(-8.0F, -8.0F, -8.0F, 16, 8, 16)
        ).rotationPointY = 24.0F;
        (this.core = new ModelRenderer(this, 0, 52)
                .addBox(-3.0F, 0.0F, -3.0F, 6, 6, 6)
        ).rotationPointY = 12.0F;
    }

    /// @see net.minecraft.client.renderer.tileentity.TileEntityShulkerBoxRenderer#render(net.minecraft.tileentity.TileEntityShulkerBox, double, double, double, float, int, float)
    public void render(TextureManager manager, IDragonCoreRenderer renderer, float x, float y, float z, int destroy, float progress, float scale, float alpha) {
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        renderer.bindTexture(manager, destroy);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        if (destroy < 0) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        }
        GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.9995F, 0.9995F, 0.9995F);
        GlStateManager.translate(0.0F, -1.0F, 0.0F);
        this.base.render(scale);
        if (progress != 0.0F) {
            GlStateManager.translate(0.0F, -progress * 0.5F, 0.0F);
            GlStateManager.rotate(270.0F * progress, 0.0F, 1.0F, 0.0F);
        }
        this.lid.render(scale);
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
