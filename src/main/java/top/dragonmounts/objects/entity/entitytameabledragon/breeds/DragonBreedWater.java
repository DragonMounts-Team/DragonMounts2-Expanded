/*
 ** 2013 October 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package top.dragonmounts.objects.entity.entitytameabledragon.breeds;

import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.BreathNode;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.effects.HydroBreathFX;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.sound.SoundEffectName;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.sound.SoundState;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponHydro;
import top.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import top.dragonmounts.objects.items.EnumItemBreedTypes;
import top.dragonmounts.util.EntityUtil;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonBreedWater extends DragonBreed {

    public DragonBreedWater(String skin, int color) {
        super(skin, color);

        setImmunity(DamageSource.DROWN);
        setImmunity(DamageSource.MAGIC);
        setImmunity(DamageSource.HOT_FLOOR);
        setImmunity(DamageSource.LIGHTNING_BOLT);
        setImmunity(DamageSource.WITHER);

        setHabitatBlock(Blocks.WATER);
        setHabitatBlock(Blocks.FLOWING_WATER);

        setHabitatBiome(Biomes.OCEAN);
        setHabitatBiome(Biomes.RIVER);
    }

    public DragonBreedWater() {
        this("sylphid", 0x4f69a8);
    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {
    }

    @Override
    public void onDisable(EntityTameableDragon dragon) {
    }

    @Override
    public void onDeath(EntityTameableDragon dragon) {
    }

    @Override
    public void spawnClientNodeEntity(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new HydroBreathFX(world, position, direction, power, partialTicks));
    }

	public void onLivingUpdate(EntityTameableDragon dragon) {
        if (dragon.isInWater()) {
            EntityUtil.addOrResetEffect(dragon, MobEffects.WATER_BREATHING, 200, 0, false, false, 21);
		}
        if (dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
            World level = dragon.world;
            Random random = this.rand;
            float s = dragon.getScale() * 1.2f;
            float h = dragon.height * s;
            float f = (dragon.width - 0.65F) * s;
            for (int i = -2; i < s; ++i) {
                level.spawnParticle(
                        EnumParticleTypes.DRIP_WATER,
                        dragon.posX + (random.nextDouble() - 0.5) * f,
                        dragon.posY - 1 + (random.nextDouble() - 0.5) * h,
                        dragon.posZ + (random.nextDouble() - 0.5) * f,
                        0,
                        0,
                        0
                );
            }
        }
    }

    @Override
    public EnumParticleTypes getSneezeParticle() {
        return null;
    }

    @Override
    public SoundEffectName getBreathWeaponSoundEffect(DragonLifeStage stage, SoundState state) {
        return state.water;
    }

    @Override
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
        return new BreathWeaponHydro(dragon);
    }


    @Override
    public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
        return EnumItemBreedTypes.WATER;
    }
}