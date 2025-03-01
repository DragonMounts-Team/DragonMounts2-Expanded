package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.model.dragon.DragonAnimator;
import net.dragonmounts.inventory.DragonInventory;
import net.dragonmounts.util.math.Interpolation;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;

public class LayerRendererDragonBanner extends DragonLayerRenderer {
    private final ModelBanner banner = new ModelBanner();
    private final TileEntityBanner instance = new TileEntityBanner();

    public void renderBanner(ItemStack stack) {
        GlStateManager.scale(0.625F, -0.625F, -0.625F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1);
        this.instance.setItemValues(stack, false);
        //noinspection DataFlowIssue
        this.manager.bindTexture(BannerTextures.BANNER_DESIGNS.getResourceLocation(
                this.instance.getPatternResourceLocation(),
                this.instance.getPatternList(),
                this.instance.getColorList()
        ));
        this.banner.bannerStand.showModel = false;
        this.banner.renderBanner();
    }

    @Override
    public void doRenderLayer(ClientDragonEntity dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        DragonAnimator animator = dragon.animator;
        float pitch = animator.getPitch();
        DragonInventory inventory = dragon.inventory;
        ItemStack stack;
        if ((stack = inventory.getBanner(0)).getItem() == Items.BANNER) {
            GlStateManager.pushMatrix();
            this.model.body.postRender(0.0625F);
            //lower x++ higher x--
            GlStateManager.translate(0.7F, 0.0, Interpolation.smoothStep(-2.6F, -0.6F, animator.getSpeed())); // all of it is get speed or one was pitch?
            // higher y-- lower y++
            GlStateManager.translate(0, Interpolation.smoothStep(0.2F, animator.getModelOffsetY() + 1.2F, animator.getSpeed()), 0);
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-pitch, 0.0F, 0.0F, 1.0F);
            this.renderBanner(stack);
            GlStateManager.popMatrix();
        }
        if ((stack = inventory.getBanner(1)).getItem() == Items.BANNER) {
            GlStateManager.pushMatrix();
            this.model.body.postRender(0.0625F);
            //lower x++ higher x--
            GlStateManager.translate(-0.7F, 0.0, Interpolation.smoothStep(-2.6F, -0.6F, animator.getSpeed()));
            // higher y-- lower y++
            GlStateManager.translate(0, Interpolation.smoothStep(0.2F, animator.getModelOffsetY() + 1.2F, animator.getSpeed()), 0);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);
            this.renderBanner(stack);
            GlStateManager.popMatrix();
        }
        if ((stack = inventory.getBanner(2)).getItem() == Items.BANNER) {
            GlStateManager.pushMatrix();
            this.model.body.postRender(0.0625F);
            GlStateManager.translate(-0.4F, -1.7F, 1.7F);
            GlStateManager.translate(0, Interpolation.smoothStep(2.9F, animator.getModelOffsetY() + 1.7F, animator.getSpeed()), 0);
            GlStateManager.translate(0, 0, Interpolation.smoothStep(-2.3F, animator.getModelOffsetZ() + 1.0F, animator.getSpeed()));
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
            this.renderBanner(stack);
            GlStateManager.popMatrix();
        }
        if ((stack = inventory.getBanner(3)).getItem() == Items.BANNER) {
            GlStateManager.pushMatrix();
            this.model.body.postRender(0.0625F);
            GlStateManager.translate(0.4F, -1.7F, 1.7F);
            GlStateManager.translate(0, Interpolation.smoothStep(2.9F, animator.getModelOffsetY() + 1.7F, animator.getSpeed()), 0);
            GlStateManager.translate(0, 0, Interpolation.smoothStep(-2.3F, animator.getModelOffsetZ() + 1.0F, animator.getSpeed()));
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
            this.renderBanner(stack);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}