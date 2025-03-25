package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.Segment;
import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import java.util.function.IntFunction;

import static net.dragonmounts.client.model.dragon.DragonModel.TAIL_SIZE;
import static net.dragonmounts.client.model.dragon.DragonModel.VERTS_TAIL;
import static net.dragonmounts.client.model.dragon.HornedTailPart.Snapshot.saveScalable;

public class HornedTailPart extends ScalablePart implements IModelPart {
    /// display horns near the tip
    public static Snapshot makeDefaultedSnapshot(int index) {
        return new HornedTailPart.Snapshot(index + 7 > VERTS_TAIL && index + 3 < VERTS_TAIL);
    }

    public static final int HORN_THICK = 3;
    public static final int HORN_LENGTH = 32;
    public final ModelRenderer leftHorn;
    public final ModelRenderer rightHorn;
    protected final Snapshot[] snapshots;

    public HornedTailPart(
            ModelBase base,
            String name,
            IntFunction<? extends Snapshot> factory
    ) {
        super(base, name);
        this.addChild(this.leftHorn = this.makeHorn(new ModelRenderer(base, name), true));
        this.addChild(this.rightHorn = this.makeHorn(new ModelRenderer(base, name), false));
        this.snapshots = DMUtils.makeArray(new Snapshot[VERTS_TAIL], factory);
    }

    /**
     * @param horn invoked with true if it is left, false otherwise
     */
    protected ModelRenderer makeHorn(ModelRenderer horn, boolean mirror) {
        horn.mirror = mirror;
        float hornOfs = horn.rotationPointY = -0.5F * HORN_THICK;
        horn.rotationPointZ = 0.5F * TAIL_SIZE;
        horn.rotateAngleX = MathX.toRadians(-15);
        horn.rotateAngleY = mirror ? MathX.toRadians(145) : MathX.toRadians(-145);
        return horn.addBox("horn", hornOfs, hornOfs, hornOfs, HORN_THICK, HORN_THICK, HORN_LENGTH);
    }

    @Override
    public void setupAnim(DragonAnimator animator) {
        Segment[] segments = animator.tailSegments;
        Snapshot[] snapshots = this.snapshots;
        final int len = Math.min(snapshots.length, segments.length);
        for (int i = 0; i < len; ++i) {
            saveScalable(segments[i], snapshots[i]);
        }
    }

    @Override
    public void render(float scale) {
        for (Snapshot snapshot : this.snapshots) {
            snapshot.apply(this);
            this.renderWithRotation(scale);
        }
    }

    public static class Snapshot extends PartSnapshot<HornedTailPart> {
        public final boolean leftHornVisible;
        public final boolean rightHornVisible;
        public float renderScaleX = 1;
        public float renderScaleY = 1;
        public float renderScaleZ = 1;

        public Snapshot(boolean visible) {
            this.leftHornVisible = this.rightHornVisible = visible;
        }

        @Override
        public void apply(HornedTailPart part) {
            super.apply(part);
            part.renderScaleX = this.renderScaleX;
            part.renderScaleY = this.renderScaleY;
            part.renderScaleZ = this.renderScaleZ;
            part.leftHorn.showModel = this.leftHornVisible;
            part.rightHorn.showModel = this.rightHornVisible;
        }

        public static void saveScalable(Segment segment, Snapshot snapshot) {
            save(segment, snapshot);
            snapshot.renderScaleX = takeIfValid(segment.scaleX, snapshot.renderScaleX);
            snapshot.renderScaleY = takeIfValid(segment.scaleY, snapshot.renderScaleY);
            snapshot.renderScaleZ = takeIfValid(segment.scaleZ, snapshot.renderScaleZ);
        }
    }
}
