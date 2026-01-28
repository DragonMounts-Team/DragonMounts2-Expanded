package net.dragonmounts.compat.data;

import net.dragonmounts.entity.helper.DragonVariantHelper;
import net.dragonmounts.init.DragonVariants;
import net.dragonmounts.item.IEntityContainer;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class DragonEntityFixer implements IFixableData {
    public static void fixDragonData(NBTTagCompound tag) {
        tag.setBoolean("FollowOwner", true);
        if (tag.hasKey("breedPoints")) {
            tag.setTag(DragonVariantHelper.NBT_VARIANT_POINTS, tag.getCompoundTag("breedPoints"));
            tag.removeTag("breedPoints");
        }
        if (tag.hasKey(DragonVariant.SERIALIZATION_KEY)) {
            switch (tag.getString(DragonVariant.SERIALIZATION_KEY)) {
                case "dragonmounts:fire_rare":
                    tag.setString(DragonVariant.SERIALIZATION_KEY, "dragonmounts:blue_fire");
                    break;
                case "dragonmounts:storm_rare":
                    tag.setString(DragonVariant.SERIALIZATION_KEY, "dragonmounts:bronzed_storm");
                    break;
            }
        } else if (tag.hasKey("Breed")) {
            boolean isMale = tag.getBoolean("IsMale");
            DragonVariant variant;
            switch (tag.getString("Breed")) {
                case "aether":
                    variant = isMale ? DragonVariants.AETHER_MALE : DragonVariants.AETHER_FEMALE;
                    break;
                case "enchant":
                    variant = isMale ? DragonVariants.ENCHANTED_MALE : DragonVariants.ENCHANTED_FEMALE;
                    break;
                case "fire":
                    variant = isMale ? DragonVariants.FIRE_MALE : DragonVariants.FIRE_FEMALE;
                    break;
                case "forest":
                    tag.setBoolean("DataFix$IsForest", true);
                    variant = DragonVariants.FOREST_MALE;
                    break;
                case "ice":
                    variant = isMale ? DragonVariants.ICE_MALE : DragonVariants.ICE_FEMALE;
                    break;
                case "moonlight":
                    variant = isMale ? DragonVariants.MOONLIGHT_MALE : DragonVariants.MOONLIGHT_FEMALE;
                    break;
                case "nether":
                    variant = isMale ? DragonVariants.NETHER_MALE : DragonVariants.NETHER_FEMALE;
                    break;
                case "skeleton":
                    variant = DragonVariants.SKELETON;
                    break;
                case "storm":
                    variant = isMale ? DragonVariants.STORM_MALE : DragonVariants.STORM_FEMALE;
                    break;
                case "sunlight":
                    variant = isMale ? DragonVariants.SUNLIGHT_MALE : DragonVariants.SUNLIGHT_FEMALE;
                    break;
                case "terra":
                    variant = isMale ? DragonVariants.TERRA_MALE : DragonVariants.TERRA_FEMALE;
                    break;
                case "water":
                case "sylphid":
                    variant = isMale ? DragonVariants.WATER_MALE : DragonVariants.WATER_FEMALE;
                    break;
                case "wither":
                    variant = DragonVariants.WITHER;
                    break;
                case "zombie":
                    variant = DragonVariants.ZOMBIE;
                    break;
                case "ender":
                case "end":
                default:
                    variant = isMale ? DragonVariants.ENDER_MALE : DragonVariants.ENDER_FEMALE;
                    break;
            }
            tag.setString(DragonVariant.SERIALIZATION_KEY, variant.getName());
            tag.removeTag("Breed");
        }
    }

    public static NBTTagCompound fixContainerItem(NBTTagCompound root) {
        NBTTagCompound result = new NBTTagCompound();
        if (root.hasKey("display")) {
            result.setTag("display", root.getCompoundTag("display"));
            root.removeTag("display");
        }
        NBTTagCompound entity = IEntityContainer.simplifyData(root).copy();
        fixDragonData(entity);
        entity.setString("id", "dragonmounts:dragon");
        result.setTag("EntityTag", entity);
        return result;
    }

    @Override
    public int getFixVersion() {
        return 2;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound tag) {
        if (!"dragonmounts:dragon".equals(tag.getString("id"))) return tag;
        fixDragonData(tag);
        return tag;
    }
}
