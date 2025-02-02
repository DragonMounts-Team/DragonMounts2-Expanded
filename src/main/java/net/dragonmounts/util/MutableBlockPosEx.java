package net.dragonmounts.util;

import net.minecraft.util.math.BlockPos;

public class MutableBlockPosEx extends BlockPos.MutableBlockPos {
    public MutableBlockPosEx(int x, int y, int z) {
        super(x, y, z);
    }

    public MutableBlockPosEx withX(int x) {
        this.x = x;
        return this;
    }

    public MutableBlockPosEx withY(int y) {
        this.y = y;
        return this;
    }

    public MutableBlockPosEx withZ(int z) {
        this.z = z;
        return this;
    }

    public MutableBlockPosEx with(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
}