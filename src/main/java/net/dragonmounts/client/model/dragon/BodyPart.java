package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class BodyPart extends ModelRenderer {
    public final ModelRenderer back;

    public BodyPart(ModelBase base, String name, ModelRenderer back) {
        super(base, name);
        this.addChild(this.back = back);
    }

    public void setupAnim(DragonAnimator animator) {
        this.back.isHidden = animator.saddled;
    }
}
