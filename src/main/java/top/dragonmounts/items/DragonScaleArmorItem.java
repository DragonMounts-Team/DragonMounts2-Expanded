package top.dragonmounts.items;

import top.dragonmounts.DragonMounts;
import top.dragonmounts.api.IArmorEffectSource;
import top.dragonmounts.api.IDescribableArmorEffect;
import top.dragonmounts.capability.IArmorEffectManager;
import top.dragonmounts.compat.CooldownOverlayCompat;
import top.dragonmounts.inits.DMArmors;
import top.dragonmounts.objects.items.EnumItemBreedTypes;
import top.dragonmounts.registry.CooldownCategory;
import top.dragonmounts.util.DMUtils;
import top.dragonmounts.util.IHasModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static top.dragonmounts.DragonMounts.makeId;

public class DragonScaleArmorItem extends ItemArmor implements IHasModel, IArmorEffectSource {
	private final EnumItemBreedTypes type;
	public final IDescribableArmorEffect effect;

	public DragonScaleArmorItem(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot slot, String unlocalizedName, EnumItemBreedTypes type, IDescribableArmorEffect effect) {
		super(materialIn, renderIndexIn, slot);
		this.effect = effect;
		setTranslationKey("dragonscale_" + slot.getName());
		setRegistryName(makeId(unlocalizedName));
		this.type = type;
		DMArmors.ARMOR.add(this);
		if (effect instanceof CooldownCategory) {
			CooldownOverlayCompat.register((CooldownCategory) effect, this);
		}
	}

	@Deprecated
	protected boolean isActive(Potion effects, EntityPlayer player) {
		return !player.isPotionActive(effects);
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
		tooltip.add(type.color + DMUtils.translateToLocal(type.translationKey));
		if (this.effect != null) {
			this.effect.appendHoverText(stack, tooltip, flag);
		}
	}
	
	@Override
	public void RegisterModels() {
		DragonMounts.proxy.registerItemRenderer(this, 0, "inventory");
	}

	/**
	 * This method determines where the item is displayed
	 */
	@Override
	public @Nonnull CreativeTabs[] getCreativeTabs() {
		return new CreativeTabs[]{DragonMounts.armoryTab};
	}

	@Override
	protected boolean isInCreativeTab(CreativeTabs targetTab) {
		for (CreativeTabs tab : this.getCreativeTabs()) {
			if (tab == targetTab) return true;
		}
		return targetTab == CreativeTabs.SEARCH;
	}
}
