/*
 ** 2012 August 27
 **
 ** The author disclaims copyright to this source code.  In place of
 ** t.AQUA legal notice, here is t.AQUA blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.proxy;

import it.unimi.dsi.fastutil.Function;
import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.client.gui.GuiDragonDebug;
import net.dragonmounts.client.other.TargetHighlighter;
import net.dragonmounts.client.render.CarriageRenderer;
import net.dragonmounts.client.render.DMCapeRenderer;
import net.dragonmounts.client.render.DragonCoreBlockEntityRenderer;
import net.dragonmounts.client.render.dragon.DragonRenderer;
import net.dragonmounts.client.render.dragon.breathweaponFX.ClientBreathNodeRenderer;
import net.dragonmounts.client.userinput.DragonOrbControl;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.client.variant.VariantAppearances;
import net.dragonmounts.entity.CarriageEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.effects.ClientBreathNodeEntity;
import net.dragonmounts.event.DragonViewEvent;
import net.dragonmounts.event.IItemColorRegistration;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.init.DMKeyBindings;
import net.dragonmounts.util.debugging.StartupDebugClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.stream.Collectors;


/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * 2nd @author TheRPGAdventurer
 */
public class ClientProxy extends ServerProxy {
    private static void addCredit(StringBuilder builder, String credit, String description) {
        builder.append(TextFormatting.GREEN)
                .append(credit)
                .append(TextFormatting.RESET)
                .append('-')
                .append(TextFormatting.AQUA)
                .append(description)
                .append(TextFormatting.RESET)
                .append('\n');
    }

    private int thirdPersonViewDragon = 0;

    @Override
    public void PreInitialization(FMLPreInitializationEvent event) {
        super.PreInitialization(event);
        MinecraftForge.EVENT_BUS.register(IItemColorRegistration.class);
        // register dragon entity renderer
        DragonMountsConfig.clientPreInit();
        RenderingRegistry.registerEntityRenderingHandler(TameableDragonEntity.class, DragonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ClientBreathNodeEntity.class, ClientBreathNodeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CarriageEntity.class, CarriageRenderer::new);

        ClientRegistry.bindTileEntitySpecialRenderer(DragonCoreBlockEntity.class, new DragonCoreBlockEntityRenderer());
        DMItems.DRAGON_CORE.setTileEntityItemStackRenderer(new DragonCoreBlockEntityRenderer.ItemStackRenderer());

        //Override mcmod.info - This looks cooler :)
        ModMetadata metadata = event.getModMetadata();
        metadata.name = TextFormatting.DARK_AQUA.toString() + TextFormatting.BOLD + metadata.name;
        StringBuilder credits = new StringBuilder(2048).append('\n');
        addCredit(credits, "BarracudaATA", "The Original Owner of Dragon Mounts.");
        addCredit(credits, "TheRPGAdventurer", "Former author of Dragon Mounts 2.");
        addCredit(credits, "Kingdomall", "First Developer for DM2. Overhauling many textures.");
        addCredit(credits, "Shannieann", "Second Developer for DM2. Zombie and Terra textures. Texture artist.");
        addCredit(credits, "UkanGundun", "Fire and Sunlight textures. Texture artist.");
        addCredit(credits, "WolfShotz (Kay9Unit)", "Coder and codebase improvements");
        addCredit(credits, "FlaemWing", "New nest textures, tool textures and dragon armor item textures.");
        addCredit(credits, "AlexThe666", "Open sourcing the code for I&F, Ice and Fire Owner.");
        addCredit(credits, "cesar_zorak", "Dragon armor model textures, breed textures and more");
        addCredit(credits, "NightScale", "For contributing new ideas");
        addCredit(credits, "Unakitononeko", "For the new and improved forest textures");
        addCredit(credits, "TheGreyGhost", "Old DM1 Developer. Prototype Dragon Breath.");
        addCredit(credits, "Tomanex", "Dragon Mounts Team Founder and Texture Artist. Revamped various textures");
        addCredit(credits, "JDSK0ala", "Dragon Mounts Team Founder and Beta Tester");
        addCredit(credits, "2190303755 (Number-Man)", "Lead Developer and Coder for Dragon Mounts 2: Continued");
        addCredit(credits, "Moaswies", "Coder for Dragon Mounts 2: Continued");
        addCredit(credits, "EnderEXE", "Beta Tester");
        addCredit(credits, "Tomohiko", "For contributing Japanese localization");
        addCredit(credits, "Signis Kerman", "For contributing French localization");
        metadata.credits = credits.toString();
        metadata.authorList = metadata.authorList.stream().map(author ->
                TextFormatting.GOLD.toString() + TextFormatting.BOLD + author + TextFormatting.RESET
        ).collect(Collectors.toList());
        metadata.description +=
                "\nTips:\n" +
                        "1. Don't forget to right click the egg to start the hatching process\n" +
                        "2. Also water dragon needs to be struck by lightning to become t.AQUA storm dragon\n" +
                        "3. You can't hatch eggs in the End Dimension\n" +
                        "4. You can press " + TextFormatting.ITALIC + "ctrl" + TextFormatting.RESET + " to enable boost flight\n" +
                        "5. Dragons need to be of opposite genders to breed\n" +
                        "6. Shift + right click a right clicked dragon egg to make it a block again";
    }

    @Override
    public void Initialization(FMLInitializationEvent evt) {
        super.Initialization(evt);
        if (DragonMountsConfig.isDebug()) {
            MinecraftForge.EVENT_BUS.register(new GuiDragonDebug());
        }
        DMKeyBindings.init();
        StartupDebugClientOnly.initClientOnly();
    }

    @Override
    public void PostInitialization(FMLPostInitializationEvent event) {
        super.PostInitialization(event);

        if (DragonMountsConfig.isDebug()) {
            MinecraftForge.EVENT_BUS.register(new GuiDragonDebug());
        }
        StartupDebugClientOnly.postInitClientOnly();

        if (DragonMountsConfig.isPrototypeBreathweapons()) {
            DragonOrbControl.createSingleton(getNetwork());
            DragonOrbControl.initialiseInterceptors();
            MinecraftForge.EVENT_BUS.register(DragonOrbControl.getInstance());
            MinecraftForge.EVENT_BUS.register(new TargetHighlighter());
        }

        //MinecraftForge.EVENT_BUS.register(new ModKeys());
        MinecraftForge.EVENT_BUS.register(new DragonViewEvent());
        MinecraftForge.EVENT_BUS.register(new DMCapeRenderer());
    }

    public int getDragon3rdPersonView() {
        return thirdPersonViewDragon;
    }

    public void setDragon3rdPersonView(int view) {
        thirdPersonViewDragon = view;
    }

    @Override
    public File getDataDirectory() {
        return Minecraft.getMinecraft().gameDir;
    }

    @Override
    public Function<String, VariantAppearance> getVariantAppearances() {
        return VariantAppearances.getMap();
    }
}
