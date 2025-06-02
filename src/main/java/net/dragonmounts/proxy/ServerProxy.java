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

import net.dragonmounts.DragonMounts;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.event.CommonMisc;
import net.dragonmounts.init.DMArmorEffects;
import net.dragonmounts.network.*;
import net.dragonmounts.registry.CarriageType;
import net.dragonmounts.registry.CooldownCategory;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.Function;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * 2nd @author TheRPGAdventurer
 */
public class ServerProxy {
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
        DragonMounts.NETWORK_WRAPPER.registerMessage(SSyncBannerPacket.Handler.class, SSyncBannerPacket.class, ++discriminator, Side.CLIENT);
        DragonMounts.NETWORK_WRAPPER.registerMessage(SInitCooldownPacket.Handler.class, SInitCooldownPacket.class, ++discriminator, Side.CLIENT);
        DragonMounts.NETWORK_WRAPPER.registerMessage(SSyncCooldownPacket.Handler.class, SSyncCooldownPacket.class, ++discriminator, Side.CLIENT);
        DragonMounts.NETWORK_WRAPPER.registerMessage(SRiposteEffectPacket.Handler.class, SRiposteEffectPacket.class, ++discriminator, Side.CLIENT);
        // C2S:
        DragonMounts.NETWORK_WRAPPER.registerMessage(MessageDragonTargetHandlerServer.class, MessageDragonTarget.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CDragonBreathPacket.Handler.class, CDragonBreathPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CDragonControlPacket.Handler.class, CDragonControlPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CDragonConfigPacket.Handler.class, CDragonConfigPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CTeleportOrderPacket.Handler.class, CTeleportOrderPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CSitOrderPacket.Handler.class, CSitOrderPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CFollowOrderPacket.Handler.class, CFollowOrderPacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(CRenameWhistlePacket.Handler.class, CRenameWhistlePacket.class, ++discriminator, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(COpenInventoryPacket.Handler.class, COpenInventoryPacket.class, ++discriminator, Side.SERVER);
    }

    public void PostInitialization(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(DMArmorEffects.class);
    }

    public Function<String, VariantAppearance> getBuiltinAppearances() {
        return ignored -> null;
    }
}