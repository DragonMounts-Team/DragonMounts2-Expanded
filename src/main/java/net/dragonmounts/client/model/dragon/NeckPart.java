package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.Segment;

import java.util.function.IntPredicate;

import static net.dragonmounts.client.model.dragon.ModelPart.ScalableSnapshot.saveScalable;
import static net.dragonmounts.entity.helper.DragonHeadLocator.NECK_SEGMENTS;

public class NeckPart implements ICachedPart {
    public final ModelPart normal;
    public final ModelPart scaled;
    protected final ModelPart.ScalableSnapshot[] snapshots;
    protected final ModelPart[] parts;

    public NeckPart(ModelPart normal, ModelPart scaled, IntPredicate hasScale) {
        this.normal = normal;
        this.scaled = scaled;
        ModelPart.ScalableSnapshot[] snapshots = new ModelPart.ScalableSnapshot[NECK_SEGMENTS];
        ModelPart[] parts = new ModelPart[NECK_SEGMENTS];
        for (int i = 0; i < NECK_SEGMENTS; ++i) {
            snapshots[i] = new ModelPart.ScalableSnapshot();
            parts[i] = hasScale.test(i) ? scaled : normal;
        }
        this.snapshots = snapshots;
        this.parts = parts;
    }

    @Override
    public void setupAnim(DragonAnimator animator) {
        Segment[] segments = animator.neckSegments;
        ModelPart.ScalableSnapshot[] snapshots = this.snapshots;
        final int len = Math.min(snapshots.length, segments.length);
        for (int i = 0; i < len; ++i) {
            saveScalable(segments[i], snapshots[i]);
        }
    }

    @Override
    public void render(float scale) {
        ModelPart[] parts = this.parts;
        ModelPart.ScalableSnapshot[] snapshots = this.snapshots;
        for (int i = 0; i < NECK_SEGMENTS; ++i) {
            final ModelPart part = parts[i];
            snapshots[i].apply(part);
            part.renderWithRotation(scale);
        }
    }
}
