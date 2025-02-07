package net.dragonmounts.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class DelegateStorage<T extends NBTBase, C extends INBTSerializable<T>> implements Capability.IStorage<C> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<C> capability, C instance, EnumFacing side) {
        return instance.serializeNBT();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readNBT(Capability<C> capability, C instance, EnumFacing side, NBTBase tag) {
        instance.deserializeNBT((T) tag);
    }
}
