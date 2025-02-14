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
import net.dragonmounts.config.DMConfig;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
        modid = DragonMountsTags.MOD_ID,
        name = DragonMountsTags.MOD_NAME,
        version = DragonMountsTags.VERSION,
        useMetadata = true,
        /// the canonical name of {@link net.dragonmounts.client.gui.ConfigGui}
        guiFactory = "net.dragonmounts.client.gui.ConfigGui"
)
public class DragonMounts {
    public static ResourceLocation makeId(String name) {
        return new ResourceLocation(DragonMountsTags.MOD_ID, name);
    }
    @SidedProxy(serverSide = "net.dragonmounts.proxy.ServerProxy", clientSide = "net.dragonmounts.proxy.ClientProxy")
    public static ServerProxy PROXY;
    public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(DragonMountsTags.MOD_ID);

    public static DragonMounts getInstance() {
        return INSTANCE;
    }

    public static ModMetadata getMetadata() {
        return METADATA;
    }

    @EventHandler
    public void PreInitialization(FMLPreInitializationEvent event) {
        DMConfig.load();
        PROXY.PreInitialization(event);
        DMItemGroups.init();
        DragonMountsCompat.load(FMLCommonHandler.instance().getDataFixer().init(DragonMountsTags.MOD_ID, DragonMountsCompat.VERSION));
    }

    @EventHandler
    public void Initialization(FMLInitializationEvent event) {
        PROXY.Initialization(event);
        DMItems.bindRepairMaterials();
        GameRegistry.registerWorldGenerator(new DragonMountsWorldGenerator(), 0);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler.INSTANCE);
        RegistryEventHandler.registerCapabilities();
        // Mod Compat Initialization
        if (Loader.isModLoaded("baubles")) BaublesCompat.load();
    }

    @EventHandler
    public void PostInitialization(FMLPostInitializationEvent event) {
        PROXY.PostInitialization(event);
    }

    @Instance(value = DragonMountsTags.MOD_ID)
    private static DragonMounts INSTANCE;

    @Metadata(value = DragonMountsTags.MOD_ID)
    private static ModMetadata METADATA;
}
