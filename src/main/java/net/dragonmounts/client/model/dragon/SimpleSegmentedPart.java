package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.Segment;
import net.minecraft.client.model.ModelBase;

import java.util.Arrays;
import java.util.function.IntFunction;

import static net.dragonmounts.client.model.dragon.PartSnapshot.save;

public class SimpleSegmentedPart extends ScalablePart implements ISegmentedPart {
    protected final ScalablePart.Snapshot[] snapshots;

    public SimpleSegmentedPart(ModelBase base, String name, int length, IntFunction<Snapshot> factory) {
        super(base, name);
        Arrays.setAll(this.snapshots = new ScalablePart.Snapshot[length], factory);
    }

    @Override
    public void setupSegments(Segment[] segments) {
        ScalablePart.Snapshot[] snapshots = this.snapshots;
        for (int i = 0, len = Math.min(snapshots.length, segments.length); i < len; ++i) {
            save(segments[i], snapshots[i]);
        }
    }

    @Override
    @Deprecated
    public void render(float scale) {
        this.renderSegments(scale);
    }

    @Override
    public void renderSegments(float scale) {
        for (ScalablePart.Snapshot snapshot : this.snapshots) {
            snapshot.apply(this);
            this.renderWithRotation(scale);
        }
    }
}
