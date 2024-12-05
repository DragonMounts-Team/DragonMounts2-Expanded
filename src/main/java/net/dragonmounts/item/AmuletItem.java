package net.dragonmounts.item;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.dragonmounts.compat.DragonMountsCompat;
import net.dragonmounts.entity.EntityContainerItemEntity;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
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

import static net.dragonmounts.item.DragonSpawnEggItem.DRAGON_ID;
import static net.dragonmounts.util.EntityUtil.notOwner;

public class AmuletItem<E extends Entity> extends Item implements IEntityContainer<E>, ICapabilityProvider {
    private static final Reference2ObjectOpenHashMap<Capability<?>, Object> CAPABILITIES = new Reference2ObjectOpenHashMap<>();

    public static <T> void registerCapability(Capability<T> capability, T instance) {
        CAPABILITIES.put(capability, instance);
    }

    public final Class<E> contentType;

    public AmuletItem(Class<E> contentType) {
        this.contentType = contentType;
        this.setMaxStackSize(1);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (!this.isEmpty(stack.getTagCompound()) || !target.isEntityAlive()) return false;
        if (target instanceof EntityTameableDragon) {
            EntityTameableDragon dragon = (EntityTameableDragon) target;
            if (dragon.isOwner(player)) {
                DragonAmuletItem amulet = dragon.getVariant().type.getInstance(DragonAmuletItem.class, null);
                if (amulet == null) return false;
                if (dragon.world.isRemote) return true;
                stack.shrink(1);
                ItemStack result = amulet.saveEntity(dragon);
                if (stack.isEmpty()) {
                    player.setHeldItem(hand, result);
                } else if (!player.addItemStackToInventory(result)) {
                    player.dropItem(result, false);
                }
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

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World level, BlockPos clicked, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (level.isRemote) return EnumActionResult.FAIL;
        ItemStack stack = player.getHeldItem(hand);
        if (this.isEmpty(stack.getTagCompound())) return EnumActionResult.FAIL;
        Entity entity = this.loadEntity(level, stack, player, clicked.offset(facing), true, "dragon.notOwned");
        if (entity == null) return EnumActionResult.FAIL;
        stack.shrink(1);
        ItemStack amulet = new ItemStack(DMItems.AMULET);
        if (stack.isEmpty()) {
            player.setHeldItem(hand, amulet);
        } else if (!player.addItemStackToInventory(amulet)) {
            player.dropItem(amulet, false);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) return;
        NBTTagCompound data = root.getCompoundTag("EntityTag");
        if (data.hasKey("CustomName")) {
            tooltips.add(I18n.format("tooltip.dragonmounts.name", data.getString("CustomName")));
        } else {
            DragonType type = this.getDragonType(data);
            tooltips.add(I18n.format("tooltip.dragonmounts.name", type.formatting + DMUtils.translateBothToLocal("entity.dragonmounts.dragon", type.translationKey)));
        }
        tooltips.add(I18n.format("tooltip.dragonmounts.health", TextFormatting.GREEN.toString() + Math.round(data.getDouble("Health"))));
        if (data.hasKey("OwnerName")) {
            tooltips.add(I18n.format("tooltip.dragonmounts.owner", TextFormatting.GOLD + data.getString("OwnerName")));
        }
    }

    @Nonnull
    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        return new EntityContainerItemEntity(world, location, stack);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack saveEntity(E entity) {
        NBTTagCompound data = new NBTTagCompound();
        if (entity.writeToNBTOptional(data)) {
            NBTTagCompound root = new NBTTagCompound();
            data.removeTag("UUIDMost");
            data.removeTag("UUIDLeast");
            root.setTag("EntityTag", IEntityContainer.simplifyData(data));
            ItemStack stack = new ItemStack(this);
            stack.setTagCompound(root);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public Entity loadEntity(World level, ItemStack stack, @Nullable EntityPlayer player, BlockPos pos, boolean yOffset, @Nullable String feedback) {
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) return null;
        NBTTagCompound data = root.getCompoundTag("EntityTag");
        if (data.isEmpty() || notOwner(data, player, feedback)) return null;
        ResourceLocation identifier = DragonSpawnEggItem.getEntityTypeFrom(stack);
        Entity entity;
        if (DRAGON_ID.equals(identifier)) {
            entity = new EntityTameableDragon(level);
        } else if (EntityList.ENTITY_EGGS.containsKey(identifier)) {
            entity = EntityList.createEntityByIDFromName(identifier, level);
            if (entity == null) return null;
        } else return null;
        EntityUtil.finalizeSpawn(level, entity, pos, true, null);
        if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
            entity.setCustomNameTag(stack.getDisplayName());
        }
        ItemMonsterPlacer.applyItemEntityDataToEntity(level, player, stack, entity);
        level.playSound(null, pos, SoundEvents.ENTITY_ILLAGER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1, 1);
        return entity;
    }

    @Override
    public Class<E> getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty(@Nullable NBTTagCompound tag) {
        return tag == null || tag.getCompoundTag("EntityTag").isEmpty();
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

    @Override
    public boolean updateItemStackNBT(NBTTagCompound nbt) {
        super.updateItemStackNBT(nbt);
        if (nbt.hasKey("EntityTag")) return false;
        if (nbt.hasKey("breed", 8)) {
            NBTTagCompound display = nbt.getCompoundTag("display");
            IEntityContainer.simplifyData(nbt);
            nbt.removeTag("display");
            nbt.removeTag("LocName");
            NBTTagCompound entity = nbt.copy();
            entity.setString("id", "dragonmounts:dragon");
            DragonMountsCompat.DRAGON_ENTITY_FIX.fixTagCompound(entity);
            nbt.tagMap.clear();
            nbt.setTag("display", display);
            return true;
        }
        return false;
    }

    protected DragonType getDragonType(NBTTagCompound data) {
        return DragonVariant.byName(data.getString("Variant")).type;
    }
}
