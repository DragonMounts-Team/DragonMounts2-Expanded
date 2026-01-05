package net.dragonmounts.api;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public interface IValidatedNBTSerializable<T extends NBTBase> extends INBTSerializable<T> {
    @Nullable
    T validateTag(@Nullable NBTBase tag);

    default void deserializeNothing() {
        throw new UnsupportedOperationException("Failed to validate NBT");
    }

    static <T extends NBTBase> void deserializeValidatedTag(IValidatedNBTSerializable<T> object, @Nullable NBTBase tag) {
        T validated = object.validateTag(tag);
        if (validated == null) {
            object.deserializeNothing();
        } else {
            object.deserializeNBT(validated);
        }
    }
}
