package net.dragonmounts.item;

import net.dragonmounts.block.DragonHeadBlock;
import net.dragonmounts.registry.DragonVariant;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.block.BlockHorizontal.FACING;
import static net.minecraft.block.BlockStandingSign.ROTATION;

public class DragonHeadItem extends Item {
    public static final IBehaviorDispenseItem DISPENSER_BEHAVIOR = new Bootstrap.BehaviorDispenseOptional() {
        @Override
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            this.successful = !ItemArmor.dispenseArmor(source, stack).isEmpty();
            return stack;
        }
    };
    public final DragonVariant variant;
    private String translationKey;

    public DragonHeadItem(DragonVariant variant) {
        this.variant = variant;
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DISPENSER_BEHAVIOR);
    }

    @Nullable
    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.HEAD;
    }

    public EnumActionResult onItemUse(EntityPlayer player, World level, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = level.getBlockState(pos);
        boolean flag = state.getBlock().isReplaceable(level, pos);
        if (facing == EnumFacing.DOWN || (!state.getMaterial().isSolid() && !flag) || (flag && facing != EnumFacing.UP))
            return EnumActionResult.FAIL;
        pos = pos.offset(facing);
        ItemStack stack = player.getHeldItem(hand);
        DragonHeadBlock.Holder holder = this.variant.head;
        if (player.canPlayerEdit(pos, facing, stack) && holder.standing.canPlaceBlockAt(level, pos)) {
            if (level.isRemote) return EnumActionResult.SUCCESS;
            pos = flag ? pos.down() : pos;
            if (facing == EnumFacing.UP) {
                level.setBlockState(pos, holder.standing.getDefaultState().withProperty(ROTATION, MathHelper.floor(player.rotationYaw / 22.5F + 0.5F) & 15), 11);
            } else {
                level.setBlockState(pos, holder.wall.getDefaultState().withProperty(FACING, facing), 11);
            }
            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
            }
            stack.shrink(1);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(this.variant.type.getName());
    }

    @Override
    public DragonHeadItem setTranslationKey(String translationKey) {
        this.translationKey = "tile." + translationKey;
        return this;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.translationKey;
    }
}
