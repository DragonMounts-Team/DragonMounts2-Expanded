/*
 ** 2012 August 27
 **
 ** The author disclaims copyright to this source code.  In place of
 ** t.AQUA legal notice, here is t.AQUA blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package top.dragonmounts.proxy;

import top.dragonmounts.DragonMountsConfig;
import top.dragonmounts.client.gui.GuiDragonDebug;
import top.dragonmounts.client.other.TargetHighlighter;
import top.dragonmounts.client.render.DragonCoreBlockEntityRenderer;
import top.dragonmounts.client.render.RenderDM2Cape;
import top.dragonmounts.client.render.dragon.DragonRenderer;
import top.dragonmounts.client.render.dragon.breathweaponFX.ClientBreathNodeRenderer;
import top.dragonmounts.client.userinput.DragonOrbControl;
import top.dragonmounts.event.DragonViewEvent;
import top.dragonmounts.event.IItemColorRegistration;
import top.dragonmounts.inits.ModBlocks;
import top.dragonmounts.inits.ModKeys;
import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.effects.ClientBreathNodeEntity;
import top.dragonmounts.objects.tileentities.TileEntityDragonShulker;
import top.dragonmounts.util.debugging.StartupDebugClientOnly;
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
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;


/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * 2nd @author TheRPGAdventurer
 */
public class ClientProxy extends ServerProxy {

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
        TextFormatting t = null, r = TextFormatting.RESET;
        ModMetadata metadata = event.getModMetadata();
        metadata.name = t.DARK_AQUA + "" + t.BOLD + "Dragon Mounts 2";
        metadata.credits = "\n" +
                t.GREEN + "BarracudaATA" + r + "-" + t.AQUA + "The Original Owner of Dragon Mounts.\n\n" +
                t.GREEN + "TheRPGAdventurer" + r + "-" + t.AQUA + "Former author of Dragon Mounts 2.\n\n" +
                t.GREEN + "Kingdomall" + r + "-" + t.AQUA + "First Developer for DM2. Overhauling many textures.\n\n" +
                t.GREEN + "Shannieann" + r + "-" + t.AQUA + "Second Developer for DM2. Zombie and Terra textures. Texture artist.\n\n" +
                t.GREEN + "UkanGundun" + r + "-" + t.AQUA + "Fire and Sunlight textures. Texture artist.\n\n" +
                t.GREEN + "WolfShotz (Kay9Unit)" + r + "-" + t.AQUA + "Coder and codebase improvements\n\n" +
                t.GREEN + "FlaemWing" + r + "-" + t.AQUA + "New nest textures, tool textures and dragon armor item textures.\n\n" +
                t.GREEN + "AlexThe666" + r + "-" + t.AQUA + "Open sourcing the code for I&F, Ice and Fire Owner.\n\n" +
                t.GREEN + "Majty/Guinea Owl" + r + "-" + t.AQUA + "For Amulet textures.\n" +
                t.GREEN + "NightScale" + r + "-" + t.AQUA + "For new ideas and once funding a server for DM2.\n" +
                t.GREEN + "Unakitononeko" + r + "-" + t.AQUA + "For the new and improved forest textures.\n" +
                t.GREEN + "Crafty" + r + "-" + t.AQUA + "For the new and improved ice textures.\n" +
                t.GREEN + "Charles Xavier" + r + "-" + t.AQUA + "Moderator.\n" +
                t.GREEN + "qnebra" + r + "-" + t.AQUA + "Moderator.\n" +
                t.GREEN + "Jester" + r + "-" + t.AQUA + "Ex Marine over 23 years old when this version is released, gave me life advices on how to improve myself.\n" +
                t.GREEN + "WheezieFreeBreezie and Eden" + r + "-" + t.AQUA + "For being friends with me while playing on a modded server along with our faction Atlas.\n" +
                t.GREEN + "WalterTheMighty" + r + "-" + t.AQUA + "For giving me a modded server experience.\n" +
                t.GREEN + "GandalfTheDank" + r + "-" + t.AQUA + "For making me realize on what happens to me if I'm being a terrible person.\n" +
                t.GREEN + "TheGreyGhost" + r + "-" + t.AQUA + "Old DM1 Developer. Prototype Dragon Breath.\n\n";
        metadata.authorList = Arrays.asList(StringUtils.split(t.GOLD + "" + t.BOLD + "TheRPGAdventurer, Kingdomall, Shannieann, UkanGundun, WolfShotz (Kay9Unit), BarracudaATA", ','));
        metadata.description =
                "\nTips:\n" +
                        "1. Don't forget to right click the egg to start the hatching process\n" +
                        "2. Also water dragon needs to be struck by lightning to become t.AQUA storm dragon\n" +
                        "3. You can't hatch eggs in the End Dimension\n" +
                        "4. You can press " + t.ITALIC + "ctrl" + r + " to enable boost flight\n" +
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
