/*
 ** 2012 August 13
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts;

import net.dragonmounts.client.gui.GuiHandler;
import net.dragonmounts.compat.BaublesCompat;
import net.dragonmounts.event.EventLiving;
import net.dragonmounts.event.RegistryEventHandler;
import net.dragonmounts.inits.DMArmors;
import net.dragonmounts.inits.DMItemGroups;
import net.dragonmounts.inits.ModTools;
import net.dragonmounts.proxy.ServerProxy;
import net.dragonmounts.util.debugging.testclasses.LoggerLimit;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

/**
 * Main control class for Forge.
 */
@Mod(modid = DragonMountsTags.MOD_ID, name = DragonMountsTags.MOD_NAME, version = DragonMountsTags.VERSION, useMetadata = true, guiFactory = DragonMounts.GUI_FACTORY)
public class DragonMounts {

    public static SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(DragonMountsTags.MOD_ID);
    /**
     * the canonical name of {@link DragonMountsConfigGuiFactory}
     */
    public static final String GUI_FACTORY = "net.dragonmounts.DragonMountsConfigGuiFactory";

    @SidedProxy(serverSide="net.dragonmounts.proxy.ServerProxy", clientSide="net.dragonmounts.proxy.ClientProxy")
    public static ServerProxy proxy;

    @Instance(value = DragonMountsTags.MOD_ID)
    public static DragonMounts instance;

    public static ResourceLocation makeId(String name) {
        return new ResourceLocation(DragonMountsTags.MOD_ID, name.toLowerCase(Locale.ROOT));
    }

    private ModMetadata metadata;
    private DragonMountsConfig config;
    @Deprecated
    public static CreativeTabs mainTab = DMItemGroups.MAIN;
    @Deprecated
    public static CreativeTabs armoryTab = DMItemGroups.COMBAT;

    public DragonMountsConfig getConfig() {
        return config;
    }

    // important for debug in config
    public ModMetadata getMetadata() {
        return metadata;
    }

  // Add this field to your main class
  public static final Logger logger = LogManager.getLogger(DragonMountsTags.MOD_ID);
    public static final LoggerLimit loggerLimit = new LoggerLimit(logger);

    @EventHandler
    public void PreInitialization(FMLPreInitializationEvent event) {
        DragonMountsLootTables.registerLootTables();
        MinecraftForge.EVENT_BUS.register(new EventLiving());
        proxy.PreInitialization(event);
        metadata=event.getModMetadata();
        DMItemGroups.init();
    }

    @EventHandler
    public void Initialization(FMLInitializationEvent event) {
        proxy.Initialization(event);
        proxy.render();
        ModTools.InitializaRepairs();
        DMArmors.InitializaRepairs();
        GameRegistry.registerWorldGenerator(new DragonMountsWorldGenerator(), 0);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        RegistryEventHandler.initRegistries();
        // Mod Compat Initialization
        if (Loader.isModLoaded("baubles")) BaublesCompat.load();
    }

    @EventHandler
    public void PostInitialization(FMLPostInitializationEvent event) {
        proxy.PostInitialization(event);
    }

    @EventHandler
    public void ServerStarting(FMLServerStartingEvent event) {
        proxy.ServerStarting(event);
    }

    @EventHandler
    public void ServerStopped(FMLServerStoppedEvent event) {
        proxy.ServerStopped(event);
    }
}
