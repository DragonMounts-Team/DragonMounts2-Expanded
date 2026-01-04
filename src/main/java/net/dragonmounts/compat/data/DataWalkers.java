package net.dragonmounts.compat.data;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.dragonmounts.compat.DragonTypeCompat;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraftforge.common.util.Constants;

public abstract class DataWalkers {
    private static final ObjectSet<String> ENTITY_CONTAINERS;

    public static NBTTagCompound fixEntityContainers(IDataFixer fixer, NBTTagCompound root, int version) {
        if (ENTITY_CONTAINERS.contains(root.getString("id"))) {
            NBTTagCompound entity = root.getCompoundTag("tag");
            NBTBase id = entity.hasKey("tag", Constants.NBT.TAG_STRING) ? entity.getTag("id") : null;
            if (id != null) {
                entity.setString("id", "dragonmounts:dragon");
            }
            fixer.process(FixTypes.ENTITY, entity, version);
            if (id != null) {
                entity.setTag("id", id);
            }
        }
        return root;
    }

    public static NBTTagCompound fixDragonCore(IDataFixer fixer, NBTTagCompound root, int version) {
        String name = root.getString("id");
        if ("dragonmounts:dragon_shulker".equals(name)) {
            DataFixesManager.processInventory(fixer, root, version, "Items");
        } else if ("dragonmounts:dragon_core".equals(name) && root.hasKey("Item", Constants.NBT.TAG_COMPOUND)) {
            fixer.process(FixTypes.ITEM_INSTANCE, root.getCompoundTag("Item"), version);
        }
        return root;
    }

    public static NBTTagCompound fixDragonInventory(IDataFixer fixer, NBTTagCompound root, int version) {
        if ("dragonmounts:dragon".equals(root.getString("id"))) {
            DataFixesManager.processInventory(fixer, root, version, "Items");
        }
        return root;
    }

    static {
        ObjectOpenHashSet<String> containers = new ObjectOpenHashSet<>();
        containers.add("dragonmounts:dragon_amulet");
        for (DragonTypeCompat type : DragonTypeCompat.values()) {
            containers.add("dragonmounts:" + type.identifier + "_dragon_amulet");
            containers.add("dragonmounts:" + type.identifier + "_dragon_essence");
        }
        ENTITY_CONTAINERS = ObjectSets.unmodifiable(containers);
    }

    private DataWalkers() {}
}
