package net.dragonmounts.client.render;

import net.dragonmounts.block.DragonCoreBlock;
import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.client.model.DragonCoreModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.dragonmounts.DragonMounts.makeId;

@SideOnly(Side.CLIENT)
public class DragonCoreBlockEntityRenderer extends TileEntitySpecialRenderer<DragonCoreBlockEntity> implements IDragonCoreRenderer {
    private static final ResourceLocation TEXTURE = makeId("textures/blocks/dragon_core.png");
    private static final DragonCoreModel MODEL = new DragonCoreModel();

    @Override
    public void render(DragonCoreBlockEntity core, double x, double y, double z, float partialTicks, int destroy, float alpha) {
        TextureManager manager = this.rendererDispatcher.renderEngine;
        if (manager == null) return;
        MODEL.render(manager, this, (float) x, (float) y, (float) z, destroy, core.getProgress(partialTicks), 0.0625F, alpha);
        if (destroy >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    @Override
    public void bindTexture(TextureManager manager, int destroy) {
        if (destroy < 0) {
            manager.bindTexture(TEXTURE);
        } else {
            manager.bindTexture(DESTROY_STAGES[destroy]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
    }

    public static class ItemStackRenderer extends TileEntityItemStackRenderer implements IDragonCoreRenderer {
        @Override
        public void bindTexture(TextureManager manager, int destroy) {
            manager.bindTexture(TEXTURE);
        }

        public void renderByItem(ItemStack stack, float partialTicks) {
            Item item = stack.getItem();
            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof DragonCoreBlock) {
                TextureManager manager = Minecraft.getMinecraft().renderEngine;
                if (manager == null) return;
                MODEL.render(manager, this, 0.0F, 0.0F, 0.0F, -1, 0.0F, 0.0625F, 1.0F);
            }
        }
    }
}