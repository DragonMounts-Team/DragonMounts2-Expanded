package top.dragonmounts.event;

import top.dragonmounts.DragonMounts;
import top.dragonmounts.DragonMountsTags;
import top.dragonmounts.client.gui.GuiHandler;
import top.dragonmounts.inits.*;
import top.dragonmounts.inits.*;
import top.dragonmounts.objects.blocks.BlockDragonBreedEgg;
import top.dragonmounts.objects.entity.entitytameabledragon.breeds.DragonBreedForest;
import top.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import top.dragonmounts.objects.items.EnumItemBreedTypes;
import top.dragonmounts.objects.items.ItemDragonBreedEgg;
import top.dragonmounts.objects.tileentities.TileEntityDragonShulker;
import top.dragonmounts.registry.CooldownCategory;
import top.dragonmounts.util.DMUtils;
import top.dragonmounts.util.IHasModel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.function.Consumer;

import static top.dragonmounts.DragonMounts.makeId;

@Mod.EventBusSubscriber
public class RegistryEventHandler {
	
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ModBlocks.BLOCKS.forEach(event.getRegistry()::register);
        event.getRegistry().register(BlockDragonBreedEgg.DRAGON_BREED_EGG.setRegistryName("dragon_egg"));
        GameRegistry.registerTileEntity(TileEntityDragonShulker.class, makeId("dragon_shulker"));
        DMUtils.getLogger().info("Block Registries Successfully Registered");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Consumer<Item> register = event.getRegistry()::register;
        ModItems.ITEMS.forEach(register);
        ModTools.TOOLS.forEach(register);
        DMArmors.ARMOR.forEach(register);
        register.accept(ItemDragonBreedEgg.DRAGON_BREED_EGG.setRegistryName("dragon_egg"));
        DMUtils.getLogger().info("Item Registries Successfully Registered!");
    }

    @SubscribeEvent
    public static void registerCooldownCategory(RegistryEvent.Register<CooldownCategory> event) {
        IForgeRegistry<CooldownCategory> registry = event.getRegistry();
        registry.register(DMArmorEffects.AETHER_EFFECT);
        registry.register(DMArmorEffects.ENDER_EFFECT);
        registry.register(DMArmorEffects.FIRE_EFFECT);
        registry.register(DMArmorEffects.FOREST_EFFECT);
        registry.register(DMArmorEffects.ICE_EFFECT);
        registry.register(DMArmorEffects.NETHER_EFFECT);
        registry.register(DMArmorEffects.SUNLIGHT_EFFECT);
        registry.register(DMArmorEffects.ZOMBIE_EFFECT);
    }

    @SubscribeEvent
    public static void registerDataSerializer(RegistryEvent.Register<DataSerializerEntry> event) {
        IForgeRegistry<DataSerializerEntry> registry = event.getRegistry();
        registry.register(new DataSerializerEntry(EnumDragonBreed.SERIALIZER).setRegistryName(DragonMountsTags.MOD_ID + ":dragon_breed"));
        registry.register(new DataSerializerEntry(DragonBreedForest.SubType.SERIALIZER).setRegistryName(DragonMountsTags.MOD_ID + ":forest_type"));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        //DragonMounts.proxy.registerModel(Item.getItemFromBlock(ModBlocks.DRAGONSHULKER), 0);
        for (Block block : ModBlocks.BLOCKS) {
        	if (block instanceof IHasModel) {
        		((IHasModel) block).RegisterModels();
        	}
        }

        for (Item item : ModItems.ITEMS) {
            if (item instanceof IHasModel) {
                ((IHasModel) item).RegisterModels();
            }
        }

        for (Item item : ModTools.TOOLS) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName().toString(), "inventory"));
        }

        for (Item item : DMArmors.ARMOR) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName().toString(), "inventory"));
        }

        // register item renderer for dragon egg block variants
        String modelLocation = DragonMountsTags.MOD_ID + ":dragon_egg";
        Arrays.stream(EnumDragonBreed.values()).forEach(breed -> ModelLoader.setCustomModelResourceLocation(
                ItemDragonBreedEgg.DRAGON_BREED_EGG,
                breed.meta,
                new ModelResourceLocation(modelLocation, "breed=" + breed.identifier)
        ));

        {// Amulets
            EnumItemBreedTypes[] types = EnumItemBreedTypes.values();
            int size = types.length;
            Object2ObjectOpenHashMap<String, ModelResourceLocation> mapping = new Object2ObjectOpenHashMap<>();
            ModelResourceLocation empty = new ModelResourceLocation("dragonmounts:dragon_amulet");
            ModelResourceLocation[] models = new ModelResourceLocation[size + 1];
            models[0] = empty;
            for (int i = 0; i < size; ) {
                EnumItemBreedTypes breed = types[i];
                ModelResourceLocation model = new ModelResourceLocation("dragonmounts:" + breed.identifier + "_dragon_amulet");
                mapping.put(breed.identifier, model);
                models[++i] = model;
            }
            ModelLoader.setCustomMeshDefinition(ModItems.Amulet, stack -> {
                NBTTagCompound root = stack.getTagCompound();
                return root == null ? empty : mapping.get(root.getString("breed"));
            });
            ModelBakery.registerItemVariants(ModItems.Amulet, models);
        }
        DMUtils.getLogger().info("Models Sucessfully Registered");
    }

    public static void preInitRegistries() {
    }

    public static void initRegistries() {
        NetworkRegistry.INSTANCE.registerGuiHandler(DragonMounts.instance, new GuiHandler());
        DMUtils.getLogger().info("Gui's Successfully Registered");
    }
}