package net.dragonmounts.client.variant;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.model.dragon.IModelFactory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;


public class DefaultAppearance extends VariantAppearance {
    public final ResourceLocation body;
    public final ResourceLocation glow;
    public final DragonModel model;

    public DefaultAppearance(ResourceLocation body, ResourceLocation glow, IModelFactory factory) {
        super(1.6F);
        this.model = new DragonModel(this, factory);
        this.body = body;
        this.glow = glow;
    }

    @Override
    public DragonModel getModel(@Nullable ClientDragonEntity dragon) {
        return this.model;
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
