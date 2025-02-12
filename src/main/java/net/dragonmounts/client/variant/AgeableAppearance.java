package net.dragonmounts.client.variant;


import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.util.ResourceLocation;

public class AgeableAppearance extends VariantAppearance {
    public final ResourceLocation body;
    public final ResourceLocation babyBody;
    public final ResourceLocation glow;
    public final ResourceLocation babyGlow;
    public final boolean hasTailHorns;
    public final boolean hasSideTailScale;

    public AgeableAppearance(
            ResourceLocation body,
            ResourceLocation babyBody,
            ResourceLocation babyGlow,
            ResourceLocation glow,
            boolean hasTailHorns,
            boolean hasSideTailScale,
            boolean isSkeleton
    ) {
        super(1.6F, isSkeleton);
        this.body = body;
        this.babyBody = babyBody;
        this.glow = glow;
        this.babyGlow = babyGlow;
        this.hasTailHorns = hasTailHorns;
        this.hasSideTailScale = hasSideTailScale;
    }

    @Override
    public boolean hasTailHorns(TameableDragonEntity dragon) {
        return this.hasTailHorns;
    }

    @Override
    public boolean hasSideTailScale(TameableDragonEntity dragon) {
        return this.hasSideTailScale;
    }

    @Override
    public boolean hasTailHornsOnShoulder() {
        return this.hasTailHorns;
    }

    @Override
    public boolean hasSideTailScaleOnShoulder() {
        return this.hasSideTailScale;
    }

    @Override
    public ResourceLocation getBody(TameableDragonEntity dragon) {
        return dragon != null && dragon.isChild() ? this.babyBody : this.body;
    }

    @Override
    public ResourceLocation getGlow(TameableDragonEntity dragon) {
        return dragon != null && dragon.isChild() ? this.babyGlow : this.glow;
    }
}
