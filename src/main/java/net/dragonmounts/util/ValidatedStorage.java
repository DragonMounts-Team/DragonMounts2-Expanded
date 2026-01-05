package net.dragonmounts.util;

import net.dragonmounts.api.IValidatedNBTSerializable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

import static net.dragonmounts.api.IValidatedNBTSerializable.deserializeValidatedTag;

public class ValidatedStorage<C> implements Capability.IStorage<C> {
    @Override
    public @Nullable NBTBase writeNBT(Capability<C> capability, C instance, EnumFacing side) {
        return instance instanceof INBTSerializable ? ((INBTSerializable<?>) instance).serializeNBT() : null;
    }

    @Override
    public void readNBT(Capability<C> capability, C instance, EnumFacing side, @Nullable NBTBase tag) {
        if (instance instanceof IValidatedNBTSerializable) {
            deserializeValidatedTag((IValidatedNBTSerializable<?>) instance, tag);
        }
    }
}