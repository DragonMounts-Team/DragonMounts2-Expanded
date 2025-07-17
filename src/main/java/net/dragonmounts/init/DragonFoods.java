package net.dragonmounts.init;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.food.*;
import net.dragonmounts.util.LogUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class DragonFoods {
    private static final Reference2ObjectOpenHashMap<Item, ICapabilityProvider> PROVIDERS = new Reference2ObjectOpenHashMap<>();
    public static final BreedingFood RAW_FISH = new BreedingFood(4, 1500, 0.75F, 0.25F);

    public static void bind(Item item, ICapabilityProvider provider) {
        if (PROVIDERS.containsKey(item)) {
            LogUtil.LOGGER.warn("Override provider for item {}", item);
        }
        PROVIDERS.put(item, provider);
    }

    public static ICapabilityProvider getProvider(Item item) {
        return PROVIDERS.get(item);
    }

    static {
        bind(Items.CARROT, new Carrot());
        bind(Items.POISONOUS_POTATO, new PoisonousPotato());

        bind(Items.BEEF, new CommonFood(6, 1500, 1.25F, 0.25F));
        bind(Items.CHICKEN, new CommonFood(4, 1500, 1.0F, 0.25F));
        bind(Items.FISH, RAW_FISH);
        bind(Items.PORKCHOP, new CommonFood(6, 1500, 1.25F, 0.25F));
        bind(Items.RABBIT, new CommonFood(6, 1500, 1.25F, 0.25F));
        bind(Items.MUTTON, new CommonFood(4, 1500, 1.0F, 0.25F));

        bind(Items.COOKED_BEEF, new CommonFood(16, 2500, 2.5F, 0.375F));
        bind(Items.COOKED_CHICKEN, new CommonFood(12, 2500, 2.0F, 0.375F));
        bind(Items.COOKED_FISH, new BreedingFood(10, 2500, 1.5F, 0.375F));
        bind(Items.COOKED_PORKCHOP, new CommonFood(16, 2500, 2.5F, 0.375F));
        CommonFood cookedRabbit = new CommonFood(10, 2500, 2.0F, 0.375F);
        bind(Items.COOKED_RABBIT, cookedRabbit);
        bind(Items.RABBIT_STEW, new WrappedFood(cookedRabbit, Items.BOWL, 0));
        bind(Items.COOKED_MUTTON, new CommonFood(12, 2500, 2.0F, 0.375F));
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
