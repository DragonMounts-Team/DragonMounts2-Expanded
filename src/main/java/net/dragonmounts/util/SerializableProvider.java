package net.dragonmounts.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public class SerializableProvider<I, T extends NBTBase> implements ICapabilitySerializable<T> {
    public final Capability<I> type;
    public final I instance;

    public SerializableProvider(Capability<I> type, I instance) {
        this.type = type;
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nullable Capability<?> capability, @Nullable EnumFacing facing) {
        return this.type == capability;
    }

    @Override
    public <E> @Nullable E getCapability(@Nullable Capability<E> capability, @Nullable EnumFacing facing) {
        return this.type == capability ? this.type.cast(this.instance) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T serializeNBT() {
        return (T) this.type.getStorage().writeNBT(this.type, this.instance, null);
    }

    @Override
    public void deserializeNBT(T nbt) {
        this.type.getStorage().readNBT(this.type, this.instance, null, nbt);
    }
}
