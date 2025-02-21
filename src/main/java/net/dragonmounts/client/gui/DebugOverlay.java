/*
 ** 2013 October 29
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.client.gui;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.dragonmounts.DragonMounts;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.entity.helper.DragonLifeStageHelper;
import net.dragonmounts.entity.helper.DragonVariantHelper;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.LogUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Set;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DebugOverlay {
    private static final int WHITE = 0xFFFFFF;
    private static final int GREY = 0xAAAAAA;
    private static final int YELLOW = 0xFFFF00;
    private static final int RED = 0xFF8888;
    private static final DecimalFormat dfShort = new DecimalFormat("0.00");
    private static final DecimalFormat dfLong = new DecimalFormat("0.0000");
    private static GuiTextPrinter text;
    private static TameableDragonEntity clientCache;
    private static TameableDragonEntity serverCache;
    
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent event) {
        if (!DMConfig.ENABLE_DEBUG_OVERLAY.value || event.isCancelable() || event.getType() != ElementType.TEXT)
            return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (text == null) {
            text = new GuiTextPrinter(minecraft.fontRenderer);
        }
        TameableDragonEntity client = getClientDragon(minecraft);
        if (client == null) return;
        TameableDragonEntity server = getServerDragon(minecraft, client);
        TameableDragonEntity selected = server == null
                ? client
                : Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
                ? client
                : server;
        renderTitle();
        try {
            if (server != null) {
                if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    renderAttributes(selected);
                    renderNavigation(server);
                    renderBreedPoints(server);
                } else {
                    renderEntityInfo(selected);
                    renderAITasks(server);
                }
            } else if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                renderAttributes(selected);
            } else {
                renderEntityInfo(selected);
            }
        } catch (Exception ex) {
            renderException(ex);
        }
        if (client.isDead) {
            clientCache = null;
            serverCache = null;
        }
    }

    @Nullable
    private static TameableDragonEntity getClientDragon(Minecraft minecraft) {
        // always return currently ridden dragon first
        Entity entity = minecraft.player.getRidingEntity();
        if (entity instanceof TameableDragonEntity) {
            return clientCache = (TameableDragonEntity) entity;
        }
        if (minecraft.objectMouseOver == null) return clientCache;
        entity = minecraft.objectMouseOver.entityHit;
        if (entity instanceof TameableDragonEntity) {
            return clientCache = (TameableDragonEntity) entity;
        }
        return clientCache;
    }

    @Nullable
    private static TameableDragonEntity getServerDragon(Minecraft minecraft, TameableDragonEntity client) {
        if (!minecraft.isSingleplayer()) return null; // impossible on dedicated
        int target = client.getEntityId();
        if (serverCache != null && serverCache.getEntityId() == target) {
            // cache hit
            return serverCache;
        }
        MinecraftServer server = minecraft.getIntegratedServer();
        if (server == null) return null; // unnecessary, but safe
        for (WorldServer level : server.worlds) {
            Entity entity = level.getEntityByID(target);
            if (entity instanceof TameableDragonEntity) {
                return serverCache = (TameableDragonEntity) entity;
            }
        }
        return null;
    }

    private static void renderTitle() {
        text.setOrigin(8, 8);
        text.setColor(GREY);
        text.print(DragonMountsTags.MOD_NAME + " " + DragonMounts.getMetadata().version);
        text.setColor(WHITE);
    }

    private static void renderEntityInfo(TameableDragonEntity dragon) {
        DecimalFormat dfShort = DebugOverlay.dfShort;
        text.setOrigin(8, 32);

        text.setColor(YELLOW);
        text.print(dragon.world.isRemote ? "Client" : "Server");
        text.print(" Entity ");
        text.setColor(WHITE);
        text.printf("(#%s)\n", dragon.getEntityId());
        text.println("Name: " + dragon.getName());

        // variant
        DragonVariant variant = dragon.getVariant();
        text.print("Type: ");
        text.setColor(variant.type.color);
        text.println(ClientUtil.translateToLocal(variant.type.translationKey));
        text.setColor(WHITE);
        text.println("Variant: " + variant.getRegistryName());

        // position
        String px = dfShort.format(dragon.posX);
        String py = dfShort.format(dragon.posY);
        String pz = dfShort.format(dragon.posZ);
        String mx = dfShort.format(dragon.motionX);
        String my = dfShort.format(dragon.motionY);
        String mz = dfShort.format(dragon.motionZ);
        text.printf("Position: (%s, %s, %s)\nMotion: (%s, %s, %s)\n", px, py, pz, mx, my, mz);

        // rotation
        String pitch = dfShort.format(dragon.rotationPitch);
        String yaw = dfShort.format(dragon.rotationYaw);
        String yawHead = dfShort.format(dragon.rotationYawHead);
        text.printf("Pitch: %s\nYaw: %s\nHeadYaw: %s\n", pitch, yaw, yawHead);

        // health
        String health = dfShort.format(dragon.getHealth());
        String healthMax = dfShort.format(dragon.getMaxHealth());
        String healthRel = dfShort.format(dragon.getHealth() / dragon.getMaxHealth() * 100);
        text.printf("Health: %s/%s (%s%%)\n", health, healthMax, healthRel);

        // hunger
        String hunger = dfShort.format(dragon.getHunger());
        text.printf("Hunger: %s\n", hunger);

        // life stage
        DragonLifeStageHelper helper = dragon.lifeStageHelper;
        int ticksSinceCreation = helper.getTicksSinceCreation();
        text.printf("Life Stage: %s %s (%d)\n", helper.getLifeStage().name(), dfShort.format(DragonLifeStage.getStageProgressFromTickCount(ticksSinceCreation)), ticksSinceCreation);

        // size
        text.println(String.format(
                "Size: %s (w:%s h:%s)",
                dfShort.format(helper.getScale()),
                dfShort.format(dragon.width),
                dfShort.format(dragon.height)
        ));
        text.println("Trust Other Players: " + (dragon.allowedOtherPlayers() ? "Yes" : "No"));
        text.println("Reproduction Count: " + dragon.getReproductionCount());
        text.print("Tamed: ");
        // tamed flag/owner name
        //String tamedString = dragon.getOwnerName();
        if (dragon.isTamed()) {
            Entity player = dragon.getOwner();
            text.println("Yes (" + (player == null
                    ? Objects.requireNonNull(dragon.getOwnerId())
                    : player.getName()
            ) + ")");
        } else {
            text.println("No");
        }
        text.println("UUID: " + dragon.getUniqueID());
    }

    private static void renderAttributes(TameableDragonEntity dragon) {
        text.setOrigin(196, 8);

        text.setColor(YELLOW);
        text.println("Attributes");
        text.setColor(WHITE);
        dragon.getAttributeMap().getAllAttributes().forEach(attr -> {
            String attribName = ClientUtil.translateToLocal("attribute.name." + attr.getAttribute().getName());
            String attribValue = dfShort.format(attr.getAttributeValue());
            String attribBase = dfShort.format(attr.getBaseValue());
            text.println(attribName + " = " + attribValue + " (" + attribBase + ")");
        });

        text.println();
    }

    private static void renderBreedPoints(TameableDragonEntity dragon) {
        text.setColor(YELLOW);
        text.println("Breed points");
        text.setColor(WHITE);
        int top = text.getY();
        int[] data = {0, 0};// lines, end
        DragonVariantHelper helper = dragon.variantHelper;
        for (Reference2IntMap.Entry<DragonType> entry : helper.getBreedPoints().reference2IntEntrySet()) {
            text.setColor(entry.getKey().color);
            text.printf("%s: %d", ClientUtil.translateToLocal(entry.getKey().translationKey), entry.getIntValue());
            if (text.getX() > data[1]) {
                data[1] = text.getX();
            }
            text.println();
            if (++data[0] > 4) {
                data[0] = 0;
                text.setOrigin(data[1] + 5, top);
            }
        }
    }

    private static void renderNavigation(TameableDragonEntity dragon) {
        text.setOrigin(8, 32);
        
        text.setColor(YELLOW);
        text.println("Navigation (Ground)");
        text.setColor(WHITE);

        PathNavigate nav = dragon.getNavigator();
        PathNavigateGround pathNavigateGround = null;
        if (nav instanceof PathNavigateGround) {
            pathNavigateGround = (PathNavigateGround) nav;
        }

        text.println("Search range: " + nav.getPathSearchRange());
        text.println("Can swim: " + (pathNavigateGround == null ? "N/A" : pathNavigateGround.getCanSwim()));
        text.println("Break doors: " + (pathNavigateGround == null ? "N/A" : pathNavigateGround.getEnterDoors()));
        text.println("No path: " + nav.noPath());

        Path path = nav.getPath();

        if (path != null) {
            text.println("Length: " + path.getCurrentPathLength());
            text.println("Index: " + path.getCurrentPathIndex());
            
            PathPoint finalPoint = path.getFinalPathPoint();
            text.println("Final point: " + finalPoint);
        }

        text.println();
        text.setColor(YELLOW);
        text.println("Navigation (Air)");
        text.setColor(WHITE);

        text.println("Can fly: " + dragon.canFly());
        text.println("Flying: " + dragon.isFlying());
        text.println("Altitude: " + dfLong.format(dragon.getAltitude()));
        text.println();
    }

    private static void renderTasks(String label, Set<EntityAITasks.EntityAITaskEntry> tasks) {
        text.setColor(YELLOW);
        text.println(label);
        text.setColor(WHITE);
        Object2ObjectOpenHashMap<String, ObjectArrayList<String>> map = new Object2ObjectOpenHashMap<>();
        for (EntityAITasks.EntityAITaskEntry entry : tasks) {
            String full = entry.action.getClass().getName();
            int dot = full.lastIndexOf('.');
            map.computeIfAbsent(
                    full.substring(0, dot),
                    ignored -> new ObjectArrayList<>()
            ).add(full.substring(dot + 1));
        }
        for (Object2ObjectMap.Entry<String, ObjectArrayList<String>> entry : map.object2ObjectEntrySet()) {
            text.println(entry.getKey());
            for (String value : entry.getValue()) {
                text.print("-   ");
                text.println(value);
            }
        }
        text.println();
    }

    private static void renderAITasks(TameableDragonEntity dragon) {
        text.setOrigin(196, 8);
        renderTasks("Running Goals", dragon.tasks.executingTaskEntries);
        renderTasks("Running Target Goals", dragon.targetTasks.executingTaskEntries);
        EntityLivingBase target = dragon.getAttackTarget();
        if (target == null) {
            text.println("Current Target: None");
        } else {
            text.println("Current Target: " + target.getDisplayName().getFormattedText());
            text.println("Target Type: " + target.getClass().getName());
            text.println("Target UUID: " + target.getUniqueID());
        }
    }

    private static void renderException(Exception ex) {
        LogUtil.LOGGER.error("Error rendering", ex);
        text.setOrigin(8, 32);
        text.setColor(RED);
        text.println("GUI Exception:");
        text.printf(ExceptionUtils.getStackTrace(ex));
        text.setColor(WHITE);
    }
}
