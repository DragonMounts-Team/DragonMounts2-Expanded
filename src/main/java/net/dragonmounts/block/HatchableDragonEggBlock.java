package net.dragonmounts.block;

import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class HatchableDragonEggBlock extends BlockDragonEgg {
    public static TameableDragonEntity spawn(World level, BlockPos pos, DragonType type) {
        level.setBlockToAir(pos);
        TameableDragonEntity egg = new TameableDragonEntity(level);//TODO: use HatchableDragonEggEntity
        egg.setVariant(type.variants.draw(level.rand, null));
        egg.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        egg.getLifeStageHelper().setLifeStage(DragonLifeStage.EGG);
        level.spawnEntity(egg);
        return egg;
    }

    public final DragonType type;

    public HatchableDragonEggBlock(DragonType type) {
        this.type = type;
        this.setSoundType(SoundType.STONE)
                .setTranslationKey("dragonEgg")
                .setHardness(0)
                .setResistance(30)
                .setLightLevel(0.125F)
                .setCreativeTab(DMItemGroups.MAIN);
    }

    @Override
    public void onBlockClicked(World level, BlockPos pos, EntityPlayer player) {
        if (DimensionType.THE_END.equals(level.provider.getDimensionType())) {
            super.onBlockClicked(level, pos, player);
        }
    }

    @Override
    public boolean onBlockActivated(World level, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (DimensionType.THE_END.equals(level.provider.getDimensionType())) {
            if (!level.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation("egg.cantHatchEnd.DragonMounts"), true);
            }
            return false;
        }
        if (level.isRemote) {
            level.playSound(player, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.PLAYERS, 1, 1);
            return true;
        }
        spawn(level, pos, this.getDragonType(this.getMetaFromState(state))).getReproductionHelper().setBreeder(player);
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(this.getDragonType(stack.getMetadata()).getName());
    }

    public DragonType getDragonType(int meta) {
        return this.type;
    }
}
