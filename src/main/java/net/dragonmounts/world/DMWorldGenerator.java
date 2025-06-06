package net.dragonmounts.world;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.config.DMConfig;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

import static net.minecraft.world.gen.structure.MapGenStructureIO.registerStructure;
import static net.minecraft.world.gen.structure.MapGenStructureIO.registerStructureComponent;

/**
 * Handles world generation for dragon nests, make a separate package if we are gonna use Mappers to optimize instead of the IWorldGenerator
 */
public class DMWorldGenerator implements IWorldGenerator {
    public static void init() {
        registerStructure(DragonNest.Start.class, "DM2EDN");
        registerStructureComponent(DragonNestPiece.class, "DM2EDN");
        GameRegistry.registerWorldGenerator(new DMWorldGenerator(), 0);
        BiomeDictionary.addTypes(Biomes.STONE_BEACH, BiomeDictionary.Type.getType("STONE_BEACH", BiomeDictionary.Type.BEACH));
        Loader.instance().getActiveModList().forEach(DRAGON_NESTS::loadNests);
    }

    private static final PlacementSettings PLACEMENT_SETTINGS = new PlacementSettings().setIgnoreEntities(false).setIgnoreStructureBlock(true);
    public static final ResourceLocation AETHER = new ResourceLocation(DragonMountsTags.MOD_ID, "aether");
    public static final ResourceLocation MOONLIGHT = new ResourceLocation(DragonMountsTags.MOD_ID, "moonlight");
    public static final ResourceLocation ICE = new ResourceLocation(DragonMountsTags.MOD_ID, "ice");
    public static final ResourceLocation FOREST1 = new ResourceLocation(DragonMountsTags.MOD_ID, "forest1");
    public static final ResourceLocation SUNLIGHT = new ResourceLocation(DragonMountsTags.MOD_ID, "sunlight");
    public static final ResourceLocation TERRA = new ResourceLocation(DragonMountsTags.MOD_ID, "terra");
    public static final ResourceLocation WATER3 = new ResourceLocation(DragonMountsTags.MOD_ID, "water3");
    public static final ResourceLocation FOREST2 = new ResourceLocation(DragonMountsTags.MOD_ID, "forest2");
    public static final ResourceLocation FIRE = new ResourceLocation(DragonMountsTags.MOD_ID, "fire");
    public static final ResourceLocation NETHER = new ResourceLocation(DragonMountsTags.MOD_ID, "nether");
    public static final ResourceLocation ZOMBIE = new ResourceLocation(DragonMountsTags.MOD_ID, "zombie");
    public static final ResourceLocation SKELETON = new ResourceLocation(DragonMountsTags.MOD_ID, "skeleton");
    public static final ResourceLocation ENCHANTED = new ResourceLocation(DragonMountsTags.MOD_ID, "enchanted");
    public static final DragonNestRegistry DRAGON_NESTS = new DragonNestRegistry(60052411, 40, 15);

    @Override
    public void generate(Random random, int x, int z, World world, IChunkGenerator generator, IChunkProvider provider) {
        if (world.isRemote ||
                // isGenerationDisabled:
                DMConfig.LIMITED_DIMENSIONS.value.contains(world.provider.getDimension()) != DMConfig.ALLOW_DECLARED_DIMENSIONS_ONLY.value
        ) return;
        DRAGON_NESTS.generate(world, x, z, random);
    }
}