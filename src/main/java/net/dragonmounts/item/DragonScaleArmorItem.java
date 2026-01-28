package net.dragonmounts.item;

import net.dragonmounts.api.IArmorEffectSource;
import net.dragonmounts.api.IDescribedArmorEffect;
import net.dragonmounts.capability.IArmorEffectManager;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.registry.CooldownCategory;
import net.dragonmounts.registry.DragonType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static net.dragonmounts.util.ItemUtil.isInCreativeInventory;

public class DragonScaleArmorItem extends ItemArmor implements IArmorEffectSource {
	private final DragonType type;
	public final IDescribedArmorEffect effect;

	public DragonScaleArmorItem(ArmorMaterial material, int index, EntityEquipmentSlot slot, DragonType type, IDescribedArmorEffect effect) {
		super(material, index, slot);
		this.effect = effect;
		this.type = type;
		if (effect instanceof CooldownCategory) {
			((CooldownCategory) effect).registerItem(this);
		}
	}

	@Override
	public void affect(IArmorEffectManager manager, EntityPlayer player, ItemStack stack) {
		if (this.effect != null) {
			manager.stackLevel(this.effect);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add(this.type.getDisplayName());
		if (this.effect != null) {
			this.effect.appendHoverText(stack, tooltip, flag);
		}
	}

	@Override
	public CreativeTabs[] getCreativeTabs() {
		return new CreativeTabs[]{DMItemGroups.COMBAT};
	}

	@Override
	protected boolean isInCreativeTab(CreativeTabs tab) {
		return isInCreativeInventory(this, tab);
	}
}
