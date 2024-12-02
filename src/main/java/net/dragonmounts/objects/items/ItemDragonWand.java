package net.dragonmounts.objects.items;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.inits.ModItems;
import net.dragonmounts.util.IHasModel;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemDragonWand extends Item implements IHasModel
{
	
	public ItemDragonWand(String name) {
		this.setTranslationKey(name);
		this.setRegistryName(new ResourceLocation(DragonMountsTags.MOD_ID, name));
		this.setMaxStackSize(1);
	//	this.setCreativeTab(DragonMounts.TAB);
		
		ModItems.ITEMS.add(this);
	}

	@Override
	public void RegisterModels()
	{
		DragonMounts.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
