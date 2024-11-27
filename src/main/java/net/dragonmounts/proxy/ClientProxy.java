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

import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.client.gui.GuiDragonDebug;
import net.dragonmounts.client.other.TargetHighlighter;
import net.dragonmounts.client.render.DragonCoreBlockEntityRenderer;
import net.dragonmounts.client.render.RenderDM2Cape;
import net.dragonmounts.client.render.dragon.DragonRenderer;
import net.dragonmounts.client.render.dragon.breathweaponFX.ClientBreathNodeRenderer;
import net.dragonmounts.client.userinput.DragonOrbControl;
import net.dragonmounts.event.DragonViewEvent;
import net.dragonmounts.event.IItemColorRegistration;
import net.dragonmounts.inits.ModBlocks;
import net.dragonmounts.inits.ModKeys;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.effects.ClientBreathNodeEntity;
import net.dragonmounts.objects.tileentities.TileEntityDragonShulker;
import net.dragonmounts.util.debugging.StartupDebugClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.stream.Collectors;


/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * 2nd @author TheRPGAdventurer
 */
public class ClientProxy extends ServerProxy {
    private static StringBuilder addCredit(StringBuilder builder, String credit, String description) {
        return builder
                .append(TextFormatting.GREEN)
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
        RenderingRegistry.registerEntityRenderingHandler(EntityTameableDragon.class, DragonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ClientBreathNodeEntity.class, ClientBreathNodeRenderer::new);

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDragonShulker.class, new DragonCoreBlockEntityRenderer());
        ModBlocks.DRAGONSHULKER.item.setTileEntityItemStackRenderer(new DragonCoreBlockEntityRenderer.ItemStackRenderer());

        //Override mcmod.info - This looks cooler :)
        //TODO: LOCALIZATION
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
        addCredit(credits, "Majty/Guinea Owl", "For Amulet textures.");
        addCredit(credits, "NightScale", "For new ideas and once funding a server for DM2.");
        addCredit(credits, "Unakitononeko", "For the new and improved forest textures.");
        addCredit(credits, "Crafty", "For the new and improved ice textures.");
        addCredit(credits, "Charles Xavier", "Moderator.");
        addCredit(credits, "qnebra", "Moderator.");
        addCredit(credits, "Jester", "Ex Marine over 23 years old when this version is released, gave me life advices on how to improve myself.");
        addCredit(credits, "WheezieFreeBreezie and Eden", "For being friends with me while playing on a modded server along with our faction Atlas.");
        addCredit(credits, "WalterTheMighty", "For giving me a modded server experience.");
        addCredit(credits, "GandalfTheDank", "For making me realize on what happens to me if I'm being a terrible person.");
        addCredit(credits, "TheGreyGhost", "Old DM1 Developer. Prototype Dragon Breath.");
        addCredit(credits, "Tomanex", "Dragon Mounts Team Founder and Texture Artist. Revamped various textures");
        addCredit(credits, "JDSK0ala", "Dragon Mounts Team Founder and Beta Tester");
        addCredit(credits, "Moaswies", "Updating the code and continuing Dragon Mounts 2 on Java 1.12.2 after the Dragon Mounts team was founded");
        addCredit(credits, "2190303755 (Number-Man)", "Updating the code and continuing Dragon Mounts 2 on Java 1.12.2 after the Dragon Mounts team was founded");
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

        MinecraftForge.EVENT_BUS.register(new ModKeys());
        MinecraftForge.EVENT_BUS.register(new DragonViewEvent());
        MinecraftForge.EVENT_BUS.register(new RenderDM2Cape());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void render() {
        ModKeys.init();
    }

    public int getDragon3rdPersonView() {
        return thirdPersonViewDragon;
    }

    public void setDragon3rdPersonView(int view) {
        thirdPersonViewDragon = view;
    }

    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @Override
    public File getDataDirectory() {
        return Minecraft.getMinecraft().gameDir;
    }
            }
