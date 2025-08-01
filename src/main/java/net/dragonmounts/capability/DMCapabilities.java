package net.dragonmounts.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class DMCapabilities {
    @CapabilityInject(IArmorEffectManager.class)
    public static final Capability<IArmorEffectManager> ARMOR_EFFECT_MANAGER = null;

    @CapabilityInject(IDragonFood.class)
    public static final Capability<IDragonFood> DRAGON_FOOD = null;

    @CapabilityInject(IHardShears.class)
    public static final Capability<IHardShears> HARD_SHEARS = null;

    @CapabilityInject(IFluteHolder.class)
    public static final Capability<IFluteHolder> FLUTE_HOLDER = null;

    @SuppressWarnings("DataFlowIssue")
    public static boolean hasCapability(ICapabilityProvider provider, @Nullable Capability<?> capability) {
        return provider.hasCapability(capability, null);
    }
}
