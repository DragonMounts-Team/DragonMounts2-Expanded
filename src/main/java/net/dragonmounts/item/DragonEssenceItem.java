package net.dragonmounts.item;

import net.dragonmounts.compat.FixerCompat;
import net.dragonmounts.compat.data.DragonEntityFixer;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

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
        Entity entity = this.loadEntity((WorldServer) level, stack, player, clicked.offset(facing));
        if (entity == null) return EnumActionResult.FAIL;
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ItemStack saveEntity(TameableDragonEntity dragon) {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound data = IEntityContainer.simplifyData(dragon.writeToNBT(new NBTTagCompound()).copy());
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
    public ServerDragonEntity loadEntity(WorldServer level, ItemStack stack, @Nullable EntityPlayer player, BlockPos pos) {
        return EntityUtil.spawnDragonFormStack(level, stack, player, pos, this.type, (world, entity) -> {
            entity.lifeStageHelper.setLifeStage(DragonLifeStage.HATCHLING);
            world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ILLAGER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1, 1);
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(this.type.getName());
        NBTTagCompound root = stack.getTagCompound();
        if (root == null || !root.hasKey("EntityTag")) {
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

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound cap) {
        NBTTagCompound root = stack.getTagCompound();
        if (root != null && !root.hasKey("EntityTag") && root.hasKey("Breed", 8)) {
            root.removeTag("UUIDMost");
            root.removeTag("UUIDLeast");
            root.removeTag("Health");
            root.removeTag("TicksSinceCreation");
            FixerCompat.disableEntityFixers(root);
            stack.setTagCompound(DragonEntityFixer.fixContainerItem(root));
        }
        return null;
    }
}
