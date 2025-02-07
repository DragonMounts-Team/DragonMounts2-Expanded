package net.dragonmounts.init;

import net.dragonmounts.capability.ArmorEffectManager;
import net.dragonmounts.capability.IArmorEffectManager;
import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.capability.IHardShears;
import net.dragonmounts.food.CommonFood;
import net.dragonmounts.util.DummyStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import static net.dragonmounts.DragonMounts.makeId;

public class DMCapabilities {
    public static final ResourceLocation ARMOR_EFFECT_MANAGER_ID = makeId("armor_effect_manager");
    public static final ResourceLocation DRAGON_FOOD_ID = makeId("dragon_food");

    @CapabilityInject(IArmorEffectManager.class)
    public static final Capability<IArmorEffectManager> ARMOR_EFFECT_MANAGER = null;

    @CapabilityInject(IDragonFood.class)
    public static final Capability<IDragonFood> DRAGON_FOOD = null;

    @CapabilityInject(IHardShears.class)
    public static final Capability<IHardShears> HARD_SHEARS = null;

    public static void register() {
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
