package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class WingPart extends ModelRenderer {
    public static final int WING_FINGERS = 4;
    public final ModelRenderer forearm;
    protected final ModelRenderer[] fingers;

    public WingPart(ModelBase base, String name, ModelRenderer forearm, ModelRenderer... fingers) {
        super(base, name);
        if (fingers.length != WING_FINGERS) throw new IllegalArgumentException();
        this.addChild(this.forearm = forearm);
        for (ModelRenderer finger : this.fingers = fingers) {
            forearm.addChild(finger);
        }
    }

    public void setupAnim(DragonAnimator animator) {
        // apply angles
        this.rotateAngleX = animator.getWingArmRotateAngleX();
        this.rotateAngleY = animator.getWingArmRotateAngleY();
        this.rotateAngleZ = animator.getWingArmRotateAngleZ();
        this.forearm.rotateAngleX = animator.getWingForearmRotateAngleX();
        this.forearm.rotateAngleY = animator.getWingForearmRotateAngleY();
        this.forearm.rotateAngleZ = animator.getWingForearmRotateAngleZ();

        // set wing finger angles
        ModelRenderer[] fingers = this.fingers;
        for (int i = 0; i < fingers.length; ++i) {
            fingers[i].rotateAngleX = animator.getWingFingerRotateX(i);
            fingers[i].rotateAngleY = animator.getWingFingerRotateY(i);
        }
    }
}
