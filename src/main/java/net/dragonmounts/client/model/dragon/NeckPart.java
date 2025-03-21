package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.Segment;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.DragonModel.NECK_SIZE;
import static net.dragonmounts.entity.helper.DragonHeadLocator.NECK_SEGMENTS;

public class NeckPart extends ModelPart {
    public static final int HORN_THICK = 3;
    public static final int HORN_LENGTH = 32;
    protected final Snapshot[] snapshots = DMUtils.makeArray(new Snapshot[NECK_SEGMENTS], Snapshot::new);
    public final ModelRenderer scale;

    public NeckPart(ModelBase base, String name) {
        super(base, name);
        this.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE);
        this.addChild(this.scale = new ModelRenderer(base, name).addBox("scale", -1, -7, -3, 2, 4, 6));
    }

    public void setupAnim(DragonAnimator animator) {
        Segment[] segments = animator.neckSegments;
        Snapshot[] snapshots = this.snapshots;
        final int len = Math.min(snapshots.length, segments.length);
        for (int i = 0; i < len; ++i) {
            this.applySegment(segments[i]);
            // hide the first and every second scale
            this.scale.isHidden = (i & 1) == 1 || i == 0;
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

    public static class Snapshot extends PartSnapshot<NeckPart> {
        public boolean scaleVisible;
        public float renderScaleX = 1;
        public float renderScaleY = 1;
        public float renderScaleZ = 1;

        @Override
        public void save(NeckPart part) {
            super.save(part);
            this.renderScaleX = part.renderScaleX;
            this.renderScaleY = part.renderScaleY;
            this.renderScaleZ = part.renderScaleZ;
            this.scaleVisible = part.scale.showModel;
        }

        @Override
        public void apply(NeckPart part) {
            super.apply(part);
            part.renderScaleX = this.renderScaleX;
            part.renderScaleY = this.renderScaleY;
            part.renderScaleZ = this.renderScaleZ;
            part.scale.showModel = this.scaleVisible;
        }
    }
}
