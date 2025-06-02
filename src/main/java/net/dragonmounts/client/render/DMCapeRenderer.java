package net.dragonmounts.client.render;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.DragonMountsTags;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class DMCapeRenderer {
    public static final ResourceLocation SUN_CAPE = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/models/misc/sun_cape.png");
    public static final ResourceLocation ICE_CAPE = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/models/misc/wolfs_cape.png");
    public static final ResourceLocation STORM_CAPE = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/models/misc/storm_cape.png");
    public static final ResourceLocation NETHER_CAPE = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/models/misc/nether_cape.png");
    public static final ResourceLocation FOREST_CATE = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/models/misc/forest_cape.png");
    public static final Object2ObjectMap<UUID, ResourceLocation> PLAYER_CAPES = new Object2ObjectOpenHashMap<>();
    static {
        PLAYER_CAPES.put(UUID.fromString("003b050f-f6fd-43b5-9738-669b23c3452f"), SUN_CAPE);// GundunUkan
        PLAYER_CAPES.put(UUID.fromString("eb9a02ed-587a-45c7-abaa-4ab28c5eedd4"), FOREST_CATE);// me
        PLAYER_CAPES.put(UUID.fromString("1f01c469-70de-4ad3-bc60-deb66db410f2"), ICE_CAPE);// Wolf
        PLAYER_CAPES.put(UUID.fromString("8a89b0d3-1bb2-431a-94cb-c7e304933176"), STORM_CAPE);// Kingdomall
        PLAYER_CAPES.put(UUID.fromString("7d5cbd00-af13-4ae7-b925-edbff61b2c56"), NETHER_CAPE);// Shannieanne
    }

    @SubscribeEvent
    public static void playerRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player instanceof AbstractClientPlayer) {
            NetworkPlayerInfo info = ((AbstractClientPlayer) player).playerInfo;
            if (info == null) return;
            Map<MinecraftProfileTexture.Type, ResourceLocation> textureMap = info.playerTextures;
            if (textureMap == null) return;
            ResourceLocation texture = PLAYER_CAPES.get(player.getUniqueID());
            if (texture == null) return;
            textureMap.put(MinecraftProfileTexture.Type.CAPE, texture);
        }
    }
}
