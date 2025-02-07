package net.dragonmounts.item;

import net.dragonmounts.api.IArmorEffectSource;
import net.dragonmounts.api.IDescribableArmorEffect;
import net.dragonmounts.capability.IArmorEffectManager;
import net.dragonmounts.compat.CooldownOverlayCompat;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DragonScaleArmorItem extends ItemArmor implements IArmorEffectSource {
	private final DragonType type;
	public final IDescribableArmorEffect effect;

	public DragonScaleArmorItem(ArmorMaterial material, int index, EntityEquipmentSlot slot, DragonType type, IDescribableArmorEffect effect) {
		super(material, index, slot);
		this.effect = effect;
		this.type = type;
		if (effect instanceof CooldownCategory) {
			CooldownOverlayCompat.register((CooldownCategory) effect, this);
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
		tooltip.add(this.type.getName());
		if (this.effect != null) {
			this.effect.appendHoverText(stack, tooltip, flag);
		}
	}

	@Override
	public @Nonnull CreativeTabs[] getCreativeTabs() {
		return new CreativeTabs[]{DMItemGroups.COMBAT};
	}

	@Override
	protected boolean isInCreativeTab(CreativeTabs targetTab) {
		for (CreativeTabs tab : this.getCreativeTabs()) {
			if (tab == targetTab) return true;
		}
		return targetTab == CreativeTabs.SEARCH;
	}
}
