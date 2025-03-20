package net.dragonmounts.item;

import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.entity.CarriageEntity;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.registry.CarriageType;
import net.dragonmounts.util.math.MathX;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CarriageItem extends Item {
    public final CarriageType type;

    public CarriageItem(CarriageType type) {
        this.type = type;
        this.setCreativeTab(CreativeTabs.TRANSPORTATION);
    }

    public ActionResult<ItemStack> onItemRightClick(World level, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        float f9 = 0.017453292F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch);
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw);
        Vec3d pos = new Vec3d(
                player.prevPosX + (player.posX - player.prevPosX),
                player.prevPosY + (player.posY - player.prevPosY) + player.getEyeHeight(),
                player.prevPosZ + (player.posZ - player.prevPosZ)
        );
        float f3 = MathHelper.cos(-f2 * f9 - MathX.PI_F);
        float f4 = MathHelper.sin(-f2 * f9 - MathX.PI_F);
        float f5 = -MathHelper.cos(-f1 * f9);
        RayTraceResult hit = level.rayTraceBlocks(pos, pos.add(f4 * f5 * 5, MathHelper.sin(-f1 * f9) * 5, f3 * f5 * 5), true);
        if (hit == null) return new ActionResult<>(EnumActionResult.PASS, stack);
        Vec3d look = player.getLook(1.0F);
        boolean flag = false;
        for (Entity entity : level.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expand(look.x * 5, look.y * 5, look.z * 5).grow(1.0D))) {
            if (entity.canBeCollidedWith() && entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize()).contains(pos)) {
                flag = true;
            }
        }
        if (flag || hit.typeOfHit != RayTraceResult.Type.BLOCK) return new ActionResult<>(EnumActionResult.PASS, stack);
        CarriageEntity carriage = new CarriageEntity(level, hit.hitVec);
        if (!level.getCollisionBoxes(carriage, carriage.getEntityBoundingBox().grow(-0.1D)).isEmpty())
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        if (!level.isRemote) {
            carriage.setType(this.type);
            carriage.rotationYaw = player.rotationYaw;
            level.spawnEntity(carriage);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            player.addStat(StatList.getObjectUseStats(this));
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(TextFormatting.GRAY + ClientUtil.translateToLocal("tooltip.dragonmounts.carriage"));
    }

    @Override
    public @Nonnull CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{DMItemGroups.ITEMS};
    }

    @Override
    protected boolean isInCreativeTab(CreativeTabs targetTab) {
        for (CreativeTabs tab : this.getCreativeTabs())
            if (tab == targetTab) return true;
        return targetTab == CreativeTabs.SEARCH;
    }
}
