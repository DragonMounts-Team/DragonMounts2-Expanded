package net.dragonmounts.registry;

import net.dragonmounts.util.DMUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.*;

import javax.annotation.Nullable;

public class NumericIdentifiedEntry<T extends NumericIdentifiedEntry<T>> extends IForgeRegistryEntry.Impl<T> {
    int id = -1;// non-private to simplify nested class access

    public final int getId() {
        return this.id;
    }

    public static class Registry<T extends NumericIdentifiedEntry<T>> extends DeferredRegistry<T> implements IForgeRegistry.AddCallback<T>, IForgeRegistry.ClearCallback<T> {
        public Registry(ResourceLocation name, Class<T> clazz, RegistryBuilder<T> builder) {
            super(name, clazz, builder);
        }

        @Override
        public void onAdd(IForgeRegistryInternal<T> owner, RegistryManager stage, int id, T obj, @Nullable T oldObj) {
            if (oldObj != null) {
                oldObj.id = -1;
            }
            obj.id = id;
        }

        @Override
        public void onClear(IForgeRegistryInternal<T> owner, RegistryManager stage) {
            DMUtils.getLogger().info("Clearing Registry!", new Throwable());
            for (T entry : owner) {
                entry.id = -1;
            }
        }
    }
}
