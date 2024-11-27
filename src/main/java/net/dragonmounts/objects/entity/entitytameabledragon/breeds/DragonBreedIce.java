/*
 ** 2013 October 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.objects.entity.entitytameabledragon.breeds;

import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.BreathNode;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.effects.IceBreathFX;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.sound.SoundEffectName;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.sound.SoundState;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponIce;
import net.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import net.dragonmounts.objects.items.EnumItemBreedTypes;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonBreedIce extends DragonBreed {
    public static final float FOOTPRINT_CHANCE = 0.01F;
    public DragonBreedIce() {
        super("ice", 0x00f2ff);

        setImmunity(DamageSource.MAGIC);
        setImmunity(DamageSource.HOT_FLOOR);
        setImmunity(DamageSource.LIGHTNING_BOLT);
        setImmunity(DamageSource.WITHER);

        setHabitatBlock(Blocks.SNOW);
        setHabitatBlock(Blocks.SNOW_LAYER);
        setHabitatBlock(Blocks.ICE);
        setHabitatBlock(Blocks.PACKED_ICE);
        setHabitatBlock(Blocks.FROSTED_ICE);

        setHabitatBiome(Biomes.FROZEN_OCEAN);
        setHabitatBiome(Biomes.FROZEN_RIVER);
        setHabitatBiome(Biomes.ICE_MOUNTAINS);
        setHabitatBiome(Biomes.ICE_PLAINS);
    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {}

    @Override
    public void onDisable(EntityTameableDragon dragon) {}

    @Override
    public void onDeath(EntityTameableDragon dragon) {}

    @Override
    public void spawnClientNodeEntity(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new IceBreathFX(world, position, direction, power, partialTicks));
    }

    @Override
    public void onLivingUpdate(EntityTameableDragon dragon) {
        World level = dragon.world;
        if (level.isRemote) return;
        if (dragon.isOverWater()) {
            EnchantmentFrostWalker.freezeNearby(dragon, level, new BlockPos(dragon), 1);
        }
        if (!dragon.isDead && dragon.posY > level.getHeight() * 1.25 && dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE) && BiomeDictionary.hasType(level.getBiome(dragon.getPosition()), BiomeDictionary.Type.SNOWY)) {
            Random random = this.rand;
            float s = dragon.getScale() * 1.2f;
            float f = (dragon.width - 0.65F) * s;
            level.spawnParticle(
                    EnumParticleTypes.FIREWORKS_SPARK,
                    dragon.posX + (random.nextDouble() - 0.5) * f,
                    dragon.posY + (random.nextDouble() - 0.5) * dragon.height * s,
                    dragon.posZ + (random.nextDouble() - 0.5) * f,
                    0,
                    0,
                    0
            );
        }
        // only apply on server adult dragons that isn't flying
        if (!dragon.isAdult() || dragon.isFlying()) return;

        // footprint loop, from EntitySnowman.onLivingUpdate with slight tweaks
        for (int i = 0; i < 4; ++i) {
            // place only if randomly selected
            if (this.rand.nextFloat() > FOOTPRINT_CHANCE) continue;

            // get footprint position
            double bx = dragon.posX + (i & 1) * 0.5 - 0.25;
            double by = dragon.posY + 0.5;
            double bz = dragon.posZ + (i / 2 % 2 * 2 - 1) * 0.25;
            BlockPos pos = new BlockPos(bx, by, bz);

            // footprints can only be placed on empty space
            if (level.isAirBlock(pos) && level.getBiomeForCoordsBody(pos).getTemperature(pos) < 0.1F && Blocks.SNOW_LAYER.canPlaceBlockAt(level, pos)) {
                level.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
            }
        }
    }

    @Override
    public EnumParticleTypes getSneezeParticle() {
        return null;
    }

    @Override
    public SoundEffectName getBreathWeaponSoundEffect(DragonLifeStage stage, SoundState state) {
        return state.ice;
    }

    @Override
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
        return new BreathWeaponIce(dragon);
    }

    @Override
    public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
        return EnumItemBreedTypes.ICE;
    }
}
