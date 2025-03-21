package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.Segment;
import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.DragonModel.TAIL_SIZE;
import static net.dragonmounts.client.model.dragon.DragonModel.VERTS_TAIL;

public class TailPart extends ModelPart {
    public static final int HORN_THICK = 3;
    public static final int HORN_LENGTH = 32;
    protected final Snapshot[] snapshots = DMUtils.makeArray(new Snapshot[VERTS_TAIL], Snapshot::new);
    public final ModelRenderer middleScale;
    public final ModelRenderer leftScale;
    public final ModelRenderer rightScale;
    public final ModelRenderer leftHorn;
    public final ModelRenderer rightHorn;

    public TailPart(ModelBase base, String name) {
        super(base, name);
        this.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE);
        this.addChild(this.middleScale = new ModelRenderer(base, name).addBox("scale", -1, -8, -3, 2, 4, 6));
        this.addChild(this.leftScale = new ModelRenderer(base, name).addBox("scale", -1, -8, -3, 2, 4, 6));
        this.addChild(this.rightScale = new ModelRenderer(base, name).addBox("scale", -1, -8, -3, 2, 4, 6));
        this.addChild(this.leftHorn = makeHorn(new ModelRenderer(base, name), true));
        this.addChild(this.rightHorn = makeHorn(new ModelRenderer(base, name), false));
        float scaleRotZ = MathX.toRadians(45);
        this.leftScale.rotateAngleZ = scaleRotZ;
        this.rightScale.rotateAngleZ = -scaleRotZ;
    }

    private ModelRenderer makeHorn(ModelRenderer horn, boolean mirror) {
        horn.mirror = mirror;
        float hornOfs = horn.rotationPointY = -(HORN_THICK / 2f);
        horn.rotationPointZ = TAIL_SIZE / 2f;
        horn.rotateAngleX = MathX.toRadians(-15);
        horn.rotateAngleY = mirror ? -MathX.toRadians(-145) : MathX.toRadians(-145);
        horn.isHidden = true;
        return horn.addBox("horn", hornOfs, hornOfs, hornOfs, HORN_THICK, HORN_THICK, HORN_LENGTH);
    }

    public void setupAnim(DragonAnimator animator) {
        Segment[] segments = animator.tailSegments;
        Snapshot[] snapshots = this.snapshots;
        final int len = Math.min(snapshots.length, segments.length);
        for (int i = 0; i < len; ++i) {
            this.applySegment(segments[i]);
            // display horns near the tip
            this.leftHorn.isHidden = this.rightHorn.isHidden = !(i > len - 7 && i < len - 3);
            snapshots[i].save(this);
        }
    }

    @Override
    public void render(float scale) {
        for (Snapshot snapshot : this.snapshots) {
            snapshot.apply(this);
            this.renderWithRotation(scale);
        }
    }

    public static class Snapshot extends PartSnapshot<TailPart> {
        public boolean middleScaleVisible;
        public boolean leftScaleVisible;
        public boolean rightScaleVisible;
        public boolean leftHornVisible;
        public boolean rightHornVisible;
        public float renderScaleX = 1;
        public float renderScaleY = 1;
        public float renderScaleZ = 1;

        @Override
        public void save(TailPart part) {
            super.save(part);
            this.renderScaleX = part.renderScaleX;
            this.renderScaleY = part.renderScaleY;
            this.renderScaleZ = part.renderScaleZ;
            this.middleScaleVisible = part.middleScale.showModel;
            this.leftScaleVisible = part.leftScale.showModel;
            this.rightScaleVisible = part.rightScale.showModel;
            this.leftHornVisible = part.leftHorn.showModel;
            this.rightHornVisible = part.rightHorn.showModel;
        }

        @Override
        public void apply(TailPart part) {
            super.apply(part);
            part.renderScaleX = this.renderScaleX;
            part.renderScaleY = this.renderScaleY;
            part.renderScaleZ = this.renderScaleZ;
            part.middleScale.showModel = this.middleScaleVisible;
            part.leftScale.showModel = this.leftScaleVisible;
            part.rightScale.showModel = this.rightScaleVisible;
            part.leftHorn.showModel = this.leftHornVisible;
            part.rightHorn.showModel = this.rightHornVisible;
        }
    }
}
