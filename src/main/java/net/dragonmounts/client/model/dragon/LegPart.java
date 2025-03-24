package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class LegPart extends ModelRenderer {
    public final ModelRenderer shank;
    public final ModelRenderer foot;
    public final ModelRenderer toe;
    public final Snapshot left = new Snapshot();
    public final Snapshot right = new Snapshot();

    public LegPart(
            ModelBase base,
            String name,
            ModelRenderer shank,
            ModelRenderer foot,
            ModelRenderer toe
    ) {
        super(base, name);
        this.addChild(this.shank = shank);
        shank.addChild(this.foot = foot);
        foot.addChild(this.toe = toe);
    }

    public static class Snapshot extends PartSnapshot<LegPart> {
        public float shankRotX;
        public float footRotX;
        public float toeRotX;

        public void save(LegPart part) {
            this.rotationPointX = takeIfValid(part.rotationPointX, this.rotationPointX);
            this.rotationPointY = takeIfValid(part.rotationPointY, this.rotationPointY);
            this.rotationPointZ = takeIfValid(part.rotationPointZ, this.rotationPointZ);
            this.rotateAngleX = takeIfValid(part.rotateAngleX, this.rotateAngleX);
            this.rotateAngleY = takeIfValid(part.rotateAngleY, this.rotateAngleY);
            this.rotateAngleZ = takeIfValid(part.rotateAngleZ, this.rotateAngleZ);
            this.shankRotX = part.shank.rotateAngleX;
            this.footRotX = part.foot.rotateAngleX;
            this.toeRotX = part.toe.rotateAngleX;
        }

        @Override
        public void apply(LegPart part) {
            super.apply(part);
            part.shank.rotateAngleX = this.shankRotX;
            part.foot.rotateAngleX = this.footRotX;
            part.toe.rotateAngleX = this.toeRotX;
        }
    }
}
