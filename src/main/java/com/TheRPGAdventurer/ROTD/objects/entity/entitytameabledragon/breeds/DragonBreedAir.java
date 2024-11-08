package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathNode;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.effects.AetherBreathFX;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundEffectName;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound.SoundState;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponAether;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class DragonBreedAir extends DragonBreed {
	// The amount of the aether dragons flight speed bonus (Added to the dragon base air speed)
//	private static final float AETHER_SPEED_BONUS = 0.68465f; FIXME DISABLED FOR HEAD ROTATE ISSUE!

    public DragonBreedAir() {
        super("aether", 0x0294bd);

        setImmunity(DamageSource.MAGIC);
        setImmunity(DamageSource.HOT_FLOOR);
        setImmunity(DamageSource.LIGHTNING_BOLT);
        setImmunity(DamageSource.WITHER);

        setHabitatBlock(Blocks.LAPIS_BLOCK);
        setHabitatBlock(Blocks.LAPIS_ORE);
    }

    @Override
    public boolean isHabitatEnvironment(EntityTameableDragon dragon) {
        // true if located pretty high (> 2/3 of the maximum world height)
        return dragon.posY > dragon.world.getHeight() * 0.66;
    }

    @Override
    public void onEnable(EntityTameableDragon dragon) { //FIXME DISALBED FOR HEAD ROTATE ISSUE!
//    	dragon.getEntityAttribute(EntityTameableDragon.MOVEMENT_SPEED_AIR).setBaseValue(EntityTameableDragon.BASE_AIR_SPEED + AETHER_SPEED_BONUS);
    }

    @Override
    public void onDisable(EntityTameableDragon dragon) {
//    	dragon.getEntityAttribute(EntityTameableDragon.MOVEMENT_SPEED_AIR).setBaseValue(EntityTameableDragon.BASE_AIR_SPEED);
    }

    @Override
    public void onLivingUpdate(EntityTameableDragon dragon) {
        World level = dragon.world;
        if (dragon.posY > level.getHeight() * 1.2 && level.isDaytime() && dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
            Random random = this.rand;
            float s = dragon.getScale() * 1.2f;
            float h = dragon.height * s;
            float f = (dragon.width - 0.65F) * s;
            for (int i = 0; i < s; ++i) {
                double x = dragon.posX + (random.nextDouble() - 0.5) * f;
                double y = dragon.posY + (random.nextDouble() - 0.5) * h;
                double z = dragon.posZ + (random.nextDouble() - 0.5) * f;
                if (random.nextInt(5) == 0) {
                    level.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 1, 1, 0, 0, 0, 0); //yellow
                } else {
                    level.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 0, 1, 1, 0, 0, 0); //aqua
                }
            }
        }
    }

    @Override
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
        return new BreathWeaponAether(dragon);
    }

    @Override
    public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
        return EnumItemBreedTypes.AETHER;
    }

    @Override
    public void spawnClientNodeEntity(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new AetherBreathFX(world, position, direction, power, partialTicks));
    }

    @Override
    public SoundEffectName getBreathWeaponSoundEffect(DragonLifeStage stage, SoundState state) {
        return state.aether;
    }
}
	
