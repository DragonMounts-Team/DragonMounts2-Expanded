package net.dragonmounts.client.variant;

import com.google.common.collect.ImmutableList;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.render.dragon.layer.*;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

import static net.dragonmounts.DragonMounts.makeId;

public abstract class VariantAppearance {
    public final static String TEXTURES_ROOT = "textures/entities/dragon/";
    public final static ResourceLocation DEFAULT_CHEST = makeId(TEXTURES_ROOT + "chest.png");
    public final static ResourceLocation DEFAULT_SADDLE = makeId(TEXTURES_ROOT + "saddle.png");
    public final static ResourceLocation DEFAULT_DISSOLVE = makeId(TEXTURES_ROOT + "dissolve.png");
    public final float positionScale;
    public final float renderScale;
    public final boolean isSkeleton;
    public final DragonModel model = new DragonModel(this);
    public final ImmutableList<DragonLayerRenderer> layers;

    public VariantAppearance(float modelScale, boolean isSkeleton) {
        this.renderScale = modelScale;
        this.positionScale = modelScale / 16.0F;
        this.isSkeleton = isSkeleton;
        this.layers = this.getLayers();
    }

    protected ImmutableList<DragonLayerRenderer> getLayers() {
        return ImmutableList.of(
                // standard layers
                new LayerRendererDragonGlow(),
//        layers.add(new LayerRendererDragonGlowAnim(parent, this, model),
                new LayerRendererDragonSaddle(),
                new LayerRendererDragonArmor(),
                new LayerRendererDragonChest(),
                new LayerRendererDragonBanner()
        );
    }

    public abstract boolean hasTailHorns(TameableDragonEntity dragon);

    public abstract boolean hasSideTailScale(TameableDragonEntity dragon);

    public abstract boolean hasTailHornsOnShoulder();

    public abstract boolean hasSideTailScaleOnShoulder();

    public abstract ResourceLocation getBody(@Nullable TameableDragonEntity dragon);

    public abstract ResourceLocation getGlow(@Nullable TameableDragonEntity dragon);

    public ResourceLocation getChest(TameableDragonEntity dragon) {
        return DEFAULT_CHEST;
    }

    public ResourceLocation getSaddle(TameableDragonEntity dragon) {
        return DEFAULT_SADDLE;
    }

    public ResourceLocation getDissolve(TameableDragonEntity dragon) {
        return DEFAULT_DISSOLVE;
    }
}
