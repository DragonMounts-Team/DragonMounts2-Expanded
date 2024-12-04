package net.dragonmounts;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.LootTableLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;

public class DragonMountsLootTables {
	/**
	 * Stores the IDs of this mod's {@link LootTable}s.
	 */
	private static final ObjectArrayList<ResourceLocation> LOOT_TABLES = new ObjectArrayList<>();

	public static final ResourceLocation ENTITIES_DRAGON_WATER = create(DragonTypes.WATER, "water");
	public static final ResourceLocation ENTITIES_DRAGON_FIRE = create(DragonTypes.FIRE, "fire");
	public static final ResourceLocation ENTITIES_DRAGON_FOREST = create(DragonTypes.FOREST, "forest");
	public static final ResourceLocation ENTITIES_DRAGON_ICE = create(DragonTypes.ICE, "ice");
	public static final ResourceLocation ENTITIES_DRAGON_AETHER = create(DragonTypes.AETHER, "aether");
	public static final ResourceLocation ENTITIES_DRAGON_END = create(DragonTypes.ENDER, "ender");
	public static final ResourceLocation ENTITIES_DRAGON_NETHER = create(DragonTypes.NETHER, "nether");
	public static final ResourceLocation ENTITIES_DRAGON_SKELETON = create(DragonTypes.SKELETON, "skeleton");
	public static final ResourceLocation ENTITIES_DRAGON_SUNLIGHT = create(DragonTypes.SUNLIGHT, "sunlight");
	public static final ResourceLocation ENTITIES_DRAGON_STORM = create(DragonTypes.STORM, "storm");
	public static final ResourceLocation ENTITIES_DRAGON_ENCHANT = create(DragonTypes.ENCHANT, "enchant");
	public static final ResourceLocation ENTITIES_DRAGON_ZOMBIE = create(DragonTypes.ZOMBIE, "zombie");
	public static final ResourceLocation ENTITIES_DRAGON_TERRA = create(DragonTypes.TERRA, "terra");
	public static final ResourceLocation ENTITIES_DRAGON_MOONLIGHT = create(DragonTypes.MOONLIGHT, "moonlight");
//	public static final ResourceLocation ENTITIES_DRAGON_LIGHT = create("light");
//	public static final ResourceLocation ENTITIES_DRAGON_DARK = create("DARK");
//	public static final ResourceLocation ENTITIES_DRAGON_SPECTER = create("specter");

	static ResourceLocation create(DragonType type, String name) {
		final LootTableLocation lootTable = new LootTableLocation(DragonMountsTags.MOD_ID, name);
		type.bindInstance(LootTableLocation.class, lootTable);
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