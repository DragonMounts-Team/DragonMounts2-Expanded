/*
 ** 2012 August 27
 **
 ** The author disclaims copyright to this source code.  In place of
 ** t.AQUA legal notice, here is t.AQUA blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD.proxy;

import com.TheRPGAdventurer.ROTD.DragonMountsConfig;
import com.TheRPGAdventurer.ROTD.client.gui.GuiDragonDebug;
import com.TheRPGAdventurer.ROTD.client.model.ModelAmuletMesh;
import com.TheRPGAdventurer.ROTD.client.other.TargetHighlighter;
import com.TheRPGAdventurer.ROTD.client.render.RenderDM2Cape;
import com.TheRPGAdventurer.ROTD.client.render.TileEntityDragonShulkerRenderer;
import com.TheRPGAdventurer.ROTD.client.render.dragon.DragonRenderer;
import com.TheRPGAdventurer.ROTD.client.render.dragon.breathweaponFX.ClientBreathNodeRenderer;
import com.TheRPGAdventurer.ROTD.client.userinput.DragonOrbControl;
import com.TheRPGAdventurer.ROTD.event.DragonViewEvent;
import com.TheRPGAdventurer.ROTD.event.IItemColorRegistration;
import com.TheRPGAdventurer.ROTD.inits.ModBlocks;
import com.TheRPGAdventurer.ROTD.inits.ModItems;
import com.TheRPGAdventurer.ROTD.inits.ModKeys;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.effects.ClientBreathNodeEntity;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import com.TheRPGAdventurer.ROTD.objects.tileentities.TileEntityDragonShulker;
import com.TheRPGAdventurer.ROTD.util.debugging.StartupDebugClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
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
    private int lockY = 0;
    private boolean followYaw = false;
    private boolean hover = false;

    @Override
    public void PreInitialization(FMLPreInitializationEvent event) {
        super.PreInitialization(event);
        MinecraftForge.EVENT_BUS.register(IItemColorRegistration.class);
        // register dragon entity renderer
        DragonMountsConfig.clientPreInit();
        RenderingRegistry.registerEntityRenderingHandler(EntityTameableDragon.class, DragonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ClientBreathNodeEntity.class, ClientBreathNodeRenderer::new);

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDragonShulker.class, new TileEntityDragonShulkerRenderer());
        ModBlocks.DRAGONSHULKER.item.setTileEntityItemStackRenderer(new TileEntityDragonShulkerRenderer.ItemStackRenderer());

        //Override mcmod.info - This looks cooler :)
        TextFormatting t = null, r = TextFormatting.RESET;
        ModMetadata metadata = event.getModMetadata();
        metadata.name = t.DARK_AQUA + "" + t.BOLD + "Dragon Mounts";
        metadata.credits = "\n" +
                t.GREEN + "BarracudaATA4" + r + "-" + t.AQUA + "The Original Owner\n\n" +
                t.GREEN + "TheRPGAdventurer" + r + "-" + t.AQUA + "Former author of Dragon Mounts 2\n\n" +
                t.GREEN + "Merpou/Kingdomall/Masked_Ares" + r + "-" + t.AQUA + "First Dev for DM2. Has Made 500+ Textures and has put forth so much effort.\n\n" +
                t.GREEN + "Shannieanne" + r + "-" + t.AQUA + "Zombie Textures, Terra textures, Texture Fixes, Overall Second Dev\n\n" +
                t.GREEN + "GundunUkan/Lord Ukan" + r + "-" + t.AQUA + "for new fire texures, sunlight textures, and more.... I Hope he finishes his university hes hardworking working student\n\n" +
                t.GREEN + "Wolf" + r + "-" + t.AQUA + "Second Coder, started making small fixes then started doing big ones, I hope his dreams of becoming computer engineer succeeds\n\n" +
                t.GREEN + "FlaemWing" + r + "-" + t.AQUA + "for new nest block textures and dragonarmor item textures, new tool textures\n\n" +
                t.GREEN + "AlexThe666" + r + "-" + t.AQUA + "for open source code, Ice and Fire owner, Older Matured and more experience than me\n\n" +
                t.GREEN + "Majty/Guinea Owl" + r + "-" + t.AQUA + "for amulet textures\n" +
                t.GREEN + "NightScale" + r + "-" + t.AQUA + "for new ideas and once funding a server for DM2\n" +
                t.GREEN + "Unakitononeko" + r + "-" + t.AQUA + "for the new and improved forest textures\n" +
                t.GREEN + "Crafty" + r + "-" + t.AQUA + "for the new and improved ice textures\n" +
                t.GREEN + "Charles Xavier" + r + "-" + t.AQUA + "Moderator\n" +
                t.GREEN + "qnebra" + r + "-" + t.AQUA + "Moderator\n" +
                t.GREEN + "Jester" + r + "-" + t.AQUA + "Ex Marine over 23 years old when this version is released, gave me life advices on how to improve myself\n" +
                t.GREEN + "WheezieFreeBreezie and Eden" + r + "-" + t.AQUA + "for being friends with me while playing on a modded server along with our faction Atlas\n" +
                t.GREEN + "WalterTheMighty" + r + "-" + t.AQUA + "for giving me a modded erver experience\n" +
                t.GREEN + "GandalfTheDank" + r + "-" + t.AQUA + "for making me realize on what happens to me if I'm being a terrible person\n" +
                t.GREEN + "TGG/TheGreyGhost" + r + "-" + t.AQUA + "old dm1 dev and prototype breath\n\n";
        metadata.authorList = Arrays.asList(StringUtils.split(t.GOLD + "" + t.BOLD + "Lintha, TheRPGAdventurer, BarracudaATA, WolfShotz", ','));
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
        System.out.println("Registered Amulets");
        ModelLoader.setCustomMeshDefinition(ModItems.Amulet, new ModelAmuletMesh());
        ModelBakery.registerItemVariants(ModItems.Amulet, new ModelResourceLocation("dragonmounts:dragon_amulet"));
        EnumDragonBreed.META_MAPPING.forEach((breed, meta) -> {
            ModelBakery.registerItemVariants(ModItems.Amulet, new ModelResourceLocation("dragonmounts:" + breed.getName() + "_dragon_amulet"));
        });

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

    public boolean getDragonFollowYaw() {
        return followYaw;
    }

    public void setDragonFollowYaw(boolean followYaw) {
        this.followYaw = followYaw;
    }

    public void setDragonLockY(int lockY) {
        this.lockY = lockY;
    }

    @Override
    public boolean getDragonHover() {
        return hover;
    }

    @Override
    public void setDragonHover(boolean hover) {
        this.hover = hover;
    }

    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @Override
    /**Handles Amulet Model Variations*/
    public void registerAmuletRenderer() {
        ModelLoader.setCustomMeshDefinition(ModItems.Amulet, new ModelAmuletMesh());
        ModelBakery.registerItemVariants(ModItems.Amulet, new ModelResourceLocation("dragonmounts:dragon_amulet"));
        EnumDragonBreed.META_MAPPING.forEach((breed, meta) -> {
            ModelBakery.registerItemVariants(ModItems.Amulet, new ModelResourceLocation("dragonmounts:" + breed.getName() + "_dragon_amulet"));
        });
    }

    @Override
    public File getDataDirectory() {
        return Minecraft.getMinecraft().gameDir;
    }
}
