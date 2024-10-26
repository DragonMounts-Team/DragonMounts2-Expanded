package com.TheRPGAdventurer.ROTD;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;

import static com.TheRPGAdventurer.ROTD.DragonMounts.makeId;

public class DragonMountsLootTables {
	/**
	 * Stores the IDs of this mod's {@link LootTable}s.
	 */
	private static final ObjectArrayList<ResourceLocation> LOOT_TABLES = new ObjectArrayList<>();

	public static final ResourceLocation ENTITIES_DRAGON_WATER = create("water");
	public static final ResourceLocation ENTITIES_DRAGON_FIRE = create("fire");
	public static final ResourceLocation ENTITIES_DRAGON_FIRE2 = create("fire2");
	public static final ResourceLocation ENTITIES_DRAGON_FOREST = create("forest");
	public static final ResourceLocation ENTITIES_DRAGON_FOREST2 = create("forest2");
	public static final ResourceLocation ENTITIES_DRAGON_ICE = create("ice");
	public static final ResourceLocation ENTITIES_DRAGON_AETHER = create("aether");
	public static final ResourceLocation ENTITIES_DRAGON_END = create("ender");
	public static final ResourceLocation ENTITIES_DRAGON_NETHER = create("nether");
	public static final ResourceLocation ENTITIES_DRAGON_NETHER2 = create("nether2");
	public static final ResourceLocation ENTITIES_DRAGON_SKELETON = create("skeleton");
	public static final ResourceLocation ENTITIES_DRAGON_SUNLIGHT = create("sunlight");
	public static final ResourceLocation ENTITIES_DRAGON_SUNLIGHT2 = create("sunlight2");
	public static final ResourceLocation ENTITIES_DRAGON_STORM = create("storm");
	public static final ResourceLocation ENTITIES_DRAGON_STORM2 = create("storm2");
	public static final ResourceLocation ENTITIES_DRAGON_ENCHANT = create("enchant");
	public static final ResourceLocation ENTITIES_DRAGON_ZOMBIE = create("zombie");
	public static final ResourceLocation ENTITIES_DRAGON_TERRA = create("terra");
	public static final ResourceLocation ENTITIES_DRAGON_TERRA2 = create("terra2");
	public static final ResourceLocation ENTITIES_DRAGON_MOONLIGHT = create("moonlight");
//	public static final ResourceLocation ENTITIES_DRAGON_LIGHT = create("light");
//	public static final ResourceLocation ENTITIES_DRAGON_DARK = create("DARK");
//	public static final ResourceLocation ENTITIES_DRAGON_SPECTER = create("specter");

	/**
	 * Create a {@link LootTable} ID.
	 *
	 * @param name The ID of the LootTable without namespace
	 * @return The ID of the LootTable
	 */
	protected static ResourceLocation create(String name) {
		final ResourceLocation lootTable = makeId(name);
		LOOT_TABLES.add(lootTable);
		return lootTable;
	}

	/**
	 * Register this mod's {@link LootTable}s.
	 */
	public static void registerLootTables() {
		for (ResourceLocation lootTable : LOOT_TABLES) {
			LootTableList.register(lootTable);
		}
	}
}