package net.dragonmounts.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeferredRegistry<V extends IForgeRegistryEntry<V>> implements IForgeRegistry<V> {
    private RegistryBuilder<V> builder;
    protected ForgeRegistry<V> registry;

    public DeferredRegistry(ResourceLocation name, Class<V> clazz, RegistryBuilder<V> builder) {
        this.builder = builder.setName(name).setType(clazz).addCallback(this);
    }

    public void register() {
        if (this.builder == null) return;
        this.registry = (ForgeRegistry<V>) this.builder.create();
        this.builder = null;
    }

    @Override
    public Class<V> getRegistrySuperType() {
        return this.registry.getRegistrySuperType();
    }

    @Override
    public void register(V value) {
        this.registry.register(value);
    }

    @SafeVarargs
    @Override
    public final void registerAll(V... values) {
        for (V value : values) {
            this.register(value);
        }
    }

    @Override
    public boolean containsKey(ResourceLocation key) {
        return this.registry.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return this.registry.containsValue(value);
    }

    @Override
    public @Nullable V getValue(ResourceLocation key) {
        return this.registry.getValue(key);
    }

    public V getOrDefault(ResourceLocation key, V fallback) {
        V value = this.registry.getValue(key);
        return value == null ? fallback : value;
    }

    public @Nullable V getIfPresent(ResourceLocation key) {
        return this.registry.containsKey(key) ? this.registry.getValue(key) : null;
    }

    @Override
    public @Nullable ResourceLocation getKey(V value) {
        return this.registry.getKey(value);
    }

    @Override
    public Set<ResourceLocation> getKeys() {
        return this.registry.getKeys();
    }

    @Deprecated
    @Override
    public List<V> getValues() {
        return this.registry.getValues();
    }

    @Override
    public Set<Map.Entry<ResourceLocation, V>> getEntries() {
        return this.registry.getEntries();
    }

    @Override
    public <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type) {
        return this.registry.getSlaveMap(slaveMapName, type);
    }

    @Override
    public Iterator<V> iterator() {
        return this.registry.iterator();
    }

    public int getID(V value) {
        return this.registry.getID(value);
    }

    public int getID(ResourceLocation name) {
        return this.registry.getID(name);
    }

    public V getValue(int id) {
        return this.registry.getValue(id);
    }
}
