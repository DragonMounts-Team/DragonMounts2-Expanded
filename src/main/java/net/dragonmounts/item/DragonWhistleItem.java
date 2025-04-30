package net.dragonmounts.item;

import com.mojang.authlib.GameProfile;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.client.gui.DragonWhistleGui;
import net.dragonmounts.compat.DragonTypeCompat;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.registry.DragonType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static net.minecraft.tileentity.TileEntitySkull.updateGameProfile;

/**
 * Dragon Whistle Item for controlling certain dragon behaviour remotely.
 *
 * @author TheRPGAdventurer
 * @modifier WolfShotz
 */
public class DragonWhistleItem extends Item {
    public static final String TRANSLATION_KEY = DragonMountsTags.TRANSLATION_KEY_PREFIX + "dragon_whistle";
    public static final String DRAGON_UUID_KEY = "DragonUUID";
    public static final String DEPRECATED_UUID_KEY = "dragonmountsdragon";

    public static void bindWhistle(ItemStack stack, TameableDragonEntity dragon, EntityPlayer player) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setUniqueId(DRAGON_UUID_KEY, dragon.getUniqueID());
        if (dragon.hasCustomName()) {
            nbt.setString("Name", dragon.getCustomNameTag());
        }
        nbt.setString("Age", dragon.lifeStageHelper.getLifeStage().translationKey);
        nbt.setString("OwnerName", player.getName());
        nbt.setUniqueId("Owner", player.getUniqueID());
        DragonType type = dragon.getVariant().type;
        nbt.setInteger("Color", type.color);
        nbt.setString("Type", type.identifier.toString());
        stack.setTagCompound(nbt);
    }

    /// Open Dragon Whistle gui for dragon with given uuid
    @SideOnly(Side.CLIENT)
    public static void openDragonWhistleGui(@Nullable UUID uuid, World world, EnumHand hand) {
        if (uuid == null || !world.isRemote) return;
        Minecraft.getMinecraft().displayGuiScreen(new DragonWhistleGui(uuid));
    }


    public DragonWhistleItem() {
        this.setMaxStackSize(1);
    }

    /// Compat
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) return null;
        if (root.hasKey("Breed")) {
            root.setString("Type", DragonTypeCompat.MAPPING.getOrDefault(
                    root.getString("Breed"),
                    DragonTypes.ENDER
            ).identifier.toString());
            root.removeTag("Breed");
        }
        if (root.hasKey("Age", 8)) {
            String name = root.getString("Age");
            if ("life_stage.dragon.prejuvenile".equals(name)) {
                root.setString("Age", "life_stage.dragon.fledgling");
            } else if (name.startsWith("dragon.")) {
                root.setString("Age", "life_stage." + name);
            }
        }
        if (root.hasUniqueId(DEPRECATED_UUID_KEY)) {
            String most = DEPRECATED_UUID_KEY + "Most";
            root.setLong("DragonUUIDMost", root.getLong(most));
            root.removeTag(most);
            String least = DEPRECATED_UUID_KEY + "Least";
            root.setLong("DragonUUIDLeast", root.getLong(least));
            root.removeTag(least);
        }
        if (root.hasKey("OwnerName", 8)) {
            String name = root.getString("OwnerName");
            if (StringUtils.isBlank(name)) return null;
            root.setUniqueId("Owner", updateGameProfile(new GameProfile(null, name)).getId());
        }
        return null;
    }

    /**
     * Called when the ItemStack is right clicked by the player
     * <p> If player is sneaking, clear the tag compound. else, open the whistle gui with given id
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        NBTTagCompound nbt;
        if (world.isRemote) {
            if (!stack.hasTagCompound() || !(nbt = stack.getTagCompound()).hasUniqueId(DRAGON_UUID_KEY)) {
                player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.whistle.empty"), true);
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
            if (nbt.hasUniqueId("Owner") && !player.getUniqueID().equals(nbt.getUniqueId("Owner"))) {
                player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.dragon.notOwner"), true);
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
            openDragonWhistleGui(nbt.getUniqueId(DRAGON_UUID_KEY), world, hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        if (!stack.hasTagCompound() || !(nbt = stack.getTagCompound()).hasUniqueId(DRAGON_UUID_KEY) || (
                nbt.hasUniqueId("Owner") && !player.getUniqueID().equals(nbt.getUniqueId("Owner"))
        )) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World level, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound root = stack.getTagCompound();
        if (root != null && root.hasUniqueId(DRAGON_UUID_KEY)) {
            if (root.hasKey("Name")) {
                tooltip.add(I18n.format("tooltip.dragonmounts.name", root.getString("Name")));
            } else if (root.hasKey("Type")) {
                DragonType type = DragonType.REGISTRY.getIfPresent(new ResourceLocation(root.getString("Type")));
                if (type != null) {
                    tooltip.add(I18n.format(
                            "tooltip.dragonmounts.name",
                            type.formatting + ClientUtil.translateToLocal(type.translationKey) + TextFormatting.RESET)
                    );
                }
            }
            tooltip.add(I18n.format("tooltip.dragonmounts.age", TextFormatting.AQUA + ClientUtil.translateToLocal(root.getString("Age")) + TextFormatting.RESET));
            tooltip.add(I18n.format("tooltip.dragonmounts.owner", TextFormatting.GOLD + root.getString("OwnerName") + TextFormatting.RESET));
        }
        tooltip.add(ClientUtil.translateToLocal("tooltip.dragonmounts.whistle"));
    }
}