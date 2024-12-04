/*
 ** 2013 March 23
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.objects.entity.entitytameabledragon.helper;

import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.DragonBreed;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.dragonmounts.util.math.MathX.parseColor;

/**
 * Helper class for breed properties.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonBreedHelper extends DragonHelper {
    private static final Logger L = LogManager.getLogger();
    private static final int BLOCK_RANGE = 2;
    private static final int POINTS_BLOCK = 1;
    private static final int POINTS_BIOME = 1;
    private static final int POINTS_INITIAL = 1000;
    private static final int POINTS_INHERIT = 1800;
    private static final int POINTS_ENV = 3;
    private static final int TICK_RATE_PARTICLES = 2;
    private static final int TICK_RATE_BLOCK = 20;
    private static final String NBT_BREED = "Breed";
    private static final String NBT_BREED_POINTS = "breedPoints";

    private final DataParameter<EnumDragonBreed> DATA_BREED;
    private final Map<EnumDragonBreed, AtomicInteger> breedPoints = new EnumMap<>(EnumDragonBreed.class);

    public DragonBreedHelper(EntityTameableDragon dragon,
                             DataParameter<EnumDragonBreed> DATA_BREED) {
        super(dragon);

        this.DATA_BREED = DATA_BREED;

        if (!dragon.world.isRemote) {
            // initialize map to avoid future checkings
            for (EnumDragonBreed type : EnumDragonBreed.values()) {
                breedPoints.put(type, new AtomicInteger());
            }
        }

        dataWatcher.register(DATA_BREED, EnumDragonBreed.END);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString(NBT_BREED, getBreedType().getName());

        NBTTagCompound breedPointTag = new NBTTagCompound();
        breedPoints.forEach((type, points) -> breedPointTag.setInteger(type.getName(), points.get()));
        nbt.setTag(NBT_BREED_POINTS, breedPointTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        // read breed name and convert it to the corresponding breed object
        String breedName = nbt.getString(NBT_BREED);
        EnumDragonBreed breed = EnumDragonBreed.NAME_MAPPING.apply(breedName);
        if (breed == null) {
            breed = EnumDragonBreed.FIRE;
            L.warn("Dragon {} loaded with invalid breed type {}, using {} instead",
                    dragon.getEntityId(), breedName, breed);
        }
        this.setBreedType(breed);

        // read breed points
        NBTTagCompound breedPointTag = nbt.getCompoundTag(NBT_BREED_POINTS);
        breedPoints.forEach((type, points) -> points.set(breedPointTag.getInteger(type.getName())));
    }

    public Map<EnumDragonBreed, AtomicInteger> getBreedPoints() {
        return Collections.unmodifiableMap(breedPoints);
    }

    public EnumDragonBreed getBreedType() {
        return dataWatcher.get(DATA_BREED);
    }

    public void setBreedType(EnumDragonBreed newType) {
        EntityTameableDragon dragon = this.dragon;
        // ignore breed changes on client side, it's controlled by the server
        if (dragon.world.isRemote || newType == null) return;
        L.trace("setBreed({})", newType);

        // check if the breed actually changed
        EnumDragonBreed oldType = getBreedType();
        if (oldType == newType && !dragon.isFirstUpdate()) return;

        DragonBreed oldBreed = oldType.getBreed();
        DragonBreed newBreed = newType.getBreed();
        dragon.getBreathHelper().onBreedChange(newBreed);

        // switch breed stats
        oldBreed.onDisable(dragon);
        newBreed.onEnable(dragon);

        // check for fire immunity and disable fire particles
        dragon.setImmuneToFire(newBreed.isImmuneToDamage(DamageSource.IN_FIRE) || newBreed.isImmuneToDamage(DamageSource.ON_FIRE) || newBreed.isImmuneToDamage(DamageSource.LAVA));

        // update breed name
        dataWatcher.set(DATA_BREED, newType);

        // reset breed points
        if (dragon.isEgg()) {
            this.resetPoints(newType);
        }
    }


    @Override
    public void onLivingUpdate() {
        EntityTameableDragon dragon = this.dragon;
        EnumDragonBreed currentType = getBreedType();
        if (dragon.isEgg()) {
            World level = dragon.world;
            // spawn breed-specific particles every other tick
            if (level.isRemote) {
                if (currentType != EnumDragonBreed.END && dragon.ticksExisted % TICK_RATE_PARTICLES == 0) {
                    double px = dragon.posX + (rand.nextDouble() - 0.5);
                    double py = dragon.posY + (rand.nextDouble() - 0.5);
                    double pz = dragon.posZ + (rand.nextDouble() - 0.5);
                    int color = currentType.getBreed().getColor();
                    level.spawnParticle(EnumParticleTypes.REDSTONE, px, py + 1, pz,
                            parseColor(color, 2), parseColor(color, 1), parseColor(color, 0));
                }
                return;
            }

            // update egg breed every second on the server
            if (DragonMountsConfig.shouldChangeBreedViaHabitatOrBlock && currentType.getBreed().canChangeBreed() && dragon.ticksExisted % TICK_RATE_BLOCK == 0) {
                BlockPos eggPos = dragon.getPosition();

                // scan surrounding for breed-loving blocks
                BlockPos eggPosFrom = eggPos.add(BLOCK_RANGE, BLOCK_RANGE, BLOCK_RANGE);
                BlockPos eggPosTo = eggPos.add(-BLOCK_RANGE, -BLOCK_RANGE, -BLOCK_RANGE);

                BlockPos.getAllInBoxMutable(eggPosFrom, eggPosTo).forEach(blockPos -> {
                    Block block = level.getBlockState(blockPos).getBlock();
                    breedPoints.entrySet().stream()
                            .filter(breed -> (breed.getKey().getBreed().isHabitatBlock(block)))
                            .forEach(breed -> breed.getValue().addAndGet(POINTS_BLOCK));
                });

                // check biome
                Biome biome = level.getBiome(eggPos);

                breedPoints.keySet().forEach(breed -> {
                    // check for biomes
                    if (breed.getBreed().isHabitatBiome(biome)) {
                        breedPoints.get(breed).addAndGet(POINTS_BIOME);
                    }

                    // extra points for good environments
                    if (breed.getBreed().isHabitatEnvironment(dragon)) {
                        breedPoints.get(breed).addAndGet(POINTS_ENV);
                    }
                });

                // update most dominant breed
                breedPoints.entrySet().stream()
                        .max(Comparator.comparingInt(breed -> breed.getValue().get()))
                        .map(Map.Entry::getKey)
                        .ifPresent(this::setBreedType);
            }
        }
    }

    @Override
    public void applyEntityAttributes() {
        getBreedHealth();
    }

    @Override
    public void onDeath() {
        getBreedType().getBreed().onDeath(dragon);
    }

    public void inheritBreed(EntityTameableDragon parent1, EntityTameableDragon parent2) {
        breedPoints.get(parent1.getBreedType()).addAndGet(POINTS_INHERIT + rand.nextInt(POINTS_INHERIT));
        breedPoints.get(parent2.getBreedType()).addAndGet(POINTS_INHERIT + rand.nextInt(POINTS_INHERIT));
    }

    /**
     * Get's the health of the dragon per breed, doubles
     * when it turns into an adult
     */
    public void getBreedHealth() {

        IAttributeInstance health = dragon.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        double base = DragonMountsConfig.BASE_HEALTH; //90d

        switch (getBreedType()) {
            case NETHER:
                health.setBaseValue(base + 5d);
                break;
            case END:
                health.setBaseValue(base + 10d);
                break;
            case SKELETON:
                health.setBaseValue(base - (base < 16d ? 0d : 15d)); // Cant have 0 health!
                break;
            case WITHER:
                health.setBaseValue(base - (base < 6d ? 0d : 10d)); // Cant have 0 health!
                break;
            default: //All Dragons without special health parameters
                health.setBaseValue(base);
                break;
        }
    }

    public void resetPoints(@Nullable EnumDragonBreed breed) {
        this.breedPoints.values().forEach(points -> points.set(0));
        this.breedPoints.get(breed == null ? this.getBreedType() : breed).set(POINTS_INITIAL);
    }
}
