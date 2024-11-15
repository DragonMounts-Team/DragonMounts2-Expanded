package top.dragonmounts.objects.items;

import top.dragonmounts.DragonMounts;
import top.dragonmounts.DragonMountsTags;
import top.dragonmounts.inits.ModItems;
import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import top.dragonmounts.objects.items.entity.EntityDragonAmulet;
import top.dragonmounts.util.IHasModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Dragon Amulet Item for the use of carrying dragons in an item <p>
 *
 * Temporary Class made in order to "datafix" old amulets.
 * This Class checks for the old amulet item use, place the dragon and then replace it with the new amulet.
 * TODO Remove this entire class at some point...
 * @author WolfShotz
 * @deprecated
 */
public class ItemDragonAmulet extends Item implements IHasModel {
	EnumItemBreedTypes type;

	public ItemDragonAmulet(EnumItemBreedTypes type, EnumDragonBreed breed) {
         this.type = type;
         this.setTranslationKey("dragon_amulet");
		this.setRegistryName(DragonMountsTags.MOD_ID, type.identifier + "_dragon_amulet");
         this.setMaxStackSize(1);
        
        ModItems.ITEMS.add(this);
    }
    
    /**
     * Called when the player has right clicked the itemstack on the ground
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	if (world.isRemote || !player.isServerWorld()) return EnumActionResult.FAIL;
    	ItemStack stack = player.getHeldItem(hand);
    	if (!stack.hasTagCompound()) return EnumActionResult.FAIL;
    	
    	EntityTameableDragon entityDragon = new EntityTameableDragon(world);
    	entityDragon.readFromNBT(stack.getTagCompound());
    	
//    	if (entityDragon.isTamedFor(player)) {
    		BlockPos blockPos = pos.offset(facing);
    		entityDragon.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    		stack.setTagCompound(null);
    		player.setHeldItem(hand, new ItemStack(ModItems.Amulet));
    		world.spawnEntity(entityDragon);
    		return EnumActionResult.SUCCESS;
//    	} else player.sendStatusMessage(new TextComponentTranslation("item.whistle.notOwned"), true);
//    	return EnumActionResult.FAIL;
    }

	/* INDESTRUCTIBLE */

	@Nonnull
	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityDragonAmulet(world, location, itemstack);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) { return true; }

	@Override
	public void RegisterModels() {
		DragonMounts.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
