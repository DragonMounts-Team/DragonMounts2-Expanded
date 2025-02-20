package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.DragonModel.NECK_SIZE;
import static net.dragonmounts.client.model.dragon.DragonModel.VERTS_NECK;

public class NeckPart extends ModelPart {
    public static final int HORN_THICK = 3;
    public static final int HORN_LENGTH = 32;
    protected final Snapshot[] snapshots;
    public final ModelRenderer scale;

    public NeckPart(ModelBase base, String name) {
        super(base, name);
        this.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE);
        this.addChild(this.scale = new ModelRenderer(base, name).addBox("scale", -1, -7, -3, 2, 4, 6));
        Snapshot[] snapshots = new Snapshot[VERTS_NECK];
        for (int i = 0; i < snapshots.length; ++i) {
            snapshots[i] = new Snapshot();
        }
        this.snapshots = snapshots;
    }

    public void save(int index) {
        if (index < 0 || index >= this.snapshots.length) throw new IndexOutOfBoundsException();
        this.snapshots[index].save(this);
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

        @Override
        public void save(NeckPart part) {
            super.save(part);
            this.scaleVisible = part.scale.showModel;
        }

        @Override
        public void apply(NeckPart part) {
            super.apply(part);
            part.scale.showModel = this.scaleVisible;
        }
    }
}
