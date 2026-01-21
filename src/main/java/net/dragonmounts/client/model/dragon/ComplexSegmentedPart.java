package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.Segment;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

import static net.dragonmounts.client.model.dragon.PartSnapshot.save;

public class ComplexSegmentedPart implements ISegmentedPart {
    public static ComplexSegmentedPart dualSegmented(
            ScalablePart normal,
            ScalablePart special,
            int segments,
            IntFunction<ScalablePart.Snapshot> factory,
            IntPredicate isSpecial
    ) {
        ScalablePart[] parts = new ScalablePart[segments];
        for (int i = 0; i < segments; ++i) {
            parts[i] = isSpecial.test(i) ? special : normal;
        }
        return new ComplexSegmentedPart(parts, factory);
    }

    protected final ScalablePart.Snapshot[] snapshots;
    protected final ScalablePart[] parts;

    public ComplexSegmentedPart(ScalablePart[] parts, IntFunction<ScalablePart.Snapshot> factory) {
        this.parts = parts;
        Arrays.setAll(this.snapshots = new ScalablePart.Snapshot[parts.length], factory);
    }

    @Override
    public void setupSegments(Segment[] segments) {
        ScalablePart.Snapshot[] snapshots = this.snapshots;
        for (int i = 0, len = Math.min(snapshots.length, segments.length); i < len; ++i) {
            save(segments[i], snapshots[i]);
        }
    }

    @Override
    public void renderSegments(float scale) {
        ScalablePart[] parts = this.parts;
        ScalablePart.Snapshot[] snapshots = this.snapshots;
        for (int i = 0, len = parts.length; i < len; ++i) {
            final ScalablePart part = parts[i];
            snapshots[i].apply(part);
            part.renderWithRotation(scale);
        }
    }
}
