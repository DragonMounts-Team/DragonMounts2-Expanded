package net.dragonmounts.init;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.dragonmounts.DragonMountsTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class DMSounds {
	public static final ObjectList<SoundEvent> INSTANCES;
	public static final SoundEvent DRAGON_AMBIENT;
	public static final SoundEvent DRAGON_AMBIENT_WATER;
	public static final SoundEvent DRAGON_CHEST;
	public static final SoundEvent DRAGON_DEATH;
	public static final SoundEvent DRAGON_DEATH_ZOMBIE;
	public static final SoundEvent DRAGON_ROAR;
	public static final SoundEvent DRAGON_ROAR_HATCHLING;
	public static final SoundEvent DRAGON_ROAR_WATER;
	public static final SoundEvent DRAGON_PURR;
	public static final SoundEvent DRAGON_PURR_HATCHLING;
	public static final SoundEvent DRAGON_PURR_NETHER;
	public static final SoundEvent DRAGON_PURR_NETHER_HATCHLING;
	public static final SoundEvent DRAGON_PURR_SKELETON;
	public static final SoundEvent DRAGON_PURR_SKELETON_HATCHLING;
	public static final SoundEvent DRAGON_PURR_ZOMBIE;
	public static final SoundEvent DRAGON_SNEEZE;
	public static final SoundEvent DRAGON_STEP;
	public static final SoundEvent DRAGON_BREATH_START_ADULT;
	public static final SoundEvent DRAGON_BREATH_START_JUVENILE;
	public static final SoundEvent DRAGON_BREATH_START_HATCHLING;
	public static final SoundEvent DRAGON_BREATH_START_WATER;
	public static final SoundEvent DRAGON_BREATH_START_AIRFLOW;
	public static final SoundEvent DRAGON_BREATH_START_ICE;
	public static final SoundEvent DRAGON_BREATH_LOOP_ADULT;
	public static final SoundEvent DRAGON_BREATH_LOOP_JUVENILE;
	public static final SoundEvent DRAGON_BREATH_LOOP_HATCHLING;
	public static final SoundEvent DRAGON_BREATH_LOOP_ICE;
	public static final SoundEvent DRAGON_BREATH_LOOP_AIRFLOW;
	public static final SoundEvent DRAGON_BREATH_LOOP_WATER;
	public static final SoundEvent DRAGON_BREATH_STOP_ADULT;
	public static final SoundEvent DRAGON_BREATH_STOP_JUVENILE;
	public static final SoundEvent DRAGON_BREATH_STOP_HATCHLING;
	public static final SoundEvent DRAGON_BREATH_STOP_ICE;
	public static final SoundEvent DRAGON_BREATH_STOP_AIRFLOW;
	public static final SoundEvent DRAGON_BREATH_STOP_WATER;
	public static final SoundEvent DRAGON_EGG_CRACK;
	public static final SoundEvent DRAGON_EGG_SHATTER;
	public static final SoundEvent FLUTE_BLOW_SHORT;
	public static final SoundEvent FLUTE_BLOW_LONG;
	public static final SoundEvent VARIATION_ORB_ACTIVATE;

	static SoundEvent create(String name) {
		ResourceLocation id = new ResourceLocation(DragonMountsTags.MOD_ID, name);
		return new SoundEvent(id).setRegistryName(id);
	}

	static {
		ObjectArrayList<SoundEvent> list = new ObjectArrayList<>();
		list.add(DRAGON_AMBIENT = create("entity.dragon.ambient"));
		list.add(DRAGON_AMBIENT_WATER = create("entity.dragon.ambient.water"));
		list.add(DRAGON_CHEST = create("entity.dragon.chest"));
		list.add(DRAGON_DEATH = create("entity.dragon.death"));
		list.add(DRAGON_DEATH_ZOMBIE = create("entity.dragon.death.zombie"));
		list.add(DRAGON_ROAR = create("entity.dragon.roar"));
		list.add(DRAGON_ROAR_HATCHLING = create("entity.dragon.roar.hatchling"));
		list.add(DRAGON_ROAR_WATER = create("entity.dragon.roar.water"));
		list.add(DRAGON_PURR = create("entity.dragon.purr"));
		list.add(DRAGON_PURR_HATCHLING = create("entity.dragon.purr.hatchling"));
		list.add(DRAGON_PURR_NETHER = create("entity.dragon.purr.nether"));
		list.add(DRAGON_PURR_NETHER_HATCHLING = create("entity.dragon.purr.nether.hatchling"));
		list.add(DRAGON_PURR_SKELETON = create("entity.dragon.purr.skeleton"));
		list.add(DRAGON_PURR_SKELETON_HATCHLING = create("entity.dragon.purr.skeleton.hatchling"));
		list.add(DRAGON_PURR_ZOMBIE = create("entity.dragon.purr.zombie"));
		list.add(DRAGON_SNEEZE = create("entity.dragon.sneeze"));
		list.add(DRAGON_STEP = create("entity.dragon.step"));
		list.add(DRAGON_BREATH_START_ADULT = create("entity.dragon.breath_start.adult"));
		list.add(DRAGON_BREATH_START_JUVENILE = create("entity.dragon.breath_start.juvenile"));
		list.add(DRAGON_BREATH_START_HATCHLING = create("entity.dragon.breath_start.hatchling"));
		list.add(DRAGON_BREATH_START_ICE = create("entity.dragon.breath_start.ice"));
		list.add(DRAGON_BREATH_START_AIRFLOW = create("entity.dragon.breath_start.airflow"));
		list.add(DRAGON_BREATH_START_WATER = create("entity.dragon.breath_start.water"));
		list.add(DRAGON_BREATH_LOOP_ADULT = create("entity.dragon.breath_loop.adult"));
		list.add(DRAGON_BREATH_LOOP_JUVENILE = create("entity.dragon.breath_loop.juvenile"));
		list.add(DRAGON_BREATH_LOOP_HATCHLING = create("entity.dragon.breath_loop.hatchling"));
		list.add(DRAGON_BREATH_LOOP_ICE = create("entity.dragon.breath_loop.ice"));
		list.add(DRAGON_BREATH_LOOP_AIRFLOW = create("entity.dragon.breath_loop.airflow"));
		list.add(DRAGON_BREATH_LOOP_WATER = create("entity.dragon.breath_loop.water"));
		list.add(DRAGON_BREATH_STOP_ADULT = create("entity.dragon.breath_stop.adult"));
		list.add(DRAGON_BREATH_STOP_JUVENILE = create("entity.dragon.breath_stop.juvenile"));
		list.add(DRAGON_BREATH_STOP_HATCHLING = create("entity.dragon.breath_stop.hatchling"));
		list.add(DRAGON_BREATH_STOP_ICE = create("entity.dragon.breath_stop.ice"));
		list.add(DRAGON_BREATH_STOP_AIRFLOW = create("entity.dragon.breath_stop.airflow"));
		list.add(DRAGON_BREATH_STOP_WATER = create("entity.dragon.breath_stop.water"));
		list.add(DRAGON_EGG_CRACK = create("entity.dragon_egg.crack"));
		list.add(DRAGON_EGG_SHATTER = create("entity.dragon_egg.shatter"));
		list.add(FLUTE_BLOW_SHORT = create("item.flute.blow.short"));
		list.add(FLUTE_BLOW_LONG = create("item.flute.blow.long"));
		list.add(VARIATION_ORB_ACTIVATE = create("item.variation_orb.activate"));
		INSTANCES = ObjectLists.unmodifiable(list);
	}
}
