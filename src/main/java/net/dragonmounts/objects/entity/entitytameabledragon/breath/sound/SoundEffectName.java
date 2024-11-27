package net.dragonmounts.objects.entity.entitytameabledragon.breath.sound;

import net.dragonmounts.DragonMountsTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * User: The Grey Ghost
 * Date: 17/04/2014
 * Contains (some of the) sound effect names used for the dragon
 */
public enum SoundEffectName {
    SILENCE("silence", 0),
    ADULT_BREATHE_FIRE_START("mob.dragon.breathweapon.fire.adultbreathefirestart", 40),
    ADULT_BREATHE_FIRE_LOOP("mob.dragon.breathweapon.fire.adultbreathefireloop", 107),
    ADULT_BREATHE_FIRE_STOP("mob.dragon.breathweapon.fire.adultbreathefirestop", 20),
    JUVENILE_BREATHE_FIRE_START("mob.dragon.breathweapon.fire.juvenilebreathefirestart", 40),
    JUVENILE_BREATHE_FIRE_LOOP("mob.dragon.breathweapon.fire.juvenilebreathefireloop", 107),
    JUVENILE_BREATHE_FIRE_STOP("mob.dragon.breathweapon.fire.juvenilebreathefirestop", 20),
    HATCHLING_BREATHE_FIRE_START("mob.dragon.breathweapon.fire.hatchlingbreathefirestart", 40),
    HATCHLING_BREATHE_FIRE_LOOP("mob.dragon.breathweapon.fire.hatchlingbreathefireloop", 107),
    HATCHLING_BREATHE_FIRE_STOP("mob.dragon.breathweapon.fire.hatchlingbreathefirestop", 20),
    BREATHE_ICE_START("mob.dragon.breathweapon.ice.adultbreatheicestart", 34),
    BREATHE_ICE_LOOP("mob.dragon.breathweapon.ice.adultbreatheiceloop", 153),
    BREATHE_ICE_STOP("mob.dragon.breathweapon.ice.adultbreatheicestop", 24),
    BREATHE_FOREST_START("mob.dragon.breathweapon.forest.breatheforeststart", 19),
    BREATHE_FOREST_LOOP("mob.dragon.breathweapon.forest.breatheforestloop", 39),
    BREATHE_FOREST_STOP("mob.dragon.breathweapon.forest.breatheforeststop", 14),
    BREATHE_AIR_START("mob.dragon.breathweapon.air.breatheairstart", 30),
    BREATHE_AIR_LOOP("mob.dragon.breathweapon.air.breatheairloop", 148),
    BREATHE_AIR_STOP("mob.dragon.breathweapon.air.breatheairstop", 26),
    BREATHE_WATER_START("mob.dragon.breathweapon.water.breathewaterstart", 15),
    BREATHE_WATER_LOOP("mob.dragon.breathweapon.water.breathewaterloop", 99),
    BREATHE_WATER_STOP("mob.dragon.breathweapon.water.breathewaterstop", 71);

    public final ResourceLocation location;
    public final int duration;
    public final SoundEvent sound;

    /**
     * Information about the sound effect
     *
     * @param path     the location of the sound
     * @param duration the duration in ticks of the sound effect (0 = unused) - in practice, this is the duration
     *                 before the cross-fade to the next sound starts.  For looping sounds no effect
     */
    SoundEffectName(String path, int duration) {
        ResourceLocation location = new ResourceLocation(DragonMountsTags.MOD_ID, path);
        this.location = location;
        this.duration = duration;
        this.sound = new SoundEvent(location).setRegistryName(location);
    }

    public final SoundEvent getSoundEvent() {
        return this.sound;
    }

    public final int getDurationInTicks() {
        return this.duration;
    }
}
