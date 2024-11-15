/*
 ** 2012 August 27
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD.proxy;

import com.TheRPGAdventurer.ROTD.DragonMounts;
import com.TheRPGAdventurer.ROTD.DragonMountsConfig;
import com.TheRPGAdventurer.ROTD.DragonMountsTags;
import com.TheRPGAdventurer.ROTD.capability.ArmorEffectManager;
import com.TheRPGAdventurer.ROTD.cmd.DragonCommandTree;
import com.TheRPGAdventurer.ROTD.event.VanillaEggHandler;
import com.TheRPGAdventurer.ROTD.inits.DMArmorEffects;
import com.TheRPGAdventurer.ROTD.inits.DMCapabilities;
import com.TheRPGAdventurer.ROTD.network.*;
import com.TheRPGAdventurer.ROTD.objects.entity.entitycarriage.EntityCarriage;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.items.entity.EntityDragonAmulet;
import com.TheRPGAdventurer.ROTD.registry.CooldownCategory;
import com.TheRPGAdventurer.ROTD.util.debugging.StartupDebugCommon;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.io.File;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * 2nd @author TheRPGAdventurer
 */
public class ServerProxy {

    public final byte DCM_DISCRIMINATOR_ID = 35;  // arbitrary non-zero ID (non-zero makes troubleshooting easier)
    public final byte DOT_DISCRIMINATOR_ID = 73;  // arbitrary non-zero ID (non-zero makes troubleshooting easier)
    private final int ENTITY_TRACKING_RANGE = 80;
    private final int ENTITY_UPDATE_FREQ = 3;
    private final int ENTITY_ID = 1;
    private final boolean ENTITY_SEND_VELO_UPDATES = true;
    private static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("DragonControls");

    public SimpleNetworkWrapper getNetwork() {
        return this.network;
    }

    public void PreInitialization(FMLPreInitializationEvent event) {
        DragonMountsConfig.PreInit();
        StartupDebugCommon.preInitCommon();
        CooldownCategory.REGISTRY.register();
    }

    @SuppressWarnings("deprecation")
    public void Initialization(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new VanillaEggHandler());
        network.registerMessage(MessageDragonTargetHandlerServer.class, MessageDragonTarget.class, DOT_DISCRIMINATOR_ID, Side.SERVER);

        // I wont touch the network the old devs made, seems redundant yeah I know - rpg
        DragonMounts.NETWORK_WRAPPER.registerMessage(MessageDragonBreath.MessageDragonBreathHandler.class, MessageDragonBreath.class, 0, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(MessageDragonExtras.MessageDragonExtrasHandler.class, MessageDragonExtras.class, 1, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(MessageDragonGui.MessageDragonGuiHandler.class, MessageDragonGui.class, 3, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(SSyncBannerPacket.Handler.class, SSyncBannerPacket.class, 4, Side.CLIENT);
        DragonMounts.NETWORK_WRAPPER.registerMessage(MessageDragonSit.MessageDragonSitHandler.class, MessageDragonSit.class, 5, Side.SERVER);
        DragonMounts.NETWORK_WRAPPER.registerMessage(MessageDragonTeleport.MessageDragonTeleportHandler.class, MessageDragonTeleport.class, 6, Side.SERVER);

        DragonMounts.NETWORK_WRAPPER.registerMessage(SInitCooldownPacket.Handler.class, SInitCooldownPacket.class, 7, Side.CLIENT);
        DragonMounts.NETWORK_WRAPPER.registerMessage(SSyncCooldownPacket.Handler.class, SSyncCooldownPacket.class, 8, Side.CLIENT);
        DragonMounts.NETWORK_WRAPPER.registerMessage(SRiposteEffectPacket.Handler.class, SRiposteEffectPacket.class, 9, Side.CLIENT);

        StartupDebugCommon.initCommon();
        MinecraftForge.EVENT_BUS.register(DMCapabilities.class);
    }

    public void PostInitialization(FMLPostInitializationEvent event) {
        registerEntities();
        if (DragonMountsConfig.isDebug()) {
            StartupDebugCommon.postInitCommon();
        }
        DMCapabilities.register();
        MinecraftForge.EVENT_BUS.register(DMArmorEffects.class);
        MinecraftForge.EVENT_BUS.register(ArmorEffectManager.Events.class);
    }

    public void ServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new DragonCommandTree());
    }

    public void ServerStopped(FMLServerStoppedEvent evt) {
    }

    private void registerEntities() {
        EntityRegistry.registerModEntity(new ResourceLocation(DragonMountsTags.MOD_ID, "dragon"), EntityTameableDragon.class, "DragonMount",
                ENTITY_ID, DragonMounts.instance, ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQ,
                ENTITY_SEND_VELO_UPDATES);
        EntityRegistry.registerModEntity(new ResourceLocation(DragonMountsTags.MOD_ID, "carriage"), EntityCarriage.class, "DragonCarriage",
                2, DragonMounts.instance, 32, ENTITY_UPDATE_FREQ,
                ENTITY_SEND_VELO_UPDATES);
        EntityRegistry.registerModEntity(new ResourceLocation(DragonMountsTags.MOD_ID, "indestructible"), EntityDragonAmulet.class, "Indestructible Item",
                3, DragonMounts.instance, 64, 20, true);
    }

    public void render() {
    }

    public int getDragon3rdPersonView() {
        return 0;
    }

    public void setDragon3rdPersonView(int view) {
    }

    public void registerItemRenderer(Item item, int meta, String id) {
    }

    // get the directory on disk used for storing the game files
    // is different for dedicated server vs client
    public File getDataDirectory() {
        return FMLServerHandler.instance().getSavesDirectory();
    }


}