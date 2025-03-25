package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.Segment;

import java.util.function.IntPredicate;

import static net.dragonmounts.client.model.dragon.ScalablePart.ScalableSnapshot.saveScalable;
import static net.dragonmounts.entity.helper.DragonHeadLocator.NECK_SEGMENTS;

public class NeckPart implements IModelPart {
    public final ScalablePart normal;
    public final ScalablePart scaled;
    protected final ScalablePart.ScalableSnapshot[] snapshots;
    protected final ScalablePart[] parts;

    public NeckPart(ScalablePart normal, ScalablePart scaled, IntPredicate hasScale) {
        this.normal = normal;
        this.scaled = scaled;
        ScalablePart.ScalableSnapshot[] snapshots = new ScalablePart.ScalableSnapshot[NECK_SEGMENTS];
        ScalablePart[] parts = new ScalablePart[NECK_SEGMENTS];
        for (int i = 0; i < NECK_SEGMENTS; ++i) {
            snapshots[i] = new ScalablePart.ScalableSnapshot();
            parts[i] = hasScale.test(i) ? scaled : normal;
        }
        this.snapshots = snapshots;
        this.parts = parts;
    }

    @Override
    public void setupAnim(DragonAnimator animator) {
        Segment[] segments = animator.neckSegments;
        ScalablePart.ScalableSnapshot[] snapshots = this.snapshots;
        final int len = Math.min(snapshots.length, segments.length);
        for (int i = 0; i < len; ++i) {
            saveScalable(segments[i], snapshots[i]);
        }
    }

    @Override
    public void render(float scale) {
        ScalablePart[] parts = this.parts;
        ScalablePart.ScalableSnapshot[] snapshots = this.snapshots;
        for (int i = 0; i < NECK_SEGMENTS; ++i) {
            final ScalablePart part = parts[i];
            snapshots[i].apply(part);
            part.renderWithRotation(scale);
        }
    }
}
