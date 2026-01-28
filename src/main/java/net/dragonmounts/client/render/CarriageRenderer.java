package net.dragonmounts.client.render;

import net.dragonmounts.client.model.CarriageModel;
import net.dragonmounts.entity.CarriageEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class CarriageRenderer extends Render<CarriageEntity> {
    protected CarriageModel model = new CarriageModel();

    public CarriageRenderer(RenderManager manager) {
        super(manager);
        this.shadowSize = 0.2F;
    }

    @Override
    public void doRender(@Nonnull CarriageEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.setupTranslation(x, y, z);
        this.setupRotation(entity, entityYaw, partialTicks);
        this.bindEntityTexture(entity);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.model.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void setupRotation(CarriageEntity entity, float entityYaw, float partialTicks) {
        GlStateManager.rotate(90.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        float f = entity.getTimeSinceHit() - partialTicks;
        float f1 = entity.getDamage() - partialTicks;

        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            GlStateManager.rotate(MathHelper.sin(f) * f * f1 / 10.0F * entity.getForwardDirection(), 1.0F, 0.0F, 0.0F);
        }

        GlStateManager.scale(-0.8F, -0.8F, 0.8F);
    }

    public void setupTranslation(double x, double y, double z) {
        GlStateManager.translate((float)x, (float)y + 0.2F, (float)z);
    }

    @Override
    protected ResourceLocation getEntityTexture(CarriageEntity entity) {
        return entity.getType().getTexture(entity);
    }

    /*public boolean isMultipass() {
        return true;
    }*/
}