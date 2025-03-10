package net.dragonmounts.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class DMCapabilities {
    @CapabilityInject(IArmorEffectManager.class)
    public static final Capability<IArmorEffectManager> ARMOR_EFFECT_MANAGER = null;

    @CapabilityInject(IDragonFood.class)
    public static final Capability<IDragonFood> DRAGON_FOOD = null;

    @CapabilityInject(IHardShears.class)
    public static final Capability<IHardShears> HARD_SHEARS = null;

    @CapabilityInject(IWhistleHolder.class)
    public static final Capability<IWhistleHolder> WHISTLE_HOLDER = null;
}
