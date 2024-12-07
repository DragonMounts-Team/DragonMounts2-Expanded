package net.dragonmounts.init;

import net.dragonmounts.DragonMountsTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(DragonMountsTags.MOD_ID)
public class DMSounds {
	@ObjectHolder("mob.dragon.step")
	public static final SoundEvent ENTITY_DRAGON_STEP = createSoundEvent("mob.dragon.step");

	@ObjectHolder("mob.dragon.breathe")
	public static final SoundEvent ENTITY_DRAGON_BREATHE = createSoundEvent("mob.dragon.breathe");

	@ObjectHolder("mob.dragon.death")
	public static final SoundEvent ENTITY_DRAGON_DEATH = createSoundEvent("mob.dragon.death");
	
	@ObjectHolder("mob.dragon.growl")
	public static final SoundEvent ENTITY_DRAGON_GROWL = createSoundEvent("mob.dragon.growl");

	@ObjectHolder("mob.dragon.hatchlinggrowl")
	public static final SoundEvent ENTITY_DRAGON_HATCHLING_GROWL = createSoundEvent("mob.dragon.hatchlinggrowl");
	
	@ObjectHolder("mob.dragon.nethergrowl")
	public static final SoundEvent ENTITY_NETHER_DRAGON_GROWL = createSoundEvent("mob.dragon.nethergrowl");

	@ObjectHolder("mob.dragon.hatchlingnethergrowl")
	public static final SoundEvent ENTITY_HATCHLING_NETHER_DRAGON_GROWL = createSoundEvent("mob.dragon.hatchlingnethergrowl");

	@ObjectHolder("mob.dragon.skeletongrowl")
	public static final SoundEvent ENTITY_SKELETON_DRAGON_GROWL = createSoundEvent("mob.dragon.skeletongrowl");

	@ObjectHolder("mob.dragon.hatchlingskeletongrowl")
	public static final SoundEvent ENTITY_HATCHLING_SKELETON_DRAGON_GROWL = createSoundEvent("mob.dragon.hatchlingskeletongrowl");
	
	@ObjectHolder("mob.dragon.zombiedeath")
	public static final SoundEvent ZOMBIE_DRAGON_DEATH = createSoundEvent("mob.dragon.zombiedeath");
	
	@ObjectHolder("mob.dragon.zombiegrowl")
	public static final SoundEvent ZOMBIE_DRAGON_GROWL = createSoundEvent("mob.dragon.zombiegrowl");
	
	@ObjectHolder("mob.dragon.sneeze")
	public static final SoundEvent DRAGON_SNEEZE = createSoundEvent("mob.dragon.sneeze");
	
	@ObjectHolder("mob.dragon.hatched")
	public static final SoundEvent DRAGON_HATCHED = createSoundEvent("mob.dragon.hatched");
	
	@ObjectHolder("mob.dragon.hatching")
	public static final SoundEvent DRAGON_HATCHING = createSoundEvent("mob.dragon.hatching");
	
	@ObjectHolder("item.whistle")
	public static final SoundEvent DRAGON_WHISTLE = createSoundEvent("item.whistle");
	
	@ObjectHolder("item.whistle1")
	public static final SoundEvent DRAGON_WHISTLE1 = createSoundEvent("item.whistle1");
	
	@ObjectHolder("mob.dragon.roar")
	public static final SoundEvent DRAGON_ROAR = createSoundEvent("mob.dragon.roar");

	@ObjectHolder("mob.dragon.hatchlingroar")
	public static final SoundEvent HATCHLING_DRAGON_ROAR = createSoundEvent("mob.dragon.hatchlingroar");

	@ObjectHolder("item.gender_switch")
	public static final SoundEvent DRAGON_SWITCH = createSoundEvent("item.gender_switch");

	private static SoundEvent createSoundEvent(final String name) {
		final ResourceLocation id = new ResourceLocation(DragonMountsTags.MOD_ID, name);
		return new SoundEvent(id).setRegistryName(id);
	}
}
