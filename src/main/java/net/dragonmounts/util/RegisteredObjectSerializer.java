package net.dragonmounts.util;

import net.dragonmounts.registry.DeferredRegistry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

public class RegisteredObjectSerializer<T extends IForgeRegistryEntry<T>> implements DataSerializer<T> {
    public final DeferredRegistry<T> registry;

    public RegisteredObjectSerializer(DeferredRegistry<T> registry) {
        this.registry = registry;
    }

    @Override
    public void write(PacketBuffer buffer, T value) {
        buffer.writeVarInt(this.registry.getID(value));
    }

    @Override
    public T read(PacketBuffer buffer) {
        return this.registry.getValue(buffer.readVarInt());
    }

    @Override
    public DataParameter<T> createKey(int id) {
        return new DataParameter<>(id, this);
    }

    @Override
    public T copyValue(@Nonnull T value) {
        return value;
    }
}