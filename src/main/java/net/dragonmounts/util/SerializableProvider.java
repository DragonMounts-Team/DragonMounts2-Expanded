package net.dragonmounts.util;

import net.dragonmounts.api.IValidatedNBTSerializable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public class SerializableProvider<T extends NBTBase, I extends IValidatedNBTSerializable<T>> implements ICapabilitySerializable<T>, IValidatedNBTSerializable<T> {
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
    public T serializeNBT() {
        return this.instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(T nbt) {
        this.type.readNBT(this.instance, null, nbt);
    }

    @Override
    public void deserializeNothing() {
        this.instance.deserializeNothing();
    }

    @Override
    public @Nullable T validateTag(@Nullable NBTBase tag) {
        return this.instance.validateTag(tag);
    }
}
