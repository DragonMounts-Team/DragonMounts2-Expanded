package net.dragonmounts.registry;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.block.DragonHeadBlock;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.util.LogUtil;
import net.dragonmounts.util.RegisteredObjectSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import static it.unimi.dsi.fastutil.Arrays.MAX_ARRAY_SIZE;
import static net.dragonmounts.DragonMounts.makeId;

public class DragonVariant extends IForgeRegistryEntry.Impl<DragonVariant> {
    public static final String DATA_PARAMETER_KEY = "Variant";
    public static final ResourceLocation DEFAULT_KEY = makeId("ender_female");
    public static final Registry REGISTRY = new Registry(makeId("dragon_variant"), new RegistryBuilder<DragonVariant>().setDefaultKey(DEFAULT_KEY));
    public static final RegisteredObjectSerializer<DragonVariant> SERIALIZER = new RegisteredObjectSerializer<>(REGISTRY);

    public static DragonVariant byName(String name) {
        return REGISTRY.getValue(new ResourceLocation(name));
    }

    int index = -1;// non-private to simplify nested class access
    public final DragonType type;
    public final VariantAppearance appearance;
    public final DragonHeadBlock.Holder head;

    public DragonVariant(
            DragonType type,
            ResourceLocation identifier,
            VariantAppearance appearance,
            Function<DragonVariant, DragonHeadBlock.Holder> factory
    ) {
        this.type = type;
        this.appearance = appearance;
        this.setRegistryName(identifier);
        this.head = factory.apply(this);
    }

    public final String getSerializedName() {
        ResourceLocation key = this.getRegistryName();
        return (key == null ? DEFAULT_KEY : key).toString();
    }

    /**
     * Simplified {@link it.unimi.dsi.fastutil.objects.ReferenceArrayList}
     */
    public static final class Manager implements Iterable<DragonVariant> {
        public static final int DEFAULT_INITIAL_CAPACITY = 8;
        public final DragonType type;
        DragonVariant[] variants = {};// non-private to simplify nested class access
        int size;// non-private to simplify nested class access

        public Manager(DragonType type) {
            this.type = type;
        }

        private void grow(int capacity) {
            if (capacity <= this.variants.length) return;
            if (this.variants.length > 0) {
                capacity = (int) Math.max(Math.min((long) this.variants.length + (this.variants.length >> 1), MAX_ARRAY_SIZE), capacity);
            } else if (capacity < DEFAULT_INITIAL_CAPACITY) {
                capacity = DEFAULT_INITIAL_CAPACITY;
            }
            final DragonVariant[] array = new DragonVariant[capacity];
            System.arraycopy(this.variants, 0, array, 0, size);
            this.variants = array;
            assert this.size <= this.variants.length;
        }

        @SuppressWarnings("UnusedReturnValue")
        boolean add(final DragonVariant variant) {// non-private to simplify nested class access
            if (variant.type != this.type || variant.index >= 0) return false;
            this.grow(this.size + 1);
            variant.index = this.size;
            this.variants[this.size++] = variant;
            assert this.size <= this.variants.length;
            return true;
        }

        @SuppressWarnings("UnusedReturnValue")
        boolean remove(final DragonVariant variant) {// non-private to simplify nested class access
            if (variant.type != this.type || variant.index < 0) return false;
            if (variant.index >= this.size) {
                throw new IndexOutOfBoundsException("Index (" + variant.index + ") is greater than or equal to list size (" + this.size + ")");
            }
            this.size--;
            if (variant.index != this.size) {
                System.arraycopy(this.variants, variant.index + 1, this.variants, variant.index, this.size - variant.index);
            }
            variant.index = -1;
            this.variants[this.size] = null;
            assert this.size <= this.variants.length;
            return true;
        }

        void clear() {// non-private to simplify nested class access
            for (int i = 0; i < this.size; ++i) {
                this.variants[i].index = -1;
                this.variants[i] = null;
            }
            this.size = 0;
        }

        public DragonVariant draw(Random random, @Nullable DragonVariant current) {
            switch (this.size) {
                case 0:
                    return current;
                case 1:
                    return this.variants[0];
            }
            if (current == null || current.type != this.type) {
                return this.variants[random.nextInt(this.size)];
            }
            if (this.size == 2) {
                return this.variants[(current.index ^ 1) & 1];//current.index == 0 ? 1 : 0
            }
            int index = random.nextInt(this.size - 1);
            return this.variants[index < current.index ? index : index + 1];
        }

        public int size() {
            return this.size;
        }

        @Nonnull
        @Override
        public Iterator<DragonVariant> iterator() {
            return new IteratorImpl();
        }

        @Override
        public void forEach(Consumer<? super DragonVariant> action) {
            for (int i = 0; i < this.size; ++i) {
                action.accept(this.variants[i]);
            }
        }

        public class IteratorImpl implements Iterator<DragonVariant> {
            int i = 0;

            @Override
            public boolean hasNext() {
                return this.i < Manager.this.size;
            }

            @Override
            public DragonVariant next() {
                return Manager.this.variants[i++];
            }
        }
    }

    public static class Registry extends DeferredRegistry<DragonVariant> implements IForgeRegistry.AddCallback<DragonVariant>, IForgeRegistry.ClearCallback<DragonVariant> {
        public Registry(ResourceLocation identifier, RegistryBuilder<DragonVariant> builder) {
            super(identifier, DragonVariant.class, builder);
        }

        @Override
        public void onAdd(IForgeRegistryInternal<DragonVariant> owner, RegistryManager stage, int id, DragonVariant obj, @Nullable DragonVariant oldObj) {
            if (oldObj != null) {
                oldObj.type.variants.remove(oldObj);
            }
            obj.type.variants.add(obj);
        }

        @Override
        public void onClear(IForgeRegistryInternal<DragonVariant> owner, RegistryManager stage) {
            if (DragonMountsConfig.isDebug()) {
                LogUtil.LOGGER.info("Clearing Registry!", new Throwable("Clearing Registry!"));
            }
            ReferenceOpenHashSet<DragonType> cleared = new ReferenceOpenHashSet<>();
            for (DragonVariant variant : owner) {
                if (cleared.contains(variant.type)) continue;
                variant.type.variants.clear();
                cleared.add(variant.type);
            }
        }
    }
}
