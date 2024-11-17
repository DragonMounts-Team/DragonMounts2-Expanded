package top.dragonmounts.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import top.dragonmounts.DragonMountsTags;
import top.dragonmounts.world.config.EnchantConfig;
import top.dragonmounts.world.config.IDragonNestConfig;
import top.dragonmounts.world.config.SimpleConfig;

public class DMStructures {
    public static final Object2ObjectOpenHashMap<String, DragonNestStructure> DRAGON_NESTS = new Object2ObjectOpenHashMap<>();

    public static DragonNestStructure makeDragonNest(String identifier, IDragonNestConfig config, int salt) {
        DragonNestStructure nest = new DragonNestStructure(config, salt);
        DRAGON_NESTS.put(identifier, nest);
        return nest;
    }

    public static final DragonNestStructure AETHER_DRAGON_NEST = makeDragonNest("aether", new SimpleConfig(
            "AetherDragonNest",
            DragonMountsTags.MOD_ID + ":aether",
            LootTableList.CHESTS_SIMPLE_DUNGEON,
            BiomeDictionary.Type.OCEAN,
            10
    ), 0x9E9F77);

    public static final DragonNestStructure ENCHANT_DRAGON_NEST = makeDragonNest("enchant", new EnchantConfig(
    ), 0x9E9F31);
}
