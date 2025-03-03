package net.dragonmounts.item;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.DMEntities;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

public class DragonSpawnEggItem extends ItemMonsterPlacer implements IEntityContainer<Entity> {
    public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "dragon_spawn_egg";
    public final DragonType type;
    public final int backgroundColor;
    public final int highlightColor;

    public DragonSpawnEggItem(final DragonType type, int background, int highlight) {
        this.type = type;
        this.setCreativeTab(DMItemGroups.ITEMS);
        this.backgroundColor = background;
        this.highlightColor = highlight;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World level, BlockPos clicked, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos pos = clicked.offset(facing);
        if (!player.canPlayerEdit(pos, facing, stack)) return EnumActionResult.FAIL;
        if (!(level instanceof WorldServer)) return EnumActionResult.SUCCESS;
        IBlockState state = level.getBlockState(clicked);
        if (state.getBlock() == Blocks.MOB_SPAWNER) {
            TileEntity block = level.getTileEntity(clicked);
            if (block instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) block).getSpawnerBaseLogic().setEntityId(getEntityTypeFrom(stack));
                block.markDirty();
                level.notifyBlockUpdate(clicked, state, state, 3);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        if (this.loadEntity((WorldServer) level, stack, player, pos, true, null) != null) {
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("ConstantValue")
    public ActionResult<ItemStack> onItemRightClick(World level, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!(level instanceof WorldServer)) return new ActionResult<>(EnumActionResult.PASS, stack);
        RayTraceResult hit = this.rayTrace(level, player, true);
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK)
            return new ActionResult<>(EnumActionResult.PASS, stack);
        BlockPos pos = hit.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof BlockLiquid))
            return new ActionResult<>(EnumActionResult.PASS, stack);
        if (!level.isBlockModifiable(player, pos) || !player.canPlayerEdit(pos, hit.sideHit, stack))
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        if (this.loadEntity((WorldServer) level, stack, player, pos, false, null) == null)
            return new ActionResult<>(EnumActionResult.PASS, stack);
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        player.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public ItemStack saveEntity(Entity entity) {
        return new ItemStack(this);
    }

    @Nullable
    @Override
    public Entity loadEntity(WorldServer level, ItemStack stack, @Nullable EntityPlayer player, BlockPos pos, boolean yOffset, @Nullable String feedback) {
        ResourceLocation identifier = getEntityTypeFrom(stack);
        Entity entity;
        if (DMEntities.DRAGON_ID.equals(identifier)) {
            ServerDragonEntity dragon = new ServerDragonEntity(level);
            dragon.setVariant(this.type.variants.draw(level.rand, null));
            dragon.lifeStageHelper.setLifeStage(player != null && player.isSneaking()
                    ? DragonLifeStage.HATCHLING
                    : DragonLifeStage.ADULT
            );
            entity = dragon;
        } else if (EntityList.ENTITY_EGGS.containsKey(identifier)) {
            entity = EntityList.createEntityByIDFromName(identifier, level);
            if (entity == null) return null;
        } else return null;
        if (!EntityUtil.finalizeSpawn(level, entity, pos, true, null)) return null;
        if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
            entity.setCustomNameTag(stack.getDisplayName());
        }
        applyItemEntityDataToEntity(level, player, stack, entity);
        return entity;
    }

    @Override
    public Class<Entity> getContentType() {
        return Entity.class;
    }

    @Override
    public boolean isEmpty(@Nullable NBTTagCompound tag) {
        return false;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(this.type.getName());
    }

    @Nonnull
    public static ResourceLocation getEntityTypeFrom(ItemStack stack) {
        ResourceLocation identifier = getNamedIdFrom(stack);
        return identifier == null ? DMEntities.DRAGON_ID : identifier;
    }
}
