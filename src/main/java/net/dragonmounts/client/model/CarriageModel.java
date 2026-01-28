package net.dragonmounts.client.model;

import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

/**
 * ModelMinecart - Either Mojang or a mod author
 * Created using Tabula 7.0.0
 */
public class CarriageModel extends ModelBase {
    public ModelRenderer field_78154_a2;
    public ModelRenderer field_78154_a3;
    public ModelRenderer field_78154_a1;
    public ModelRenderer field_78154_a6;
    public ModelRenderer field_78154_a4;
    public ModelRenderer field_78154_a5;

    public CarriageModel() {
        this.textureWidth = 128;
        this.textureHeight = 32;
        this.field_78154_a1 = new ModelRenderer(this, 0, 18);
        this.field_78154_a1.setRotationPoint(0.0F, 4.0F, -2.0F);
        this.field_78154_a1.addBox(-10.0F, -1.0F, -4.0F, 20, 1, 12, 0.0F);
        this.field_78154_a4 = new ModelRenderer(this, 40, 0);
        this.field_78154_a4.setRotationPoint(0.0F, 4.0F, -7.0F);
        this.field_78154_a4.addBox(-8.0F, -5.0F, -3.0F, 16, 4, 2, 0.0F);
        this.field_78154_a4.rotateAngleY = MathX.PI_F;
        this.field_78154_a2 = new ModelRenderer(this, 0, 0);
        this.field_78154_a2.setRotationPoint(-4.0F, 4.0F, 0.0F);
        this.field_78154_a2.addBox(-6.0F, -4.0F, -6.0F, 12, 3, 2, 0.0F);
        this.field_78154_a2.rotateAngleY = MathX.PI_F * 0.5F;
        this.field_78154_a3 = new ModelRenderer(this, 0, 0);
        this.field_78154_a3.setRotationPoint(9.0F, 4.0F, 0.0F);
        this.field_78154_a3.addBox(-6.0F, -4.0F, -1.0F, 12, 3, 2, 0.0F);
        this.field_78154_a3.rotateAngleY = MathX.PI_F * 0.5F;
        this.field_78154_a6 = new ModelRenderer(this, 68, 18);
        this.field_78154_a6.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.field_78154_a6.addBox(-8.0F, -1.0F, -6.0F, 16, 1, 12, 0.0F);
        this.field_78154_a5 = new ModelRenderer(this, 40, 0);
        this.field_78154_a5.setRotationPoint(0.0F, 4.0F, 7.0F);
        this.field_78154_a5.addBox(-8.0F, -5.0F, -3.0F, 16, 4, 2, 0.0F);
    }

    @Override
    public void render(@Nonnull Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.field_78154_a1.render(f5);
        this.field_78154_a4.render(f5);
        this.field_78154_a2.render(f5);
        this.field_78154_a3.render(f5);
        this.field_78154_a6.render(f5);
        this.field_78154_a5.render(f5);
    }
}
