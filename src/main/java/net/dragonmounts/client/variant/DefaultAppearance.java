package net.dragonmounts.client.variant;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.breath.BuiltinBreathTextures;
import net.dragonmounts.client.breath.IBreathParticleFactory;
import net.dragonmounts.client.breath.impl.SimpleBreathParticle;
import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.model.dragon.IModelFactory;
import net.dragonmounts.entity.breath.BreathPower;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DefaultAppearance extends VariantAppearance {
    public final IBreathParticleFactory factory;
    public final ResourceLocation breath;
    public final ResourceLocation body;
    public final ResourceLocation glow;
    public final ResourceLocation decal;
    public final DragonModel model;

    public DefaultAppearance(
            IModelFactory model,
            ResourceLocation body,
            ResourceLocation glow,
            ResourceLocation decal,
            ResourceLocation breath,
            IBreathParticleFactory factory
    ) {
        this.model = new DragonModel(model);
        this.body = body;
        this.glow = glow;
        this.decal = decal;
        this.breath = breath;
        this.factory = factory;
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
    public void spawnBreathParticle(World level, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        level.spawnEntity(this.factory.createParticle(level, position, direction, power, this.breath, partialTicks));
    }

    public static class Builder {
        public final IModelFactory model;
        public IBreathParticleFactory factory = SimpleBreathParticle.FACTORY;
        public ResourceLocation breath = BuiltinBreathTextures.FLAME_BREATH;
        public ResourceLocation decal = DEFAULT_DISSOLVE;

        public Builder(IModelFactory model) {
            this.model = model;
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
            return new DefaultAppearance(this.model, body, glow, this.decal, this.breath, this.factory);
        }
    }
}
