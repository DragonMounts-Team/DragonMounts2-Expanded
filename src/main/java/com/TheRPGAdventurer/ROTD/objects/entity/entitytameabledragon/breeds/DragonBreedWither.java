package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds;

import com.TheRPGAdventurer.ROTD.inits.ModSounds;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathNode;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.effects.WitherBreathFX;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponWither;
import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;


public class DragonBreedWither extends DragonBreed {

    DragonBreedWither() {
        super("wither", 0x50260a);
        
        setImmunity(DamageSource.MAGIC);
        setImmunity(DamageSource.HOT_FLOOR);
        setImmunity(DamageSource.LIGHTNING_BOLT);
        setImmunity(DamageSource.WITHER);
        
    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {}

    @Override
    public void onDisable(EntityTameableDragon dragon) {}

    @Override
    public void onDeath(EntityTameableDragon dragon) {}

    public SoundEvent getLivingSound(EntityTameableDragon dragon) {
        return dragon.isBaby() ? ModSounds.ENTITY_DRAGON_HATCHLING_GROWL : ModSounds.ENTITY_NETHER_DRAGON_GROWL;
    }
    
	@Override
	public boolean canChangeBreed() {
		return false;
	}

    @Override
    public void spawnClientNodeEntity(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new WitherBreathFX(world, position, direction, power, partialTicks));
    }

	@Override
	public void onLivingUpdate(EntityTameableDragon dragon) {
        World level = dragon.world;
        if (level instanceof WorldServer && !dragon.isDead && dragon.isUsingBreathWeapon() && !dragon.isEgg()) {
            ((WorldServer) level).spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    dragon.posX,
                    dragon.posY + dragon.getEyeHeight(),
                    dragon.posZ,
                    1,
                    0.5,
                    0.25,
                    0.5,
                    0.0
            );
        }
	}
	
//	@Override
//	public boolean isInfertile() {
//		return true;
//	}
	
	@Override
	public EnumParticleTypes getSneezeParticle() {
		return null;
	}

    @Override
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
        return new BreathWeaponWither(dragon);
    }


    @Override
    public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
        return EnumItemBreedTypes.WITHER;
    }
}
	
