package com.TheRPGAdventurer.ROTD.event;

import com.TheRPGAdventurer.ROTD.DragonMounts;
import com.TheRPGAdventurer.ROTD.DragonMountsTags;
import com.TheRPGAdventurer.ROTD.client.gui.GuiHandler;
import com.TheRPGAdventurer.ROTD.inits.*;
import com.TheRPGAdventurer.ROTD.objects.blocks.BlockDragonBreedEgg;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import com.TheRPGAdventurer.ROTD.objects.items.ItemDragonBreedEgg;
import com.TheRPGAdventurer.ROTD.objects.tileentities.TileEntityDragonShulker;
import com.TheRPGAdventurer.ROTD.registry.CooldownCategory;
import com.TheRPGAdventurer.ROTD.util.DMUtils;
import com.TheRPGAdventurer.ROTD.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Consumer;

import static com.TheRPGAdventurer.ROTD.DragonMounts.makeId;

@Mod.EventBusSubscriber
public class RegistryEventHandler {
	
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ModBlocks.BLOCKS.forEach(event.getRegistry()::register);
        GameRegistry.registerTileEntity(TileEntityDragonShulker.class, makeId("dragon_shulker"));
        DMUtils.getLogger().info("Block Registries Successfully Registered");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Consumer<Item> register = event.getRegistry()::register;
        ModItems.ITEMS.forEach(register);
        ModTools.TOOLS.forEach(register);
        ModArmour.ARMOR.forEach(register);
        
        DMUtils.getLogger().info("Item Registries Successfully Registered!");
    }
    
	@SubscribeEvent
	public static void registerDragonEggItem(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(ItemDragonBreedEgg.DRAGON_BREED_EGG.setRegistryName("dragon_egg"));
	}

	@SubscribeEvent
	public static void registerDragonnEggBlock(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(BlockDragonBreedEgg.DRAGON_BREED_EGG.setRegistryName("dragon_egg"));
	}

    @SubscribeEvent
    public static void registerCooldownCategory(RegistryEvent.Register<CooldownCategory> event) {
        IForgeRegistry<CooldownCategory> registry = event.getRegistry();
        registry.register(DMArmorEffects.AETHER_EFFECT.setRegistryName("aether"));
        registry.register(DMArmorEffects.ENDER_EFFECT.setRegistryName("ender"));
        registry.register(DMArmorEffects.FIRE_EFFECT.setRegistryName("fire"));
        registry.register(DMArmorEffects.FOREST_EFFECT.setRegistryName("forest"));
        registry.register(DMArmorEffects.ICE_EFFECT.setRegistryName("ice"));
        registry.register(DMArmorEffects.NETHER_EFFECT.setRegistryName("nether"));
        registry.register(DMArmorEffects.SUNLIGHT_EFFECT.setRegistryName("sunlight"));
        registry.register(DMArmorEffects.ZOMBIE_EFFECT.setRegistryName("zombie"));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        //DragonMounts.proxy.registerModel(Item.getItemFromBlock(ModBlocks.DRAGONSHULKER), 0);

        // Register item render for amulet item variants
        DragonMounts.proxy.registerAmuletRenderer();
        
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

        for (Item item : ModArmour.ARMOR) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName().toString(), "inventory"));
        }

        // register item renderer for dragon egg block variants
        Item eggItem = ItemDragonBreedEgg.DRAGON_BREED_EGG;
        String modelLocation = DragonMountsTags.MOD_ID + ":dragon_egg";
        EnumDragonBreed.META_MAPPING.forEach(
                (breed, meta) -> ModelLoader.setCustomModelResourceLocation(eggItem, meta, new ModelResourceLocation(modelLocation, "breed=" + breed.getName()))
        );

      DMUtils.getLogger().info("Models Sucessfully Registered");
    }

    public static void preInitRegistries() {
    }

    public static void initRegistries() {
        NetworkRegistry.INSTANCE.registerGuiHandler(DragonMounts.instance, new GuiHandler());
        DMUtils.getLogger().info("Gui's Successfully Registered");
    }
}