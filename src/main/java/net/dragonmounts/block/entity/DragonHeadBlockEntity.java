package net.dragonmounts.block.entity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class DragonHeadBlockEntity extends TileEntity implements ITickable {
    private float progress;
    private boolean active;

    public float getAnimationProgress(float partialTicks) {
        return this.active ? partialTicks + this.progress : this.progress;
    }

    public void update() {
        if (this.world.isBlockPowered(this.pos)) {
            this.active = true;
            ++this.progress;
        } else {
            this.active = false;
        }
    }
}
