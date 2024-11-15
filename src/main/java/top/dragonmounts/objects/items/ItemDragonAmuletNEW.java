package top.dragonmounts.objects.items;

import top.dragonmounts.DragonMountsTags;
import top.dragonmounts.inits.DMItemGroups;
import top.dragonmounts.inits.ModItems;
import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.items.entity.EntityDragonAmulet;
import top.dragonmounts.util.DMUtils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Dragon Amulet Item for the use of carrying dragons in an item
 *
 * @author WolfShotz
 */
public class ItemDragonAmuletNEW extends Item implements ICapabilityProvider {
    private static final Reference2ObjectOpenHashMap<Capability<?>, Object> CAPABILITIES = new Reference2ObjectOpenHashMap<>();

    public static <T> void registerCapability(Capability<T> capability, T instance) {
        CAPABILITIES.put(capability, instance);
    }

    public ItemDragonAmuletNEW() {
        String name = "dragon_amulet";
        this.setRegistryName(DragonMountsTags.MOD_ID, name);
        this.setTranslationKey(name);
        this.setMaxStackSize(1);
        this.setCreativeTab(DMItemGroups.MAIN);

        ModItems.ITEMS.add(this);
    }

    public static boolean containsDragonEntity(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey("breed");
    }

    /**
     * Called when the player has right clicked an entity with the itemstack
     * <p> Writes the entity NBT data to the item stack, and then sets dead
     */
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (containsDragonEntity(stack) || !target.isEntityAlive()) return false;
        if (target instanceof EntityTameableDragon) {
            EntityTameableDragon dragon = (EntityTameableDragon) target;
            if (dragon.isTamedFor(player)) {
                if (target.world.isRemote) return true;
                NBTTagCompound tag = stack.getTagCompound();
                if (tag == null) {
                    tag = new NBTTagCompound();
                    stack.setTagCompound(tag);
                }
                tag.setString("breed", dragon.getBreed().getItemBreed(dragon).identifier);
                if (dragon.hasCustomName()) {
                    stack.setStackDisplayName(dragon.getCustomNameTag());
                } else {
                    tag.setString("LocName", dragon.makeTranslationKey());
                }
                tag.setString("OwnerName", player.getName());
                target.writeToNBT(tag);
                player.setHeldItem(hand, stack);
                if (dragon.getLeashed()) dragon.clearLeashed(true, true); // Fix Lead Dupe exploit
                player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.NEUTRAL, 1, 1);
                target.setDead();
                return true;
            } else {
                player.sendStatusMessage(new TextComponentTranslation("dragon.notOwned"), true);
            }
        }
        return false;
    }

    /**
     * Called when the player has right clicked the ItemStack on a block
     * <p> Spawns an entity in the world with the given NBT data the ItemStack was storing
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return EnumActionResult.FAIL;
        ItemStack stack = player.getHeldItem(hand);
        if (!containsDragonEntity(stack)) return EnumActionResult.FAIL;
        EntityTameableDragon dragon = new EntityTameableDragon(world);
        dragon.readFromNBT(stack.getTagCompound());

        if (dragon.isAllowed(player)) {
            BlockPos blockPos = pos.offset(facing);
            dragon.setPosition(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
            stack.setTagCompound(null);
            player.setHeldItem(hand, stack);
            world.spawnEntity(dragon);
            player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ILLAGER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1, 1);
            return EnumActionResult.SUCCESS;
        } else player.sendStatusMessage(new TextComponentTranslation("dragon.notOwned"), true);
        return EnumActionResult.FAIL;
    }

    /* Item Extras */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (containsDragonEntity(stack)) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("CustomName")) {
                tooltip.add(I18n.format("tooltip.dragonmounts.name", nbt.getString("CustomName")));
            } else {
                String name = DMUtils.translateToLocal(nbt.getString("LocName"), nbt, "Name");
                String type = nbt.getString("breed");
                tooltip.add(I18n.format("tooltip.dragonmounts.name", type.isEmpty() ? name : EnumItemBreedTypes.byName(type).color + name));
            }
            tooltip.add(I18n.format("tooltip.dragonmounts.health", TextFormatting.GREEN.toString() + Math.round(nbt.getDouble("Health"))));
            tooltip.add(I18n.format("tooltip.dragonmounts.owner", TextFormatting.GOLD + nbt.getString("OwnerName")));
            tooltip.add(I18n.format("tooltip.dragonmounts.gender", nbt.getBoolean("IsMale")
                    ? TextFormatting.BLUE + "M"
                    : TextFormatting.RED + "FM"
            ));
        } else {
            tooltip.add(TextFormatting.GREEN + DMUtils.translateToLocal("item.dragonamulet.info"));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = super.getItemStackDisplayName(stack);
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) return name;
        String type = root.getString("breed");
        return type.isEmpty() ? name : EnumItemBreedTypes.byName(type).color + name;
    }

    @Nonnull
    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        return new EntityDragonAmulet(world, location, stack);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return this;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return CAPABILITIES.containsKey(capability);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return (T) CAPABILITIES.get(capability);
    }
}