package net.dragonmounts.compat.fixer;

import net.dragonmounts.init.DragonVariants;
import net.dragonmounts.entity.helper.DragonVariantHelper;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class DragonEntityCompat implements IFixableData {
    @Override
    public int getFixVersion() {
        return 0;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound tag) {
        if (!tag.getString("id").equals("dragonmounts:dragon")) return tag;
        if (tag.hasKey("breedPoints")) {
            tag.setTag(DragonVariantHelper.NBT_VARIANT_POINTS, tag.getCompoundTag("breedPoints"));
            tag.removeTag("breedPoints");
        }
        if (tag.hasKey("Breed")) {
            boolean isMale = tag.getBoolean("IsMale");
            DragonVariant variant;
            switch (tag.getString("Breed")) {
                case "aether":
                    variant = isMale ? DragonVariants.AETHER_MALE : DragonVariants.AETHER_FEMALE;
                    break;
                case "enchant":
                    variant = isMale ? DragonVariants.ENCHANT_MALE : DragonVariants.ENCHANT_FEMALE;
                    break;
                case "fire":
                    variant = isMale ? DragonVariants.FIRE_MALE : DragonVariants.FIRE_FEMALE;
                    break;
                case "forest":
                    tag.setBoolean("DataFix$IsForest", true);
                    variant = isMale ? DragonVariants.FOREST_MALE : DragonVariants.FOREST_FEMALE;
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
                    variant = isMale ? DragonVariants.SKELETON_MALE : DragonVariants.SKELETON_FEMALE;
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
                    variant = isMale ? DragonVariants.WATER_MALE : DragonVariants.WATER_FEMALE;
                    break;
                case "wither":
                    variant = isMale ? DragonVariants.WITHER_MALE : DragonVariants.WITHER_FEMALE;
                    break;
                case "zombie":
                    variant = isMale ? DragonVariants.ZOMBIE_MALE : DragonVariants.ZOMBIE_FEMALE;
                    break;
                case "ender":
                default:
                    variant = isMale ? DragonVariants.ENDER_MALE : DragonVariants.ENDER_FEMALE;
                    break;
            }
            tag.setString(DragonVariant.DATA_PARAMETER_KEY, variant.getSerializedName());
            tag.removeTag("Breed");
        }
        return tag;
    }
}
