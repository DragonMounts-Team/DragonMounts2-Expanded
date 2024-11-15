package top.dragonmounts.objects.items;

import top.dragonmounts.DragonMounts;
import top.dragonmounts.DragonMountsTags;
import top.dragonmounts.inits.ModItems;
import top.dragonmounts.util.IHasModel;
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
