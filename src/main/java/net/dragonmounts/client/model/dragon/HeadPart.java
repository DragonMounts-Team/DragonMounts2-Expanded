package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.Segment;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.BuiltinFactory.HEAD_OFS;
import static net.dragonmounts.client.model.dragon.BuiltinFactory.makeHorn;
import static net.dragonmounts.client.model.dragon.PartSnapshot.takeIfValid;

public class HeadPart extends ModelRenderer implements IModelPart {
    public static HeadPart buildBasicHead(ModelBase root) {
        HeadPart head = new HeadPart(root, "head", new ModelRenderer(root, "head")
                .addBox("lowerjaw", -6, 0, -16, 12, 4, 16)
        );
        head.addBox("upperjaw", -6, -1, -8 + HEAD_OFS, 12, 5, 16);
        head.addBox("mainhead", -8, -8, 6 + HEAD_OFS, 16, 16, 16);
        head.jaw.setRotationPoint(0, 4, 8 + HEAD_OFS);
        head.addChild(makeHorn(root, head, false));
        head.addChild(makeHorn(root, head, true));
        return head;
    }
    public final ModelRenderer jaw;

    public HeadPart(ModelBase base, String name, ModelRenderer jaw) {
        super(base, name);
        this.addChild(this.jaw = jaw);
    }

    @Override
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
