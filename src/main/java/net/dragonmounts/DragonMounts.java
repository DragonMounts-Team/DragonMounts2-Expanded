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
import net.dragonmounts.compat.DragonMountsCompat;
import net.dragonmounts.event.RegistryEventHandler;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.proxy.ServerProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;


/**
 * Main control class for Forge.
 */
@Mod(modid = DragonMountsTags.MOD_ID, name = DragonMountsTags.MOD_NAME, version = DragonMountsTags.VERSION, useMetadata = true, guiFactory = DragonMounts.GUI_FACTORY)
public class DragonMounts {
    public static ResourceLocation makeId(String name) {
        return new ResourceLocation(DragonMountsTags.MOD_ID, name);
    }

    public static SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(DragonMountsTags.MOD_ID);
    /**
     * the canonical name of {@link DragonMountsConfigGuiFactory}
     */
    public static final String GUI_FACTORY = "net.dragonmounts.DragonMountsConfigGuiFactory";

    @SidedProxy(serverSide = "net.dragonmounts.proxy.ServerProxy", clientSide = "net.dragonmounts.proxy.ClientProxy")
    public static ServerProxy PROXY;

    public static DragonMounts getInstance() {
        return INSTANCE;
    }

    // important for debug in config
    public static ModMetadata getMetadata() {
        return METADATA;
    }

    @EventHandler
    public void PreInitialization(FMLPreInitializationEvent event) {
        PROXY.PreInitialization(event);
        DMItemGroups.init();
        DragonMountsCompat.load(FMLCommonHandler.instance().getDataFixer().init(DragonMountsTags.MOD_ID, DragonMountsCompat.VERSION));
    }

    @EventHandler
    public void Initialization(FMLInitializationEvent event) {
        PROXY.Initialization(event);
        DMItems.bindRepairMaterials();
        GameRegistry.registerWorldGenerator(new DragonMountsWorldGenerator(), 0);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        RegistryEventHandler.registerCapabilities();
        // Mod Compat Initialization
        if (Loader.isModLoaded("baubles")) BaublesCompat.load();
    }

    @EventHandler
    public void PostInitialization(FMLPostInitializationEvent event) {
        PROXY.PostInitialization(event);
    }

    @EventHandler
    public void ServerStarting(FMLServerStartingEvent event) {
        PROXY.ServerStarting(event);
    }

    @Instance(value = DragonMountsTags.MOD_ID)
    private static DragonMounts INSTANCE;

    @Metadata(value = DragonMountsTags.MOD_ID)
    private static ModMetadata METADATA;
}
