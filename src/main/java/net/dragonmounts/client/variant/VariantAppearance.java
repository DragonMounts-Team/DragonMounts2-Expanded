package net.dragonmounts.client.variant;

import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.minecraft.util.ResourceLocation;

import static net.dragonmounts.DragonMounts.makeId;

public abstract class VariantAppearance {
    public final static String TEXTURES_ROOT = "textures/entities/dragon/";
    public final static ResourceLocation DEFAULT_CHEST = makeId(TEXTURES_ROOT + "chest.png");
    public final static ResourceLocation DEFAULT_SADDLE = makeId(TEXTURES_ROOT + "saddle.png");
    public final static ResourceLocation DEFAULT_DISSOLVE = makeId(TEXTURES_ROOT + "dissolve.png");
    public final float positionScale;
    public final float renderScale;

    public VariantAppearance(float modelScale) {
        this.renderScale = modelScale;
        this.positionScale = modelScale / 16.0F;
    }

    public abstract boolean hasTailHorns(EntityTameableDragon dragon);

    public abstract boolean hasSideTailScale(EntityTameableDragon dragon);

    public abstract boolean hasTailHornsOnShoulder();

    public abstract boolean hasSideTailScaleOnShoulder();

    public abstract ResourceLocation getBody(EntityTameableDragon dragon);

    public abstract ResourceLocation getGlow(EntityTameableDragon dragon);

    public ResourceLocation getChest(EntityTameableDragon dragon) {
        return DEFAULT_CHEST;
    }

    public ResourceLocation getSaddle(EntityTameableDragon dragon) {
        return DEFAULT_SADDLE;
    }

    public ResourceLocation getDissolve(EntityTameableDragon dragon) {
        return DEFAULT_DISSOLVE;
    }
}
