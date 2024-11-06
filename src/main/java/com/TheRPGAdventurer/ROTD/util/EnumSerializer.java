package com.TheRPGAdventurer.ROTD.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;

import javax.annotation.Nonnull;

@MethodsReturnNonnullByDefault
public class EnumSerializer<E extends Enum<E>> implements DataSerializer<E> {
    private final E[] values;
    public final E defaultValue;

    public EnumSerializer(Class<E> type, E defaultValue) {
        this.defaultValue = defaultValue;
        this.values = type.getEnumConstants();
    }

    @Override
    public void write(PacketBuffer buffer, E value) {
        buffer.writeVarInt(value.ordinal());
    }

    @Override
    public E read(PacketBuffer buffer) {
        int index = buffer.readVarInt();
        if (index < 0 || index >= this.values.length) return this.defaultValue;
        return this.values[index];
    }

    @Override
    public DataParameter<E> createKey(int id) {
        return new DataParameter<>(id, this);
    }

    @Override
    public E copyValue(@Nonnull E value) {
        return value;
    }
}
