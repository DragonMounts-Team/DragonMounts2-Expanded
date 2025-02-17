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
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.Collection;

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
        String title = String.format("%s %s Debug", DragonMountsTags.MOD_NAME, DragonMounts.getMetadata().version);
        text.setOrigin(16, 8);
        text.setColor(GREY);
        text.println(title);
        text.setColor(WHITE);
    }

    private static void renderEntityInfo(TameableDragonEntity dragon) {
        DecimalFormat dfShort = DebugOverlay.dfShort;
        text.setOrigin(16, 32);

        text.setColor(YELLOW);
        text.print("Entity ");
        text.setColor(WHITE);
        text.printf("(#%s)\n", dragon.getEntityId());
        text.println("Side: " + (dragon.world.isRemote ? "client" : "server"));
        text.println("UUID: " + StringUtils.abbreviate(dragon.getUniqueID().toString(), 22));
        text.println("Name: " + dragon.getName());

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
        text.printf("Pitch: %s; Yaw: %s; HeadYaw: %s\n", pitch, yaw, yawHead);

        // health
        String health = dfShort.format(dragon.getHealth());
        String healthMax = dfShort.format(dragon.getMaxHealth());
        String healthRel = dfShort.format(dragon.getHealth() / dragon.getMaxHealth() * 100);
        text.printf("Health: %s/%s (%s%%)\n", health, healthMax, healthRel);
        // hunger
        String hunger = dfShort.format(dragon.getHunger());
        text.printf("Hunger: %s\n", hunger);

        // variant
        DragonVariant variant = dragon.getVariant();
        text.println("Variant: " + variant.getRegistryName());
        text.print("Type: ");
        text.setColor(variant.type.color);
        text.println(ClientUtil.translateToLocal(variant.type.translationKey));
        text.setColor(WHITE);

        // life stage
        DragonLifeStageHelper helper = dragon.lifeStageHelper;
        int ticksSinceCreation = helper.getTicksSinceCreation();
        text.printf("Life Stage: %s %s (%d)\n", helper.getLifeStage().name(), dfShort.format(DragonLifeStage.getStageProgressFromTickCount(ticksSinceCreation)), ticksSinceCreation);

        // size
        String scale = dfShort.format(helper.getScale());
        String width = dfShort.format(dragon.width);
        String height = dfShort.format(dragon.height);
        AxisAlignedBB box = dragon.getEntityBoundingBox();
        text.printf(
                "Size: %s (w:%s h:%s)\nBox: (%s, %s, %s) (%s, %s, %s)\n",
                scale,
                width,
                height,
                dfShort.format(box.minX),
                dfShort.format(box.minY),
                dfShort.format(box.minZ),
                dfShort.format(box.maxX),
                dfShort.format(box.maxY),
                dfShort.format(box.maxZ)
        );

        // tamed flag/owner name
        //String tamedString = dragon.getOwnerName();
        String tamedString;
        if (dragon.isTamed()) {
            Entity player = dragon.getOwner();
            if (player != null) {
                tamedString = "yes (" + player.getName()+ ")";
            } else {
                tamedString = "yes (" + StringUtils.abbreviate(String.valueOf(dragon.getOwnerId()), 22) + ")";
            }
        } else {
            tamedString = "no";
        }
        text.println("Tamed: " + tamedString);

        String allowOthersString;
        if (dragon.allowedOtherPlayers()) {
        	allowOthersString = "yes"; 
        } else {
        	allowOthersString = "no";
        }
        text.println("AllowedOthers: " + allowOthersString);
        text.println("Reproduction Count: " + dragon.getReproductionCount());
    }

    private static void renderAttributes(TameableDragonEntity dragon) {
        text.setOrigin(text.getX() + 180, 8);

        text.setColor(YELLOW);
        text.println("Attributes");
        text.setColor(WHITE);

        Collection<IAttributeInstance> attrs = dragon.getAttributeMap().getAllAttributes();

        attrs.forEach(attr -> {
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
        text.setOrigin(16, 32);
        
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
    }

    private static void renderAITasks(TameableDragonEntity dragon) {
        text.setOrigin(text.getX() + 180, 8);

        text.setColor(YELLOW);
        text.println("AI tasks");
        text.setColor(WHITE);

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
        text.setOrigin(16, 32);
        text.setColor(RED);
        text.println("GUI exception:");
        text.printf(ExceptionUtils.getStackTrace(ex));
        text.setColor(WHITE);
        LogUtil.LOGGER.error("Error rendering", ex);
    }
}
