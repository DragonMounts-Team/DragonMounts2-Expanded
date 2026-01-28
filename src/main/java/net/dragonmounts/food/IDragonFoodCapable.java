package net.dragonmounts.food;

import net.dragonmounts.capability.IDragonFood;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static net.dragonmounts.capability.DMCapabilities.DRAGON_FOOD;

/// @see <a href="https://dictionary.cambridge.org/dictionary/english/capable">Cambridge Dictionary: Capable</a> -capable suffix
public interface IDragonFoodCapable extends IDragonFood, ICapabilityProvider {
    @Override
    default boolean hasCapability(@Nullable Capability<?> capability, @Nullable EnumFacing facing) {
        return DRAGON_FOOD == capability;
    }

    @Override
    default <T> @Nullable T getCapability(@Nullable Capability<T> capability, @Nullable EnumFacing facing) {
        //noinspection DataFlowIssue
        return DRAGON_FOOD == capability ? DRAGON_FOOD.cast(this) : null;
    }
}
