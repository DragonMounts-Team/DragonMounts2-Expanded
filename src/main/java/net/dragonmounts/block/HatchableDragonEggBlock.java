package net.dragonmounts.block;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.BlockProperties;
import net.minecraft.block.BlockDragonEgg;
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

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

public class HatchableDragonEggBlock extends BlockDragonEgg {
    public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "dragon_egg";
    @Nullable
    public static TameableDragonEntity spawn(World level, BlockPos pos, DragonType type) {
        level.setBlockToAir(pos);
        TameableDragonEntity egg = new TameableDragonEntity(level);//TODO: use HatchableDragonEggEntity
        DragonVariant variant = type.variants.draw(level.rand, null);
        if (variant == null) return null;
        egg.setVariant(variant);
        egg.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        egg.getLifeStageHelper().setLifeStage(DragonLifeStage.EGG);
        level.spawnEntity(egg);
        return egg;
    }

    public final DragonType type;

    public HatchableDragonEggBlock(DragonType type, BlockProperties props) {
        this.type = type;
        this.setSoundType(props.sound)
                .setHardness(props.hardness)
                .setResistance(props.resistance)
                .setLightLevel(props.light)
                .setCreativeTab(props.creativeTab);
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
                player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.egg.wrongDimension"), true);
            }
            return false;
        }
        if (level.isRemote) {
            level.playSound(player, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.PLAYERS, 1, 1);
            return true;
        }
        TameableDragonEntity egg = spawn(level, pos, this.getDragonType(this.getMetaFromState(state)));
        if (egg != null) {
            egg.getReproductionHelper().setBreeder(player);
        }
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
