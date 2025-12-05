package net.dragonmounts.init;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.dragonmounts.capability.DMCapabilities;
import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.food.*;
import net.dragonmounts.util.LogUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DragonFoods {
    private static final Reference2ObjectOpenHashMap<Item, IDragonFood> FALLBACKS = new Reference2ObjectOpenHashMap<>();
    public static final BreedingFood RAW_FISH = new BreedingFood(4, 1500, 0.75F, 0.25F);
    public static final BreedingFood COOKED_FISH = new BreedingFood(10, 2500, 1.5F, 0.375F);
    public static final CommonFood RAW_MEAT = new CommonFood(4, 1500, 1.0F, 0.25F);
    public static final CommonFood COOKED_MEAT = new CommonFood(12, 2500, 2.0F, 0.375F);
    public static final CommonFood PREMIUM_RAW_MEAT = new CommonFood(6, 1500, 1.0F, 0.25F);
    public static final CommonFood PREMIUM_COOKED_MEAT = new CommonFood(16, 2500, 2.0F, 0.375F);

    public static void bindFallback(Item item, IDragonFood food) {
        if (FALLBACKS.put(item, food) != null) {
            LogUtil.LOGGER.warn("Override dragon food fallback for item {}", ForgeRegistries.ITEMS.getKey(item));
        }
    }

    public static IDragonFood getFallback(Item item) {
        return FALLBACKS.get(item);
    }

    public static boolean isDragonFood(ItemStack stack) {
        if (stack.hasCapability(DMCapabilities.DRAGON_FOOD, null)) return true;
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (Items.FISH == item && stack.getMetadata() == ItemFishFood.FishType.PUFFERFISH.getMetadata()) return false;
        IDragonFood food = getFallback(item);
        if (food != null) return food != IDragonFood.EMPTY;
        int[] tags = OreDictionary.getOreIDs(stack);
        if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllfishcooked"))) return true;
        if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllmeatcooked"))) return true;
        if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllfishraw"))) return true;
        if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllmeatraw"))) return true;
        return item instanceof ItemFood && ((ItemFood) item).isWolfsFavoriteMeat();
    }

    public static @Nonnull IDragonFood getFood(ItemStack stack) {
        IDragonFood food = stack.getCapability(DMCapabilities.DRAGON_FOOD, null);
        if (food != null) return food;
        if (stack.isEmpty()) return IDragonFood.EMPTY;
        Item item = stack.getItem();
        if (Items.FISH == item
                && stack.getMetadata() == ItemFishFood.FishType.PUFFERFISH.getMetadata()
        ) return IDragonFood.EMPTY;
        food = getFallback(item);
        if (food != null) return food;
        int[] tags = OreDictionary.getOreIDs(stack);
        if (item instanceof ItemFood) {
            ItemFood info = (ItemFood) item;
            int level = info.getHealAmount(stack) * 2;
            if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllfishcooked"))) return new BreedingFood(
                    level, 2500, 0.125F * level + 0.5F, 0.375F
            );
            if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllmeatcooked"))) return new CommonFood(
                    level, 2500, 0.125F * level + 0.5F, 0.375F
            );
            if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllfishraw"))) return new BreedingFood(
                    level, 1500, 0.125F * level + 0.5F, 0.25F
            );
            if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllmeatraw"))) return new CommonFood(
                    level, 1500, 0.125F * level + 0.5F, 0.25F
            );
            if (info.isWolfsFavoriteMeat()) {
                return level < 10
                        ? new CommonFood(level, 1500, 0.125F * level + 0.5F, 0.25F)
                        : new CommonFood(level, 2500, 0.125F * level + 0.5F, 0.375F);
            }
            return IDragonFood.EMPTY;
        }
        if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllfishcooked"))) return DragonFoods.COOKED_FISH;
        if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllmeatcooked"))) return DragonFoods.COOKED_MEAT;
        if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllfishraw"))) return DragonFoods.RAW_FISH;
        if (ArrayUtils.contains(tags, OreDictionary.getOreID("listAllmeatraw"))) return DragonFoods.RAW_MEAT;
        return IDragonFood.EMPTY;
    }

    static {
        bindFallback(Items.CARROT, new Carrot());
        bindFallback(Items.POISONOUS_POTATO, new PoisonousPotato());

        bindFallback(Items.BEEF, PREMIUM_RAW_MEAT);
        bindFallback(Items.CHICKEN, RAW_MEAT);
        bindFallback(Items.FISH, RAW_FISH);
        bindFallback(Items.PORKCHOP, PREMIUM_RAW_MEAT);
        bindFallback(Items.RABBIT, PREMIUM_RAW_MEAT);
        bindFallback(Items.MUTTON, RAW_MEAT);

        bindFallback(Items.COOKED_BEEF, PREMIUM_COOKED_MEAT);
        bindFallback(Items.COOKED_CHICKEN, COOKED_MEAT);
        bindFallback(Items.COOKED_FISH, COOKED_FISH);
        bindFallback(Items.COOKED_PORKCHOP, PREMIUM_COOKED_MEAT);
        bindFallback(Items.COOKED_MUTTON, COOKED_MEAT);

        bindFallback(Items.COOKED_RABBIT, new CommonFood(10, 2500, 2.0F, 0.375F));
        bindFallback(Items.RABBIT_STEW, new ContainerFood(new CommonFood(20, 2500, 2.0F, 0.375F), Items.BOWL, 0));

        bindFallback(DMItems.DRAGON_MEAT, IDragonFood.EMPTY);
        bindFallback(DMItems.COOKED_DRAGON_MEAT, IDragonFood.EMPTY);
    }

    public static class Storage implements Capability.IStorage<IDragonFood> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IDragonFood> capability, IDragonFood instance, EnumFacing side) {
            return instance instanceof INBTSerializable ? ((INBTSerializable<?>) instance).serializeNBT() : null;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public void readNBT(Capability<IDragonFood> capability, IDragonFood instance, EnumFacing side, NBTBase tag) {
            if (instance instanceof INBTSerializable) {
                ((INBTSerializable) instance).deserializeNBT(tag);
            }
        }
    }
}
