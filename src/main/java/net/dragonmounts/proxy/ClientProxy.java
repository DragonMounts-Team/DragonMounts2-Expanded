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

import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.block.entity.DragonHeadBlockEntity;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.breath.ClientBreathNodeEntity;
import net.dragonmounts.client.gui.DebugOverlay;
import net.dragonmounts.client.render.CarriageRenderer;
import net.dragonmounts.client.render.DMCapeRenderer;
import net.dragonmounts.client.render.DragonCoreBlockEntityRenderer;
import net.dragonmounts.client.render.DragonHeadBlockEntityRenderer;
import net.dragonmounts.client.render.dragon.ClientBreathNodeRenderer;
import net.dragonmounts.client.render.dragon.DragonRenderer;
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.client.variant.VariantAppearances;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.CarriageEntity;
import net.dragonmounts.event.CameraHandler;
import net.dragonmounts.event.ClientMisc;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.init.DMKeyBindings;
import net.dragonmounts.init.DragonVariants;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.function.Function;
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

    @Override
    public void PreInitialization(FMLPreInitializationEvent event) {
        super.PreInitialization(event);
        MinecraftForge.EVENT_BUS.register(ClientMisc.class);
        RenderingRegistry.registerEntityRenderingHandler(ClientDragonEntity.class, DragonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ClientBreathNodeEntity.class, ClientBreathNodeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CarriageEntity.class, CarriageRenderer::new);

        ClientRegistry.bindTileEntitySpecialRenderer(DragonCoreBlockEntity.class, new DragonCoreBlockEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DragonHeadBlockEntity.class, new DragonHeadBlockEntityRenderer());
        DMItems.DRAGON_CORE.setTileEntityItemStackRenderer(new DragonCoreBlockEntityRenderer.ItemStackRenderer());
        DragonHeadBlockEntityRenderer.ItemStackRenderer renderer = new DragonHeadBlockEntityRenderer.ItemStackRenderer();
        for (DragonVariant variant : DragonVariants.BUILTIN_VALUES) {
            variant.head.item.setTileEntityItemStackRenderer(renderer);
        }

        //Override mcmod.info - This looks cooler :)
        ModMetadata metadata = event.getModMetadata();
        metadata.authorList = metadata.authorList.stream().map(author ->
                TextFormatting.GOLD.toString() + TextFormatting.BOLD + author + TextFormatting.RESET
        ).collect(Collectors.toList());
    }

    @Override
    public void Initialization(FMLInitializationEvent evt) {
        super.Initialization(evt);
        DMKeyBindings.register();
    }

    @Override
    public void PostInitialization(FMLPostInitializationEvent event) {
        super.PostInitialization(event);
        MinecraftForge.EVENT_BUS.register(CameraHandler.class);
        MinecraftForge.EVENT_BUS.register(DMCapeRenderer.class);
        if (DMConfig.DEBUG_MODE.value) {
            // defer the task to be executed after initialization.
            new Thread(() -> Minecraft.getMinecraft().addScheduledTask(() -> {
                Minecraft.getMinecraft().debugRenderer.pathfindingEnabled = true;
            })).start();
            MinecraftForge.EVENT_BUS.register(DebugOverlay.class);
        }
    }

    @Override
    public Function<String, VariantAppearance> getBuiltinAppearances() {
        return VariantAppearances.getSupplier();
    }
}
