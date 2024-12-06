package net.dragonmounts.objects.items;

import com.mojang.authlib.GameProfile;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.client.gui.GuiDragonWhistle;
import net.dragonmounts.compat.DragonTypeCompat;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.inits.ModItems;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.DMUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import static net.minecraft.util.text.translation.I18n.translateToLocal;

/**
 * Dragon Whistle Item for controlling certain dragon behaviour remotely.
 *
 * @author TheRPGAdventurer
 * @modifier WolfShotz
 */
public class ItemDragonWhistle extends Item {
    public final String DRAGON_UUID_KEY = DragonMountsTags.MOD_ID + "dragon";

    public static String getDragonName(NBTTagCompound tag) {
        if (tag.hasKey("Name")) return tag.getString("Name");
        if (tag.hasKey("Type")) {
            DragonType type = DragonType.REGISTRY.getValue(new ResourceLocation(tag.getString("Type")));
            return type == null ? null : type.formatting + translateToLocal(type.translationKey) + TextFormatting.RESET;
        }
        return null;
    }

    public ItemDragonWhistle() {
        this.setTranslationKey("dragon_whistle");
        this.setRegistryName(DragonMountsTags.MOD_ID, "dragon_whistle");
        this.setMaxStackSize(1);
        this.setCreativeTab(DMItemGroups.MAIN);
        ModItems.ITEMS.add(this);
    }

    /**
     * compat
     */
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) return null;
        if (root.hasKey("Breed")) {
            root.setString("Type", DragonTypeCompat.MAPPING.getOrDefault(root.getString("Breed"), DragonTypes.ENDER).identifier.toString());
            root.removeTag("Breed");
        }
        if (root.hasUniqueId("Owner")) return null;
        if (root.hasKey("OwnerName", 8)) {
            String name = root.getString("OwnerName");
            if (StringUtils.isBlank(name)) return null;
            root.setUniqueId("Owner", updateGameProfile(new GameProfile(null, name)).getId());
        }
        return null;
    }

    /**
     * Open Dragon Whistle gui for dragon with given uuid
     *
     * @param uuid
     * @param world
     */
    @SideOnly(Side.CLIENT)
    private void openDragonWhistleGui(UUID uuid, World world) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiDragonWhistle(world, uuid));
    }


    /**
     * Called when the player right clicks the dragon
     * <p> Registers dragon id as well as cosmetic keys to the whistle
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        if (target.world.isRemote) return false;
        if (target instanceof EntityTameableDragon) {
            EntityTameableDragon dragon = (EntityTameableDragon) target;
            EntityLivingBase owner = dragon.getOwner();
            if (owner != null && dragon.isAllowed(player)) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setUniqueId(DRAGON_UUID_KEY, dragon.getUniqueID());
                if (dragon.hasCustomName()) {
                    nbt.setString("Name", dragon.getCustomNameTag());
                }
                nbt.setString("Age", "dragon." + dragon.getLifeStageHelper().getLifeStage().name().toLowerCase());
                nbt.setString("OwnerName", owner.getName());
                nbt.setUniqueId("Owner", owner.getUniqueID());
                DragonType type = dragon.getVariant().type;
                nbt.setInteger("Color", type.color);
                nbt.setString("Type", type.identifier.toString());

                stack.setTagCompound(nbt);
                player.sendStatusMessage(new TextComponentTranslation("whistle.msg.hasDragon"), true);
                return true;
            }
        }

        return false;
    }

    /**
     * Called when the ItemStack is right clicked by the player
     * <p> If player is sneaking, clear the tag compound. else, open the whistle gui with given id
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasUniqueId(DRAGON_UUID_KEY)) {
            player.sendStatusMessage(new TextComponentTranslation("whistle.msg.unBound"), true);
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasUniqueId("Owner")) {
            if (!player.getUniqueID().equals(nbt.getUniqueId("Owner"))) {
                player.sendStatusMessage(new TextComponentTranslation("dragon.notOwned"), true);
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
        }
        if (player.isSneaking()) {
            stack.setTagCompound(null);
            player.swingArm(hand);
            player.sendStatusMessage(new TextComponentTranslation("whistle.msg.cleared"), true);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        this.openDragonWhistleGui(stack.getTagCompound().getUniqueId(DRAGON_UUID_KEY), world);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    /*=== Item Extras ===*/
    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasUniqueId(DRAGON_UUID_KEY)) {
            String name = getDragonName(nbt);
            if (name != null) return I18n.format("tooltip.dragonmounts.whistle.name", name);
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasUniqueId(DRAGON_UUID_KEY)) {
            tooltip.add(DMUtils.translateToLocal("item.whistle.info"));
            return;
        }
        String name = getDragonName(nbt);
        if (name != null) {
            tooltip.add(I18n.format("tooltip.dragonmounts.name", getDragonName(nbt)));
        }
        tooltip.add(I18n.format("tooltip.dragonmounts.age", TextFormatting.AQUA + DMUtils.translateToLocal(nbt.getString("Age")) + TextFormatting.RESET));
        tooltip.add(I18n.format("tooltip.dragonmounts.owner", TextFormatting.GOLD + nbt.getString("OwnerName") + TextFormatting.RESET));
        tooltip.add(TextFormatting.ITALIC + DMUtils.translateToLocal("item.removeNBT"));
    }
}