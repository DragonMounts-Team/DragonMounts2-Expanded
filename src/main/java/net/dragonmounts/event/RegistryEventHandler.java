package net.dragonmounts.event;

import com.google.common.util.concurrent.Callables;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.dragonmounts.block.DragonEggCompatBlock;
import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.block.entity.DragonHeadBlockEntity;
import net.dragonmounts.capability.*;
import net.dragonmounts.compat.DragonMountsCompat;
import net.dragonmounts.compat.DragonTypeCompat;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.init.*;
import net.dragonmounts.inventory.FluteHolder;
import net.dragonmounts.registry.CarriageType;
import net.dragonmounts.registry.CooldownCategory;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.DummyStorage;
import net.dragonmounts.util.SerializableProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

import static net.dragonmounts.DragonMounts.applyId;
import static net.dragonmounts.DragonMounts.makeId;
import static net.dragonmounts.DragonMountsTags.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class RegistryEventHandler {
    public static final ResourceLocation ARMOR_EFFECT_MANAGER_ID = makeId("armor_effect_manager");
    public static final ResourceLocation FLUTE_HOLDER_ID = makeId("flute_holder");

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        for (Block block : DMBlocks.BLOCKS) {
            registry.register(block);
        }
        for (DragonVariant variant : DragonVariants.BUILTIN_VALUES) {
            registry.register(variant.head.wall);
            registry.register(variant.head.standing);
        }
        registry.register(DragonMountsCompat.DRAGON_EGG_BLOCK);
        GameRegistry.registerTileEntity(DragonCoreBlockEntity.class, makeId("dragon_core"));
        GameRegistry.registerTileEntity(DragonHeadBlockEntity.class, makeId("dragon_head"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        ObjectArrayList<Item> items = DMItems.ITEMS;
        for (Item item : items) {
            registry.register(item);
        }
        registry.register(DragonMountsCompat.DRAGON_EGG_ITEM);
        if (DMConfig.DEBUG_MODE.value) {
            registry.register(DMItems.DRAGON_ORB);
            registry.register(DMItems.TEST_RUNNER);
        }
    }

    @SubscribeEvent
    public static void registerCarriageTypes(RegistryEvent.Register<CarriageType> event) {
        IForgeRegistry<CarriageType> registry = event.getRegistry();
        registry.register(CarriageTypes.OAK);
        registry.register(CarriageTypes.SPRUCE);
        registry.register(CarriageTypes.BIRCH);
        registry.register(CarriageTypes.JUNGLE);
        registry.register(CarriageTypes.ACACIA);
        registry.register(CarriageTypes.DARK_OAK);
    }

    @SubscribeEvent
    public static void registerCooldownCategories(RegistryEvent.Register<CooldownCategory> event) {
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
    public static void registerDragonTypes(RegistryEvent.Register<DragonType> event) {
        IForgeRegistry<DragonType> registry = event.getRegistry();
        registry.register(DragonTypes.AETHER);
        registry.register(DragonTypes.ENCHANTED);
        registry.register(DragonTypes.ENDER);
        registry.register(DragonTypes.FIRE);
        registry.register(DragonTypes.FOREST);
        registry.register(DragonTypes.ICE);
        registry.register(DragonTypes.MOONLIGHT);
        registry.register(DragonTypes.NETHER);
        registry.register(DragonTypes.SKELETON);
        registry.register(DragonTypes.STORM);
        registry.register(DragonTypes.SUNLIGHT);
        registry.register(DragonTypes.TERRA);
        registry.register(DragonTypes.WATER);
        registry.register(DragonTypes.WITHER);
        registry.register(DragonTypes.ZOMBIE);
        registry.register(DragonTypes.DARK);
    }

    @SubscribeEvent
    public static void registerDragonVariants(RegistryEvent.Register<DragonVariant> event) {
        DragonVariants.BUILTIN_VALUES.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void registerDataSerializers(RegistryEvent.Register<DataSerializerEntry> event) {
        IForgeRegistry<DataSerializerEntry> registry = event.getRegistry();
        registry.register(applyId(new DataSerializerEntry(CarriageType.SERIALIZER), "carriage_type"));
        registry.register(applyId(new DataSerializerEntry(DragonVariant.SERIALIZER), "dragon_variant"));
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> registry = event.getRegistry();
        registry.register(DMEntities.DRAGON);
        registry.register(DMEntities.CARRIAGE);
    }

    @SubscribeEvent
    public static void registerMobEffects(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(DMMobEffects.DARK_DRAGONS_GRACE);
    }

    @SubscribeEvent
    public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
        DMSounds.INSTANCES.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Item amulet = DMItems.AMULET;
        for (Item item : DMItems.ITEMS) {
            if (amulet == item) continue;
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
        }
        ModelLoader.setCustomModelResourceLocation(DMItems.DRAGON_ORB, 0, new ModelResourceLocation("dragonmounts:dragon_orb", "inventory"));
        ModelLoader.setCustomModelResourceLocation(DMItems.TEST_RUNNER, 0, new ModelResourceLocation("dragonmounts:test_runner", "inventory"));
        {
            Item egg = DragonMountsCompat.DRAGON_EGG_ITEM;
            Object2IntOpenHashMap<String> cache = new Object2IntOpenHashMap<>();
            Reference2IntOpenHashMap<DragonType> mapping = new Reference2IntOpenHashMap<>();
            DragonTypeCompat[] types = DragonTypeCompat.values();
            int size = types.length;
            ModelResourceLocation[] models = new ModelResourceLocation[size + 1];
            models[0] = new ModelResourceLocation("dragonmounts:amulet");
            for (int i = 0; i < size; ) {
                DragonTypeCompat compat = types[i];
                // Compat: register item model for dragon egg variants
                ModelLoader.setCustomModelResourceLocation(egg, i, new ModelResourceLocation(DragonEggCompatBlock.IDENTIFIER, "breed=" + compat.identifier));
                DragonType type = compat.type;
                models[++i] = new ModelResourceLocation("dragonmounts:" + type.identifier.getPath() + "_dragon_amulet");
                mapping.put(type, i);
            }
            // Compat: register item model for amulet variants
            ModelBakery.registerItemVariants(amulet, models);
            ModelLoader.setCustomMeshDefinition(amulet, stack -> {
                NBTTagCompound root = stack.getTagCompound();
                if (root == null) return models[0];
                NBTTagCompound data = root.getCompoundTag("EntityTag");
                if (data.isEmpty()) return models[0];
                String variant = data.getString(DragonVariant.DATA_PARAMETER_KEY);
                if (cache.containsKey(variant)) return models[cache.getInt(variant)];
                int model = mapping.getInt(DragonVariant.byName(variant).type);
                cache.put(variant, model);
                return models[model];
            });
        }
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IArmorEffectManager.class, new ArmorEffectManager.Storage(), Callables.returning(null));
        CapabilityManager.INSTANCE.register(IDragonFood.class, new DragonFoods.Storage(), Callables.returning(IDragonFood.EMPTY));
        CapabilityManager.INSTANCE.register(IHardShears.class, new DummyStorage<>(), Callables.returning(null));
        CapabilityManager.INSTANCE.register(IFluteHolder.class, new FluteHolder.Storage(), FluteHolder::new);
    }

    public static void registerRecipes() {
        GameRegistry.addSmelting(DMItems.DRAGON_MEAT, new ItemStack(DMItems.COOKED_DRAGON_MEAT), 0.35F);
        GameRegistry.addSmelting(DMItems.IRON_DRAGON_ARMOR, new ItemStack(Items.IRON_INGOT), 0.7F);
        GameRegistry.addSmelting(DMItems.GOLDEN_DRAGON_ARMOR, new ItemStack(Items.GOLD_INGOT), 1.0F);
        NonNullList<ItemStack> coppers = OreDictionary.getOres("ingotCopper");
        if (coppers.isEmpty()) return;
        GameRegistry.addSmelting(DMItems.COPPER_DRAGON_ARMOR, coppers.get(0), 0.7F);
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof EntityPlayer) {
            event.addCapability(
                    ARMOR_EFFECT_MANAGER_ID,
                    new SerializableProvider<>(DMCapabilities.ARMOR_EFFECT_MANAGER, new ArmorEffectManager((EntityPlayer) entity))
            );
            event.addCapability(
                    FLUTE_HOLDER_ID,
                    new SerializableProvider<>(DMCapabilities.FLUTE_HOLDER, new FluteHolder())
            );
        }
    }
}