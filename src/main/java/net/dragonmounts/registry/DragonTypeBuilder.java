package net.dragonmounts.registry;

import com.google.common.collect.HashMultimap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

public class DragonTypeBuilder {
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

    public DragonTypeBuilder(int color, TextFormatting formatting) {
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

    public DragonTypeBuilder notConvertible() {
        this.convertible = false;
        return this;
    }

    public DragonTypeBuilder isSkeleton() {
        this.isSkeleton = true;
        return this;
    }

    public DragonTypeBuilder putAttributeModifier(IAttribute attribute, String name, double value, int operation) {
        this.attributes.put(attribute.getName(), new AttributeModifier(MODIFIER_UUID, name, value, operation));
        return this;
    }

    public DragonTypeBuilder addImmunity(DamageSource source) {
        this.immunities.add(source);
        return this;
    }

    public DragonTypeBuilder addHabitat(Block block) {
        this.blocks.add(block);
        return this;
    }

    public DragonTypeBuilder addHabitat(Biome biome) {
        this.biomes.add(biome);
        return this;
    }

    public DragonTypeBuilder setSneezeParticle(@Nullable EnumParticleTypes particle) {
        this.sneezeParticle = particle;
        return this;
    }

    public DragonTypeBuilder setEggParticle(EnumParticleTypes particle) {
        this.eggParticle = particle;
        return this;
    }

    public DragonTypeBuilder avoidWater() {
        this.avoidWater = true;
        return this;
    }

    public DragonTypeBuilder removeLoot() {
        this.hasLootTable = false;
        return this;
    }

    public DragonTypeBuilder loot(ResourceLocation table) {
        this.lootTable = table;
        this.hasLootTable = true;
        return this;
    }

    public <T extends DragonType> T build(
            BiFunction<ResourceLocation, DragonTypeBuilder, T> factory,
            ResourceLocation identifier
    ) {
        return factory.apply(identifier, this);
    }
}
