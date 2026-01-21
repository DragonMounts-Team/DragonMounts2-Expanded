package net.dragonmounts.client.variant;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.breath.BuiltinBreathTextures;
import net.dragonmounts.client.breath.IBreathParticleFactory;
import net.dragonmounts.client.breath.impl.FlameBreathParticle;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.model.dragon.IModelFactory;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

public class DefaultAppearance extends VariantAppearance {
    private static final Object2ObjectOpenHashMap<String, Map<ResourceLocation, ResourceLocation>> ARMOR_TEXTURES = new Object2ObjectOpenHashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> DEFAULT_ARMOR_TEXTURES = getTextures(null);

    synchronized static Map<ResourceLocation, ResourceLocation> getTextures(@Nullable String category) {
        return ARMOR_TEXTURES.computeIfAbsent(category, $ -> new Object2ObjectOpenHashMap<>());
    }

    public synchronized static void registerArmorTexture(@Nullable String category, ResourceLocation material, ResourceLocation texture) {
        if (getTextures(category).put(material, texture) != null) {
            throw new IllegalStateException("Duplicate category: " + material);
        }
    }
    public final IBreathParticleFactory factory;
    public final ResourceLocation breath;
    public final ResourceLocation body;
    public final ResourceLocation glow;
    public final ResourceLocation decal;
    public final DragonModel model;
    final Map<ResourceLocation, ResourceLocation> armors;

    public DefaultAppearance(
            IModelFactory model,
            ResourceLocation body,
            ResourceLocation glow,
            ResourceLocation decal,
            ResourceLocation breath,
            Map<ResourceLocation, ResourceLocation> armors,
            IBreathParticleFactory factory
    ) {
        this.model = new DragonModel(model);
        this.body = body;
        this.glow = glow;
        this.decal = decal;
        this.breath = breath;
        this.factory = factory;
        this.armors = armors;
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

    @Override
    public ResourceLocation getDissolve(ClientDragonEntity dragon) {
        return this.decal;
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ResourceLocation material) {
        ResourceLocation override = this.armors.get(material);
        return override == null ? DEFAULT_ARMOR_TEXTURES.get(material) : override;
    }

    @Override
    public void spawnBreathParticle(World level, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        level.spawnEntity(this.factory.createParticle(level, position, direction, power, this.breath, partialTicks));
    }

    public static class Builder {
        public final IModelFactory model;
        public IBreathParticleFactory factory = FlameBreathParticle.FACTORY;
        public ResourceLocation breath = BuiltinBreathTextures.FLAME_BREATH;
        public ResourceLocation decal = DEFAULT_DISSOLVE;
        Map<ResourceLocation, ResourceLocation> armors = DEFAULT_ARMOR_TEXTURES;

        public Builder(IModelFactory model) {
            this.model = model;
        }

        public Builder setArmorCategory(@Nullable String category) {
            this.armors = getTextures(category);
            return this;
        }

        public Builder withBreath(ResourceLocation breath) {
            this.breath = breath;
            return this;
        }

        public Builder withBreath(ResourceLocation breath, IBreathParticleFactory factory) {
            this.factory = factory;
            return this.withBreath(breath);
        }

        public Builder withDecal(ResourceLocation decal) {
            this.decal = decal;
            return this;
        }

        public DefaultAppearance build(String namespace, String path) {
            return this.build(
                    new ResourceLocation(namespace, TEXTURES_ROOT + path + "/body.png"),
                    new ResourceLocation(namespace, TEXTURES_ROOT + path + "/glow.png")
            );
        }

        public DefaultAppearance build(ResourceLocation body, ResourceLocation glow) {
            return new DefaultAppearance(this.model, body, glow, this.decal, this.breath, this.armors, this.factory);
        }
    }
}
