package net.dragonmounts.objects.items;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.inits.ModItems;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.IHasModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemDragonEssence extends Item implements IHasModel {

    public EnumItemBreedTypes type;
    public EnumDragonBreed breed;

    public ItemDragonEssence(EnumItemBreedTypes type, EnumDragonBreed breed) {
        this.breed = breed;
        this.setTranslationKey("dragon_essence");
        this.setRegistryName(type.identifier + "_dragon_essence");
        this.maxStackSize = 1;
        this.type = type;

        ModItems.ITEMS.add(this);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float x, float y, float z) {
    	ItemStack stack = player.getHeldItem(hand);
        if (!stack.hasTagCompound()) return EnumActionResult.PASS;
        if (!(world instanceof WorldServer)) return EnumActionResult.SUCCESS;
        EntityTameableDragon dragon = new EntityTameableDragon(world);
        dragon.readFromNBT(stack.getTagCompound());

        if (dragon.isAllowed(player)) {
        	dragon.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
            dragon.setHealth(dragon.getMaxHealth());
            UUID uuid = dragon.getUniqueID();
            WorldServer server = (WorldServer) world;
            while (server.getEntityFromUuid(uuid) != null) {
                uuid = MathHelper.getRandomUUID(world.rand);
            }
            dragon.setUniqueId(uuid);
            world.spawnEntity(dragon);
            dragon.world.playSound(x, y, z, SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 1, 1, false);
            dragon.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x + dragon.getRNG().nextInt(5), y + dragon.getRNG().nextInt(5), z + dragon.getRNG().nextInt(5), 1, 1, 1, 0);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            return EnumActionResult.SUCCESS;
        }
        player.sendStatusMessage(new TextComponentTranslation("dragon.notOwned"), true);
        return super.onItemUse(player, world, pos, hand, facing, x, y, z);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(type.color + DMUtils.translateToLocal(type.translationKey));
        if (stack.getTagCompound() == null) {
            //Broken NBT, possibly cheated in, Warn the player...
            tooltip.add(TextFormatting.RED + "ERROR: Broken or Missing NBT Data");
        }
    }

    @Override
    public void RegisterModels() { DragonMounts.proxy.registerItemRenderer(this, 0, "inventory"); }
}