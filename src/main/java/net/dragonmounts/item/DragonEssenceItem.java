package net.dragonmounts.item;

import net.dragonmounts.compat.DragonMountsCompat;
import net.dragonmounts.entity.EntityContainerItemEntity;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;
import static net.dragonmounts.util.EntityUtil.notOwner;

public class DragonEssenceItem extends Item implements IEntityContainer<TameableDragonEntity> {
    public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "dragon_essence";
    public final DragonType type;

    public DragonEssenceItem(DragonType type) {
        this.type = type;
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World level, BlockPos clicked, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!(level instanceof WorldServer)) return EnumActionResult.FAIL;
        ItemStack stack = player.getHeldItem(hand);
        Entity entity = this.loadEntity((WorldServer) level, stack, player, clicked.offset(facing), true, "message.dragonmounts.dragon.notOwner");
        if (entity == null) return EnumActionResult.FAIL;
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ItemStack saveEntity(TameableDragonEntity dragon) {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound data = IEntityContainer.simplifyData(dragon.writeToNBT(new NBTTagCompound()));
        data.removeTag("UUIDMost");
        data.removeTag("UUIDLeast");
        data.removeTag("Health");
        data.removeTag("TicksSinceCreation");
        root.setTag("EntityTag", data);
        ItemStack stack = new ItemStack(this);
        stack.setTagCompound(root);
        return stack;
    }

    @Nullable
    @Override
    public ServerDragonEntity loadEntity(WorldServer level, ItemStack stack, @Nullable EntityPlayer player, BlockPos pos, boolean yOffset, String feedback) {
        ServerDragonEntity dragon = new ServerDragonEntity(level);
        NBTTagCompound root = stack.getTagCompound();
        boolean flag = root == null;
        if (!flag) {
            NBTTagCompound data = root.getCompoundTag("EntityTag");
            if (!data.isEmpty()) {
                if (notOwner(data, player, feedback)) return null;
                flag = !data.hasKey(DragonVariant.DATA_PARAMETER_KEY);
                dragon.readFromNBT(data);
            }
        }
        if (flag) {
            dragon.setVariant(this.type.variants.draw(level.rand, null));
        }
        if (!EntityUtil.finalizeSpawn(level, dragon, pos, true, null)) return null;
        if (stack.hasDisplayName()) {
            dragon.setCustomNameTag(stack.getDisplayName());
        }
        ItemMonsterPlacer.applyItemEntityDataToEntity(level, player, stack, dragon);
        dragon.lifeStageHelper.setLifeStage(DragonLifeStage.HATCHLING);
        level.playSound(null, pos, SoundEvents.ENTITY_ILLAGER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1, 1);
        return dragon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(this.type.getName());
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("EntityTag")) {
            //Broken NBT, possibly cheated in, Warn the player...
            tooltips.add(TextFormatting.RED + "ERROR: Broken or Missing NBT Data");
        }
    }

    @Override
    public Class<TameableDragonEntity> getContentType() {
        return TameableDragonEntity.class;
    }

    @Override
    public boolean isEmpty(@Nullable NBTTagCompound tag) {
        return false;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        return new EntityContainerItemEntity(world, location, stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        NBTTagCompound root = stack.getTagCompound();
        if (root != null && !root.hasKey("EntityTag") && root.hasKey("Breed", 8)) {
            NBTTagCompound display = root.getCompoundTag("display");
            IEntityContainer.simplifyData(root);
            root.removeTag("display");
            root.removeTag("LocName");
            NBTTagCompound entity = root.copy();
            entity.setString("id", "dragonmounts:dragon");
            entity.removeTag("UUIDMost");
            entity.removeTag("UUIDLeast");
            entity.removeTag("Health");
            entity.removeTag("TicksSinceCreation");
            DragonMountsCompat.DRAGON_ENTITY_FIX.fixTagCompound(entity);
            root.tagMap.clear();
            root.setTag("EntityTag", entity);
            if (!display.isEmpty()) {
                root.setTag("display", display);
            }
        }
        return null;
    }
}
