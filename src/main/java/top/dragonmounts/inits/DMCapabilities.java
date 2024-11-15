package top.dragonmounts.inits;

import top.dragonmounts.capability.ArmorEffectManager;
import top.dragonmounts.capability.IArmorEffectManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static top.dragonmounts.DragonMounts.makeId;

public class DMCapabilities {
    public static final ResourceLocation ARMOR_EFFECT_MANAGER_ID = makeId("armor_effect_manager");

    @CapabilityInject(IArmorEffectManager.class)
    public static final Capability<IArmorEffectManager> ARMOR_EFFECT_MANAGER = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IArmorEffectManager.class, new ArmorEffectManager.Storage(), () -> null);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof EntityPlayer) {
            event.addCapability(ARMOR_EFFECT_MANAGER_ID, new ArmorEffectManager.LazyProvider((EntityPlayer) entity));
        }
    }
}
