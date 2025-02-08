package net.dragonmounts.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.effects.FlameBreathFX;
import net.dragonmounts.entity.breath.impl.FireBreath;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.util.DMUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.registries.*;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;
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

    public DragonType(ResourceLocation identifier, Properties props) {
        this.color = props.color;
        this.convertible = props.convertible;
        this.isSkeleton = props.isSkeleton;
        this.avoidWater = props.avoidWater;
        this.formatting = props.formatting;
        this.attributes = ImmutableMultimap.copyOf(props.attributes);
        this.immunities = new ReferenceOpenHashSet<>(props.immunities);
        this.blocks = new ReferenceOpenHashSet<>(props.blocks);
        this.biomes = new ReferenceOpenHashSet<>(props.biomes);
        this.sneezeParticle = props.sneezeParticle;
        this.eggParticle = props.eggParticle;
        this.lootTable = props.hasLootTable ? (props.lootTable == null ? identifier : props.lootTable) : null;
        this.setRegistryName(this.identifier = identifier);
        this.translationKey = DMUtils.makeDescriptionId("dragon_type", identifier);
    }

    public void tick(TameableDragonEntity dragon) {}

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
                        0.6 * scale,
                        sitting ? 3.4 * scale : 4.4 * scale,
                        0.1 * scale
                );
            case 2:
                return new Vec3d(
                        -0.6 * scale,
                        sitting ? 3.4 * scale : 4.4 * scale,
                        0.1 * scale
                );
            case 3:
                return new Vec3d(
                        1.6 * scale,
                        sitting ? 2.1 * scale : 2.5 * scale,
                        0.2 * scale
                );
            case 4:
                return new Vec3d(
                        -1.6 * scale,
                        sitting ? 2.1 * scale : 2.5 * scale,
                        0.2 * scale
                );
            default:
                return new Vec3d(
                        0,
                        sitting ? 3.4 * scale : 4.4 * scale,
                        2.2 * scale
                );
        }
    }

    @Nullable
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new FireBreath(dragon, 0.7F);
    }

    public SoundEffectName getBreathSound(DragonLifeStage stage, SoundState state) {
        return state.getSoundByAge(stage);
    }

    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isBaby() ? DMSounds.ENTITY_DRAGON_HATCHLING_GROWL :
                (dragon.getRNG().nextInt(3) == 0
                        ? DMSounds.ENTITY_DRAGON_GROWL
                        : DMSounds.ENTITY_DRAGON_BREATHE
                );
    }

    public SoundEvent getRoarSound(TameableDragonEntity dragon) {
        return dragon.isBaby() ? DMSounds.HATCHLING_DRAGON_ROAR : DMSounds.DRAGON_ROAR;
    }

    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new FlameBreathFX(world, position, direction, power, partialTicks));
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

    public static class Properties {
        protected static final UUID MODIFIER_UUID = UUID.fromString("12e4cc82-db6d-5676-afc5-86498f0f6464");
        public final HashMultimap<String, AttributeModifier> attributes = HashMultimap.create();
        public final int color;
        public final TextFormatting formatting;
        public final Set<DamageSource> immunities = new ReferenceOpenHashSet<>();
        public final Set<Block> blocks = new ReferenceOpenHashSet<>();
        public final Set<Biome> biomes = new ReferenceOpenHashSet<>();
        public EnumParticleTypes sneezeParticle = EnumParticleTypes.SMOKE_LARGE;
        public EnumParticleTypes eggParticle = EnumParticleTypes.TOWN_AURA;
        public ResourceLocation lootTable;
        boolean hasLootTable = true;
        public boolean convertible = true;
        public boolean isSkeleton = false;
        public boolean avoidWater = false;

        public Properties(int color, TextFormatting formatting) {
            this.color = color;
            this.formatting = formatting;
            // ignore suffocation damage
            this.addImmunity(DamageSource.DROWN)
                    .addImmunity(DamageSource.IN_WALL)
                    .addImmunity(DamageSource.ON_FIRE)
                    .addImmunity(DamageSource.IN_FIRE)
                    .addImmunity(DamageSource.LAVA)
                    .addImmunity(DamageSource.HOT_FLOOR)
                    .addImmunity(DamageSource.CACTUS) // assume that cactus needles don't do much damage to animals with horned scales
                    .addImmunity(DamageSource.DRAGON_BREATH); // ignore damage from vanilla ender dragon. I kinda disabled this because it wouldn't make any sense, feel free to re enable
        }

        public Properties notConvertible() {
            this.convertible = false;
            return this;
        }

        public Properties isSkeleton() {
            this.isSkeleton = true;
            return this;
        }

        public Properties putAttributeModifier(IAttribute attribute, String name, double value, int operation) {
            this.attributes.put(attribute.getName(), new AttributeModifier(MODIFIER_UUID, name, value, operation));
            return this;
        }

        public Properties addImmunity(DamageSource source) {
            this.immunities.add(source);
            return this;
        }

        public Properties addHabitat(Block block) {
            this.blocks.add(block);
            return this;
        }

        public Properties addHabitat(Biome biome) {
            this.biomes.add(biome);
            return this;
        }

        public Properties setSneezeParticle(@Nullable EnumParticleTypes particle) {
            this.sneezeParticle = particle;
            return this;
        }

        public Properties setEggParticle(EnumParticleTypes particle) {
            this.eggParticle = particle;
            return this;
        }

        public Properties avoidWater() {
            this.avoidWater = true;
            return this;
        }

        public Properties removeLoot() {
            this.hasLootTable = false;
            return this;
        }

        public Properties loot(ResourceLocation table) {
            this.lootTable = table;
            this.hasLootTable = true;
            return this;
        }
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
