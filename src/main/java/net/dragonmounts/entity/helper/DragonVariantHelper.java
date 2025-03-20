package net.dragonmounts.entity.helper;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMaps;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.Random;

import static net.dragonmounts.util.math.MathX.parseColor;

public class DragonVariantHelper implements ITickable {
    public static final String NBT_VARIANT_POINTS = "VariantPoints";
    private static final int BLOCK_RANGE = 2;
    private static final int POINTS_BLOCK = 1;
    private static final int POINTS_BIOME = 1;
    private static final int POINTS_INITIAL = 1000;
    private static final int POINTS_INHERIT = 1800;
    private static final int POINTS_ENV = 3;
    private static final int TICK_RATE_PARTICLES = 2;
    private static final int TICK_RATE_BLOCK = 20;
    private final Reference2IntOpenHashMap<DragonType> points = new Reference2IntOpenHashMap<>();
    public final TameableDragonEntity dragon;
    private DragonType lastType;

    public DragonVariantHelper(TameableDragonEntity dragon) {
        this.dragon = dragon;
    }

    public Reference2IntMap<DragonType> getBreedPoints() {
        return Reference2IntMaps.unmodifiable(this.points);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound points = new NBTTagCompound();
        for (Reference2IntMap.Entry<DragonType> entry : this.points.reference2IntEntrySet()) {
            points.setInteger(entry.getKey().identifier.toString(), entry.getIntValue());
        }
        nbt.setTag(NBT_VARIANT_POINTS, points);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagCompound points = nbt.getCompoundTag(NBT_VARIANT_POINTS);
        for (Reference2IntMap.Entry<DragonType> entry : this.points.reference2IntEntrySet()) {
            entry.setValue(points.getInteger(entry.getKey().identifier.toString()));
        }
    }

    @Override
    public void update() {
        TameableDragonEntity dragon = this.dragon;
        if (!dragon.isEgg()) return;
        DragonType current = dragon.getVariant().type;
        World level = dragon.world;
        // spawn breed-specific particles every other tick
        if (level.isRemote) {
            Random random = dragon.getRNG();
            if (current != DragonTypes.ENDER && dragon.ticksExisted % TICK_RATE_PARTICLES == 0) {
                double px = dragon.posX + (random.nextDouble() - 0.5);
                double py = dragon.posY + (random.nextDouble() - 0.5);
                double pz = dragon.posZ + (random.nextDouble() - 0.5);
                int color = current.color;
                level.spawnParticle(EnumParticleTypes.REDSTONE, px, py + 1, pz,
                        parseColor(color, 2), parseColor(color, 1), parseColor(color, 0));
            }
            return;
        }

        // update egg breed every second on the server
        if (DMConfig.ADAPTIVE_CONVERSION.value && current.convertible && dragon.ticksExisted % TICK_RATE_BLOCK == 0) {
            BlockPos eggPos = dragon.getPosition();
            // scan surrounding for breed-loving blocks
            for (BlockPos pos : BlockPos.getAllInBoxMutable(
                    eggPos.add(BLOCK_RANGE, BLOCK_RANGE, BLOCK_RANGE),
                    eggPos.add(-BLOCK_RANGE, -BLOCK_RANGE, -BLOCK_RANGE)
            )) {
                Block block = level.getBlockState(pos).getBlock();
                for (DragonType type : DragonType.REGISTRY) {
                    if (type.isHabitat(block)) {
                        this.points.addTo(type, POINTS_BLOCK);
                    }
                }
            }

            // check biome
            Biome biome = level.getBiome(eggPos);
            for (DragonType type : DragonType.REGISTRY) {
                if (type.isHabitat(biome)) {
                    this.points.addTo(type, POINTS_BIOME);
                }
                if (type.isHabitatEnvironment(dragon)) {
                    this.points.addTo(type, POINTS_ENV);
                }
            }
            this.applyPoints(current);
        }
    }

    public void applyPoints(DragonType current) {
        DragonType neo = current;
        int point = this.points.getInt(current);
        for (Reference2IntMap.Entry<DragonType> entry : this.points.reference2IntEntrySet()) {
            if (entry.getIntValue() > point) {
                point = entry.getIntValue();
                neo = entry.getKey();
            }
        }
        if (neo != current) {
            dragon.setVariant(neo.variants.draw(dragon.getRNG(), null));
        }
    }

    public void inheritBreed(TameableDragonEntity parent1, TameableDragonEntity parent2) {
        Random random = this.dragon.getRNG();
        this.points.addTo(parent1.getVariant().type, POINTS_INHERIT + random.nextInt(POINTS_INHERIT));
        this.points.addTo(parent2.getVariant().type, POINTS_INHERIT + random.nextInt(POINTS_INHERIT));
        this.applyPoints(this.dragon.getVariant().type);
    }

    public void onVariantChanged(DragonVariant variant) {
        // check if the breed actually changed
        DragonType type = variant.type;
        if (this.lastType == type) return;
        TameableDragonEntity dragon = this.dragon;
        dragon.breathHelper.onBreedChange(type);
        // check for fire immunity and disable fire particles
        ReferenceSet<DamageSource> immunities = type.getImmunities();
        dragon.setImmuneToFire(immunities.contains(DamageSource.LAVA) || immunities.contains(DamageSource.IN_FIRE) || immunities.contains(DamageSource.ON_FIRE));
        if (dragon.world.isRemote) {
            this.lastType = type;
            return;
        }
        if (this.lastType == null || this.lastType.avoidWater != type.avoidWater) {
            PathNavigate navigator = dragon.getNavigator();
            if (navigator instanceof PathNavigateGround) {
                ((PathNavigateGround) navigator).setCanSwim(!type.avoidWater); // originally setAvoidsWater()
            }
        }

        // reset breed points
        if (dragon.isEgg()) {
            this.resetPoints(type);
        }

        AbstractAttributeMap attributes = this.dragon.getAttributeMap();
        if (this.lastType != null) {
            attributes.removeAttributeModifiers(this.lastType.attributes);
        }
        attributes.applyAttributeModifiers(type.attributes);
        this.lastType = type;
    }

    public void resetPoints(@Nullable DragonType type) {
        this.points.clear();
        this.points.put(type == null ? this.dragon.getVariant().type : type, POINTS_INITIAL);
    }
}
