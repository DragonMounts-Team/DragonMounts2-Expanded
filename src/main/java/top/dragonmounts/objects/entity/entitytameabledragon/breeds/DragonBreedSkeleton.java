package top.dragonmounts.objects.entity.entitytameabledragon.breeds;

import top.dragonmounts.inits.ModSounds;
import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.items.EnumItemBreedTypes;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;


public class DragonBreedSkeleton extends DragonBreed {

    DragonBreedSkeleton() {
        super("skeleton", 0xffffff);

        setHabitatBlock(Blocks.BONE_BLOCK);
        setImmunity(DamageSource.LIGHTNING_BOLT);
        setImmunity(DamageSource.WITHER);
    }

    @Override
    public boolean isHabitatEnvironment(EntityTameableDragon dragon) {
        if (dragon.posY * 4 > dragon.world.getHeight()) {
            // woah dude, too high!
            return false;
        }

        // too bright!
        return dragon.world.getLightBrightness(new BlockPos(dragon)) < 4;
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

    public SoundEvent getLivingSound(EntityTameableDragon dragon) {
        return dragon.isBaby() ? ModSounds.ENTITY_DRAGON_HATCHLING_GROWL : ModSounds.ENTITY_SKELETON_DRAGON_GROWL;
    }

//	@Override
//	public boolean canChangeBreed() {return false;}

    @Override
    public boolean canUseBreathWeapon() {
        return false;
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
    public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
        return EnumItemBreedTypes.SKELETON;
    }
}
	
