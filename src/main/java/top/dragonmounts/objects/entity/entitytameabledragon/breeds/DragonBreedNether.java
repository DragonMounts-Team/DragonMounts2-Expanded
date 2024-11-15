package top.dragonmounts.objects.entity.entitytameabledragon.breeds;


import top.dragonmounts.inits.ModSounds;
import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.BreathNode;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.effects.NetherBreathFX;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponNether;
import top.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import top.dragonmounts.objects.items.EnumItemBreedTypes;
import net.minecraft.init.Biomes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class DragonBreedNether extends DragonBreed {

    DragonBreedNether() {
        super("nether", 0xe5b81b);
        setHabitatBiome(Biomes.HELL);

        setImmunity(DamageSource.MAGIC);
        setImmunity(DamageSource.HOT_FLOOR);
        setImmunity(DamageSource.LIGHTNING_BOLT);
        setImmunity(DamageSource.WITHER);

    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {
        dragon.getBrain().setAvoidsWater(true);
    }

    @Override
    public void onDisable(EntityTameableDragon dragon) {
        dragon.getBrain().setAvoidsWater(false);
    }

    @Override
    public void onDeath(EntityTameableDragon dragon) {

    }

    public SoundEvent getLivingSound(EntityTameableDragon dragon) {
        return dragon.isBaby() ? ModSounds.ENTITY_DRAGON_HATCHLING_GROWL : ModSounds.ENTITY_NETHER_DRAGON_GROWL;
    }

    @Override
    public void spawnClientNodeEntity(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new NetherBreathFX(world, position, direction, power, partialTicks));
    }

    @Override
    public void onLivingUpdate(EntityTameableDragon dragon) {
        World level = dragon.world;
        if (level.isRemote || dragon.isDead || !dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE))
            return;
        Random random = this.rand;
        float s = dragon.getScale();
        float h = dragon.height * s;
        float f = (dragon.width - 0.65F) * s;
        boolean isWet = dragon.isWet();
        for (int i = -1; i < s; ++i) {
            level.spawnParticle(
                    EnumParticleTypes.DRIP_LAVA,
                    dragon.posX + (random.nextDouble() - 0.5) * f,
                    dragon.posY + (random.nextDouble() - 0.5) * h,
                    dragon.posZ + (random.nextDouble() - 0.5) * f,
                    0,
                    0,
                    0
            );
            if (isWet) {
                level.spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        dragon.posX + (random.nextDouble() - 0.5) * f,
                        dragon.posY + (random.nextDouble() - 0.5) * h,
                        dragon.posZ + (random.nextDouble() - 0.5) * f,
                        0,
                        0,
                        0
                );
            }
        }
    }

    @Override
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
        return new BreathWeaponNether(dragon);
    }

    @Override
    public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
        return dragon.isMale() ? EnumItemBreedTypes.NETHER : EnumItemBreedTypes.NETHER2;
    }
}
