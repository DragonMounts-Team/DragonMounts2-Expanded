package net.dragonmounts.client.variant;

import com.google.common.collect.ImmutableList;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.render.dragon.BuiltinDragonLayer;
import net.dragonmounts.client.render.dragon.IDragonLayer;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.dragonmounts.DragonMounts.makeId;

public abstract class VariantAppearance {
    public final static String TEXTURES_ROOT = "textures/entities/dragon/";
    public final static ResourceLocation DEFAULT_CHEST = makeId(TEXTURES_ROOT + "chest.png");
    public final static ResourceLocation DEFAULT_SADDLE = makeId(TEXTURES_ROOT + "saddle.png");
    public final static ResourceLocation DEFAULT_DISSOLVE = makeId(TEXTURES_ROOT + "dissolve.png");
    public final ImmutableList<IDragonLayer> layers;

    public VariantAppearance() {
        this.layers = this.getLayers();
    }

    protected ImmutableList<IDragonLayer> getLayers() {
        return BuiltinDragonLayer.DEFAULT_LAYERS;
    }

    public abstract DragonModel getModel(@Nullable ClientDragonEntity dragon);

    public abstract ResourceLocation getBody(@Nullable ClientDragonEntity dragon);

    public abstract ResourceLocation getGlow(@Nullable ClientDragonEntity dragon);

    public abstract ResourceLocation getDissolve(@Nullable ClientDragonEntity dragon);

    public ResourceLocation getChest(@Nullable ClientDragonEntity dragon) {
        return DEFAULT_CHEST;
    }

    public ResourceLocation getSaddle(@Nullable ClientDragonEntity dragon) {
        return DEFAULT_SADDLE;
    }

    public abstract void spawnBreathParticle(World level, Vec3d position, Vec3d direction, BreathPower power, float partialTicks);
}
