package net.dragonmounts.client.render.dragon;

import com.google.common.collect.ImmutableList;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.model.dragon.DragonAnimator;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.inventory.DragonInventory;
import net.dragonmounts.item.DragonArmorItem;
import net.dragonmounts.util.math.Interpolation;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;

import static org.lwjgl.opengl.GL11.GL_ONE;

public enum BuiltinDragonLayer implements IDragonLayer {
    GLOW() {
        @Override
        public void renderLayer(TextureManager manager, DragonModel model, ClientDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
            manager.bindTexture(dragon.getVariant().appearance.getGlow(dragon));
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL_ONE, GL_ONE);
            GlStateManager.color(1, 1, 1, 1);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            GlStateManager.disableLighting();
            model.render(DragonRenderMode.DRAGON, dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
            int brightness = dragon.getBrightnessForRender();
            OpenGlHelper.setLightmapTextureCoords(
                    OpenGlHelper.lightmapTexUnit,
                    brightness & 0xFFFF,
                    brightness >> 16
            );
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
        }
    },
    ARMOR() {
        @Override
        public void renderLayer(TextureManager manager, DragonModel model, ClientDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
            ItemStack stack = dragon.getArmor();
            if (stack.isEmpty()) return;
            Item item = stack.getItem();
            if (item instanceof DragonArmorItem) {
                manager.bindTexture(((DragonArmorItem) item).texture);
                model.render(DragonRenderMode.DRAGON, dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
            }
        }
    },
    BANNER() {
        @Override
        public void renderLayer(TextureManager manager, DragonModel model, ClientDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
            DragonAnimator animator = dragon.animator;
            float pitch = animator.getPitch();
            DragonInventory inventory = dragon.inventory;
            ItemStack stack;
            if ((stack = inventory.getBanner(0)).getItem() == Items.BANNER) {
                GlStateManager.pushMatrix();
                model.body.postRender(0.0625F);
                //lower x++ higher x--
                GlStateManager.translate(0.7F, 0.0, Interpolation.smoothStep(-2.6F, -0.6F, animator.getSpeed())); // all of it is get speed or one was pitch?
                // higher y-- lower y++
                GlStateManager.translate(0, Interpolation.smoothStep(0.2F, animator.getModelOffsetY() + 1.2F, animator.getSpeed()), 0);
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(-pitch, 0.0F, 0.0F, 1.0F);
                renderBanner(manager, stack);
                GlStateManager.popMatrix();
            }
            if ((stack = inventory.getBanner(1)).getItem() == Items.BANNER) {
                GlStateManager.pushMatrix();
                model.body.postRender(0.0625F);
                //lower x++ higher x--
                GlStateManager.translate(-0.7F, 0.0, Interpolation.smoothStep(-2.6F, -0.6F, animator.getSpeed()));
                // higher y-- lower y++
                GlStateManager.translate(0, Interpolation.smoothStep(0.2F, animator.getModelOffsetY() + 1.2F, animator.getSpeed()), 0);
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);
                renderBanner(manager, stack);
                GlStateManager.popMatrix();
            }
            if ((stack = inventory.getBanner(2)).getItem() == Items.BANNER) {
                GlStateManager.pushMatrix();
                model.body.postRender(0.0625F);
                GlStateManager.translate(-0.4F, -1.7F, 1.7F);
                GlStateManager.translate(0, Interpolation.smoothStep(2.9F, animator.getModelOffsetY() + 1.7F, animator.getSpeed()), 0);
                GlStateManager.translate(0, 0, Interpolation.smoothStep(-2.3F, animator.getModelOffsetZ() + 1.0F, animator.getSpeed()));
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
                renderBanner(manager, stack);
                GlStateManager.popMatrix();
            }
            if ((stack = inventory.getBanner(3)).getItem() == Items.BANNER) {
                GlStateManager.pushMatrix();
                model.body.postRender(0.0625F);
                GlStateManager.translate(0.4F, -1.7F, 1.7F);
                GlStateManager.translate(0, Interpolation.smoothStep(2.9F, animator.getModelOffsetY() + 1.7F, animator.getSpeed()), 0);
                GlStateManager.translate(0, 0, Interpolation.smoothStep(-2.3F, animator.getModelOffsetZ() + 1.0F, animator.getSpeed()));
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
                renderBanner(manager, stack);
                GlStateManager.popMatrix();
            }
        }

        @Override
        public boolean shouldCombineTextures() {
            return true;
        }
    },
    CHEST() {
        @Override
        public void renderLayer(TextureManager manager, DragonModel model, ClientDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
            if (dragon.isChested()) {
                manager.bindTexture(dragon.getVariant().appearance.getChest(dragon));
                model.render(DragonRenderMode.CHEST, dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
            }
        }
    },
    SADDLE() {
        @Override
        public void renderLayer(TextureManager manager, DragonModel model, ClientDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
            if (!dragon.isSaddled()) return;
            manager.bindTexture(dragon.getVariant().appearance.getSaddle(dragon));
            model.render(DragonRenderMode.SADDLE, dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
        }
    };
    private static final ModelBanner BANNER_MODEL = new ModelBanner();
    private static final TileEntityBanner BANNER_ENTITY = new TileEntityBanner();
    public static final ImmutableList<IDragonLayer> DEFAULT_LAYERS = ImmutableList.of(GLOW, SADDLE, ARMOR, CHEST, BANNER);

    private static void renderBanner(TextureManager manager, ItemStack stack) {
        GlStateManager.scale(0.625F, -0.625F, -0.625F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1);
        BANNER_ENTITY.setItemValues(stack, false);
        //noinspection DataFlowIssue
        manager.bindTexture(BannerTextures.BANNER_DESIGNS.getResourceLocation(
                BANNER_ENTITY.getPatternResourceLocation(),
                BANNER_ENTITY.getPatternList(),
                BANNER_ENTITY.getColorList()
        ));
        BANNER_MODEL.bannerStand.showModel = false;
        BANNER_MODEL.renderBanner();
    }
}
