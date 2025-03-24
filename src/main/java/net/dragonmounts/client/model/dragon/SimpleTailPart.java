package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.Segment;
import net.minecraft.client.model.ModelBase;

import static net.dragonmounts.client.model.dragon.DragonModel.VERTS_TAIL;
import static net.dragonmounts.client.model.dragon.ModelPart.ScalableSnapshot.saveScalable;

public class SimpleTailPart extends ModelPart implements ICachedPart {
    protected final ScalableSnapshot[] snapshots;

    public SimpleTailPart(ModelBase base, String name) {
        super(base, name);
        this.snapshots = DMUtils.makeArray(new ScalableSnapshot[VERTS_TAIL], ScalableSnapshot::new);
    }

    @Override
    public void setupAnim(DragonAnimator animator) {
        Segment[] segments = animator.tailSegments;
        ScalableSnapshot[] snapshots = this.snapshots;
        final int len = Math.min(snapshots.length, segments.length);
        for (int i = 0; i < len; ++i) {
            saveScalable(segments[i], snapshots[i]);
        }
    }

    @Override
    public void render(float scale) {
        for (ScalableSnapshot snapshot : this.snapshots) {
            snapshot.apply(this);
            this.renderWithRotation(scale);
        }
    }
}
