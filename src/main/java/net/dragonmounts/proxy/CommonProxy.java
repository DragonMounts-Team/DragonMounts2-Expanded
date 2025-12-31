/*
 ** 2012 August 27
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.proxy;

import com.google.common.base.Functions;
import net.dragonmounts.DragonMounts;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.event.CommonMisc;
import net.dragonmounts.init.DMArmorEffects;
import net.dragonmounts.network.*;
import net.dragonmounts.registry.CarriageType;
import net.dragonmounts.registry.CooldownCategory;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.Function;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * 2nd @author TheRPGAdventurer
 */
public class CommonProxy {
    public void PreInitialization(FMLPreInitializationEvent event) {
        CarriageType.REGISTRY.register();
        CooldownCategory.REGISTRY.register();
        DragonType.REGISTRY.register();
        DragonVariant.REGISTRY.register();
    }

    public void Initialization(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(CommonMisc.class);
        int discriminator = 0;
        // S2C:
        DragonMounts.NETWORK_WRAPPER.registerMessage(SSyncBannerPacket::handle, SSyncBannerPacket.class, ++discriminator, Side.CLIENT);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CommonProxy::scheduleClientTask, SInitCooldownPacket.class, ++discriminator, Side.CLIENT);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CommonProxy::scheduleClientTask, SSyncCooldownPacket.class, ++discriminator, Side.CLIENT);
        DragonMounts.NETWORK_WRAPPER.registerMessage(SRiposteEffectPacket::handle, SRiposteEffectPacket.class, ++discriminator, Side.CLIENT);
        // C2S:
        DragonMounts.NETWORK_WRAPPER.registerMessage(CDragonBreathPacket::handle, CDragonBreathPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CDragonControlPacket::handle, CDragonControlPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CDragonConfigPacket::handle, CDragonConfigPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CTeleportOrderPacket::handle, CTeleportOrderPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CSitOrderPacket::handle, CSitOrderPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CFollowOrderPacket::handle, CFollowOrderPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CRenameFlutePacket::handle, CRenameFlutePacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(COpenInventoryPacket::handle, COpenInventoryPacket.class, ++discriminator, Side.SERVER);
    }

    public void PostInitialization(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(DMArmorEffects.class);
    }

    public Function<? super String, VariantAppearance> getBuiltinAppearances() {
        return Functions.constant(null);
    }

    public static <T extends IMessage & Runnable> IMessage scheduleClientTask(T packet, MessageContext context) {
        Minecraft.getMinecraft().addScheduledTask(packet);
        return null;
    }
}