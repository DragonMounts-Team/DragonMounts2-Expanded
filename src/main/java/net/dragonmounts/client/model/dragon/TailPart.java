package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.DragonModel.TAIL_SIZE;
import static net.dragonmounts.client.model.dragon.DragonModel.VERTS_TAIL;

public class TailPart extends ModelPart {
    public static final int HORN_THICK = 3;
    public static final int HORN_LENGTH = 32;
    protected final Snapshot[] snapshots;
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
        Snapshot[] snapshots = new Snapshot[VERTS_TAIL];
        for (int i = 0; i < snapshots.length; ++i) {
            snapshots[i] = new Snapshot();
        }
        this.snapshots = snapshots;
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

    public void save(int index) {
        if (index < 0 || index >= this.snapshots.length) throw new IndexOutOfBoundsException();
        this.snapshots[index].save(this);
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

        @Override
        public void save(TailPart part) {
            super.save(part);
            this.middleScaleVisible = part.middleScale.showModel;
            this.leftScaleVisible = part.leftScale.showModel;
            this.rightScaleVisible = part.rightScale.showModel;
            this.leftHornVisible = part.leftHorn.showModel;
            this.rightHornVisible = part.rightHorn.showModel;
        }

        @Override
        public void apply(TailPart part) {
            super.apply(part);
            part.middleScale.showModel = this.middleScaleVisible;
            part.leftScale.showModel = this.leftScaleVisible;
            part.rightScale.showModel = this.rightScaleVisible;
            part.leftHorn.showModel = this.leftHornVisible;
            part.rightHorn.showModel = this.rightHornVisible;
        }
    }
}
