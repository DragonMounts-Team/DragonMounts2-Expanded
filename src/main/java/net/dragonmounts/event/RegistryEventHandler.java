package net.dragonmounts.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.block.entity.DragonHeadBlockEntity;
import net.dragonmounts.capability.ArmorEffectManager;
import net.dragonmounts.capability.IArmorEffectManager;
import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.capability.IHardShears;
import net.dragonmounts.compat.DragonMountsCompat;
import net.dragonmounts.compat.DragonTypeCompat;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.food.CommonFood;
import net.dragonmounts.init.*;
import net.dragonmounts.registry.CarriageType;
import net.dragonmounts.registry.CooldownCategory;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.DummyStorage;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;
import java.util.function.Function;

import static net.dragonmounts.DragonMounts.makeId;
import static net.dragonmounts.DragonMountsTags.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class RegistryEventHandler {
    public static final ResourceLocation ARMOR_EFFECT_MANAGER_ID = makeId("armor_effect_manager");
    public static final ResourceLocation DRAGON_FOOD_ID = makeId("dragon_food");

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
        registry.register(DragonTypes.ENCHANT);
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
        registry.register(new DataSerializerEntry(CarriageType.SERIALIZER).setRegistryName(MOD_ID + ":carriage_type"));
        registry.register(new DataSerializerEntry(DragonVariant.SERIALIZER).setRegistryName(MOD_ID + ":dragon_variant"));
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> registry = event.getRegistry();
        registry.register(DMEntities.DRAGON);
        registry.register(DMEntities.CARRIAGE);
        registry.register(DMEntities.CONTAINER_ITEM);
    }

    @SubscribeEvent
    public static void registerMobEffects(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(DMMobEffects.DARK_DRAGONS_GRACE);
    }

    @SubscribeEvent
    public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();
        registry.register(DMSounds.ENTITY_DRAGON_STEP);
        registry.register(DMSounds.ENTITY_DRAGON_BREATHE);
        registry.register(DMSounds.ENTITY_DRAGON_DEATH);
        registry.register(DMSounds.ENTITY_DRAGON_GROWL);
        registry.register(DMSounds.ENTITY_NETHER_DRAGON_GROWL);
        registry.register(DMSounds.ENTITY_SKELETON_DRAGON_GROWL);
        registry.register(DMSounds.ENTITY_DRAGON_HATCHLING_GROWL);
        registry.register(DMSounds.ENTITY_HATCHLING_NETHER_DRAGON_GROWL);
        registry.register(DMSounds.ENTITY_HATCHLING_SKELETON_DRAGON_GROWL);
        registry.register(DMSounds.ZOMBIE_DRAGON_GROWL);
        registry.register(DMSounds.ZOMBIE_DRAGON_DEATH);
        registry.register(DMSounds.DRAGON_SNEEZE);
        registry.register(DMSounds.DRAGON_HATCHED);
        registry.register(DMSounds.DRAGON_HATCHING);
        registry.register(DMSounds.DRAGON_WHISTLE);
        registry.register(DMSounds.DRAGON_WHISTLE1);
        registry.register(DMSounds.DRAGON_ROAR);
        registry.register(DMSounds.HATCHLING_DRAGON_ROAR);
        registry.register(DMSounds.DRAGON_SWITCH);
        for (SoundEffectName sound : SoundEffectName.values()) {
            registry.register(sound.sound);
        }
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
        {// Compat: register item model for dragon egg variants
            Item egg = DragonMountsCompat.DRAGON_EGG_ITEM;
            String model = MOD_ID + ":dragon_egg";
            for (DragonTypeCompat type : DragonTypeCompat.values()) {
                ModelLoader.setCustomModelResourceLocation(egg, type.ordinal(), new ModelResourceLocation(model, "breed=" + type.identifier));
            }
        }
        {// Compat: register item model for amulet variants
            DragonTypeCompat[] types = DragonTypeCompat.values();
            int size = types.length;
            Object2ObjectOpenHashMap<String, ModelResourceLocation> mapping = new Object2ObjectOpenHashMap<>();
            Reference2IntOpenHashMap<DragonType> meta = new Reference2IntOpenHashMap<>();
            ModelResourceLocation empty = new ModelResourceLocation("dragonmounts:amulet");
            ModelResourceLocation[] models = new ModelResourceLocation[size + 1];
            models[0] = empty;
            for (int i = 0; i < size; ) {
                DragonType type = types[i].type;
                models[++i] = new ModelResourceLocation("dragonmounts:" + type.identifier.getPath() + "_dragon_amulet");
                meta.put(type, i);
            }
            Function<String, ModelResourceLocation> computer = variant -> models[meta.getOrDefault(DragonVariant.byName(variant).type, 0)];
            ModelLoader.setCustomMeshDefinition(amulet, stack -> {
                NBTTagCompound root = stack.getTagCompound();
                if (root == null) return empty;
                NBTTagCompound data = root.getCompoundTag("EntityTag");
                if (data.isEmpty()) return empty;
                return mapping.computeIfAbsent(data.getString(DragonVariant.DATA_PARAMETER_KEY), computer);
            });
            ModelBakery.registerItemVariants(amulet, models);
        }
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IArmorEffectManager.class, new ArmorEffectManager.Storage(), () -> null);
        CapabilityManager.INSTANCE.register(IDragonFood.class, new DragonFoods.Storage(), () -> IDragonFood.EMPTY);
        CapabilityManager.INSTANCE.register(IHardShears.class, new DummyStorage<>(), () -> null);
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof EntityPlayer) {
            event.addCapability(ARMOR_EFFECT_MANAGER_ID, new ArmorEffectManager.LazyProvider((EntityPlayer) entity));
        }
    }

    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        Item item = stack.getItem();
        if (item == Items.FISH && stack.getMetadata() == ItemFishFood.FishType.PUFFERFISH.getMetadata()) return;
        ICapabilityProvider provider = DragonFoods.getProvider(item);
        if (provider == null) {
            if (item instanceof ItemFood) {
                ItemFood food = (ItemFood) item;
                if (food.isWolfsFavoriteMeat()) {
                    int level = food.getHealAmount(stack) * 2;
                    event.addCapability(DRAGON_FOOD_ID, level < 10
                            ? new CommonFood(level, 1500, 0.125F * level + 0.5F, 0.25F)
                            : new CommonFood(level, 2500, 0.125F * level + 0.5F, 0.375F)
                    );
                    return;
                }
            }
            if (!stack.isEmpty() && ArrayUtils.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID("listAllfishraw"))) {
                event.addCapability(DRAGON_FOOD_ID, DragonFoods.RAW_FISH);
            }
        } else {
            event.addCapability(DRAGON_FOOD_ID, provider);
        }
    }
}