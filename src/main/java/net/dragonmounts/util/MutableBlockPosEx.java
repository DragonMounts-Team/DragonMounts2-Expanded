package net.dragonmounts.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

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

    public MutableBlockPosEx with(double x, double y, double z) {
        this.x = MathHelper.floor(x);
        this.y = MathHelper.floor(y);
        this.z = MathHelper.floor(z);
        return this;
    }

    public MutableBlockPosEx climb() {
        ++this.y;
        return this;
    }

    public MutableBlockPosEx descent() {
        --this.y;
        return this;
    }
}