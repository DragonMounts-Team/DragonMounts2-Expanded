package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.Segment;

public interface ISegmentedPart {
    void setupSegments(Segment[] segments);

    void renderSegments(float scale);
}
