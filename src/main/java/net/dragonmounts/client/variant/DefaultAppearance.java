package net.dragonmounts.client.variant;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.model.dragon.IModelFactory;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;


public class DefaultAppearance extends VariantAppearance {
    public final ResourceLocation body;
    public final ResourceLocation glow;
    public final boolean hasTailHorns;
    public final boolean hasSideTailScale;
    public final DragonModel model;

    public DefaultAppearance(ResourceLocation body, ResourceLocation glow, boolean hasTailHorns, boolean hasSideTailScale, IModelFactory factory) {
        super(1.6F);
        this.model = new DragonModel(this, factory);
        this.body = body;
        this.glow = glow;
        this.hasTailHorns = hasTailHorns;
        this.hasSideTailScale = hasSideTailScale;
    }

    @Override
    public DragonModel getModel(@Nullable ClientDragonEntity dragon) {
        return this.model;
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
    public ResourceLocation getBody(ClientDragonEntity dragon) {
        return this.body;
    }

    @Override
    public ResourceLocation getGlow(ClientDragonEntity dragon) {
        return this.glow;
    }
}
