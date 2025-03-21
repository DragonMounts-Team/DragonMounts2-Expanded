package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.Segment;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.ModelPart.takeIfValid;

public class HeadPart extends ModelRenderer {
    public final ModelRenderer jaw;

    public HeadPart(ModelBase base, String name, ModelRenderer jaw) {
        super(base, name);
        this.addChild(this.jaw = jaw);
    }

    public void setupAnim(DragonAnimator animator) {
        Segment segment = animator.head;
        this.rotateAngleX = takeIfValid(segment.rotX, this.rotateAngleX);
        this.rotateAngleY = takeIfValid(segment.rotY, this.rotateAngleY);
        this.rotateAngleZ = takeIfValid(segment.rotZ, this.rotateAngleZ);
        this.rotationPointX = takeIfValid(segment.posX, this.rotationPointX);
        this.rotationPointY = takeIfValid(segment.posY, this.rotationPointY);
        this.rotationPointZ = takeIfValid(segment.posZ, this.rotationPointZ);
        this.jaw.rotateAngleX = animator.getJawRotateAngleX();
    }
}
