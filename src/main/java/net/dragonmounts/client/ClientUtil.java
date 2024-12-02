package net.dragonmounts.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import net.dragonmounts.objects.items.EnumItemBreedTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.ModelLoader;

/**
 * to avoid {@link ClassNotFoundException}
 */
public abstract class ClientUtil {
    public static EntityPlayer getLocalPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public static void registerDragonEggModel(Item item) {
        // register item renderer for dragon egg block variants
        String model = DragonMountsTags.MOD_ID + ":dragon_egg";
        for (EnumDragonBreed breed : EnumDragonBreed.values()) {
            ModelLoader.setCustomModelResourceLocation(item, breed.meta, new ModelResourceLocation(model, "breed=" + breed.identifier));
        }
    }

    public static void registerAmuletModel(Item item) {
        EnumItemBreedTypes[] types = EnumItemBreedTypes.values();
        int size = types.length;
        Object2ObjectOpenHashMap<String, ModelResourceLocation> mapping = new Object2ObjectOpenHashMap<>();
        ModelResourceLocation empty = new ModelResourceLocation("dragonmounts:dragon_amulet");
        ModelResourceLocation[] models = new ModelResourceLocation[size + 1];
        models[0] = empty;
        for (int i = 0; i < size; ) {
            EnumItemBreedTypes breed = types[i];
            ModelResourceLocation model = new ModelResourceLocation("dragonmounts:" + breed.identifier + "_dragon_amulet");
            mapping.put(breed.identifier, model);
            models[++i] = model;
        }
        ModelLoader.setCustomMeshDefinition(item, stack -> {
            NBTTagCompound root = stack.getTagCompound();
            return root == null ? empty : mapping.get(root.getString("breed"));
        });
        ModelBakery.registerItemVariants(item, models);
    }

    private ClientUtil() {}
}
