package net.dragonmounts.registry;

import com.google.common.collect.ImmutableMultimap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.FireBreath;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.util.DMUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.registries.*;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.dragonmounts.DragonMounts.makeId;

public class DragonType extends IForgeRegistryEntry.Impl<DragonType> {
    public static final String DATA_PARAMETER_KEY = "DragonType";
    public static final ResourceLocation DEFAULT_KEY = makeId("ender");
    public static final DeferredRegistry<DragonType> REGISTRY = new Registry(makeId("dragon_type"), new RegistryBuilder<DragonType>().setDefaultKey(DEFAULT_KEY));

    public final int color;
    public final boolean convertible;
    public final boolean isSkeleton;
    public final boolean avoidWater;
    public final ResourceLocation identifier;
    public final String translationKey;
    public final TextFormatting formatting;
    public final ImmutableMultimap<String, AttributeModifier> attributes;
    public final DragonVariant.Manager variants = new DragonVariant.Manager(this);
    private final Reference2ObjectOpenHashMap<Class<?>, Object> map = new Reference2ObjectOpenHashMap<>();
    private final ReferenceOpenHashSet<DamageSource> immunities;
    private final ReferenceOpenHashSet<Block> blocks;
    private final ReferenceOpenHashSet<Biome> biomes;
    public final EnumParticleTypes sneezeParticle;
    public final EnumParticleTypes eggParticle;
    @Nullable
    public final ResourceLocation lootTable;

    public DragonType(ResourceLocation identifier, DragonTypeBuilder builder) {
        this.color = builder.color;
        this.convertible = builder.convertible;
        this.isSkeleton = builder.isSkeleton;
        this.avoidWater = builder.avoidWater;
        this.formatting = builder.formatting;
        this.attributes = ImmutableMultimap.copyOf(builder.attributes);
        this.immunities = new ReferenceOpenHashSet<>(builder.immunities);
        this.blocks = new ReferenceOpenHashSet<>(builder.blocks);
        this.biomes = new ReferenceOpenHashSet<>(builder.biomes);
        this.sneezeParticle = builder.sneezeParticle;
        this.eggParticle = builder.eggParticle;
        this.lootTable = builder.hasLootTable ? (builder.lootTable == null ? identifier : builder.lootTable) : null;
        this.setRegistryName(this.identifier = identifier);
        this.translationKey = DMUtils.makeDescriptionId("dragon_type", identifier);
    }

    public void tickServer(ServerDragonEntity dragon) {}

    /// Do **NOT** directly access client only class here!
    public void tickClient(ClientDragonEntity dragon) {}

    public boolean isHabitatEnvironment(Entity egg) {
        return false;
    }

    public Vec3d locatePassenger(int index, boolean sitting, float scale) {
        // dragon position is the middle of the model, and the saddle is on
        // the shoulders, so move player forwards on Z axis relative to the
        // dragon's rotation to fix that
        switch (index) {
            case 1:
                return new Vec3d(
                        0.375 * scale,
                        sitting ? 2.125 * scale : 2.75 * scale,
                        0.0625 * scale
                );
            case 2:
                return new Vec3d(
                        -0.375 * scale,
                        sitting ? 2.125 * scale : 2.75 * scale,
                        0.0625 * scale
                );
            case 3:
                return new Vec3d(
                        scale,
                        sitting ? 1.3125 * scale : 1.5625 * scale,
                        0.125 * scale
                );
            case 4:
                return new Vec3d(
                        -scale,
                        sitting ? 1.3125 * scale : 1.5625 * scale,
                        0.125 * scale
                );
            default:
                return new Vec3d(
                        0,
                        sitting ? 2.125 * scale : 3.0 * scale - 0.5,
                        1.375 * scale
                );
        }
    }

    @Nullable
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new FireBreath(dragon, 0.7F);
    }

    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isChild() ? DMSounds.DRAGON_PURR_HATCHLING : (
                dragon.getRNG().nextFloat() < 0.33F
                        ? DMSounds.DRAGON_PURR
                        : DMSounds.DRAGON_AMBIENT
        );
    }

    public SoundEvent getDeathSound(TameableDragonEntity dragon) {
        return dragon.isEgg() ? DMSounds.DRAGON_EGG_SHATTER : DMSounds.DRAGON_DEATH;
    }

    public SoundEvent getRoarSound(TameableDragonEntity dragon) {
        return dragon.isChild() ? DMSounds.DRAGON_ROAR_HATCHLING : DMSounds.DRAGON_ROAR;
    }

    public String getName() {
        return this.formatting + ClientUtil.translateToLocal(this.translationKey);
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return !this.immunities.isEmpty() && this.immunities.contains(source);
    }

    public ReferenceSet<DamageSource> getImmunities() {
        return ReferenceSets.unmodifiable(this.immunities);
    }

    public boolean isHabitat(Block block) {
        return !this.blocks.isEmpty() && this.blocks.contains(block);
    }

    public boolean isHabitat(@Nullable Biome biome) {
        return biome != null && !this.biomes.isEmpty() && this.biomes.contains(biome);
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T> T bindInstance(Class<T> clazz, T instance) {
        return clazz.cast(this.map.put(clazz, instance));
    }

    public <T> T getInstance(Class<T> clazz, @Nullable T fallback) {
        return clazz.cast(this.map.getOrDefault(clazz, fallback));
    }

    public <T> void ifPresent(Class<T> clazz, Consumer<? super T> consumer) {
        Object value = this.map.get(clazz);
        if (value != null) {
            consumer.accept(clazz.cast(value));
        }
    }

    public <T, V> V ifPresent(Class<T> clazz, Function<? super T, V> function, V fallback) {
        Object value = this.map.get(clazz);
        if (value != null) {
            return function.apply(clazz.cast(value));
        }
        return fallback;
    }

    public static class Registry extends DeferredRegistry<DragonType> implements IForgeRegistry.AddCallback<DragonType> {
        public Registry(ResourceLocation identifier, RegistryBuilder<DragonType> builder) {
            super(identifier, DragonType.class, builder);
        }

        @Override
        public void onAdd(IForgeRegistryInternal<DragonType> owner, RegistryManager stage, int id, DragonType obj, @Nullable DragonType oldObj) {
            ResourceLocation loot = obj.lootTable;
            if (loot != null && !LootTableList.getAll().contains(loot)) {
                LootTableList.register(loot);
            }
        }
    }
}
