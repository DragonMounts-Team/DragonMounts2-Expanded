package com.TheRPGAdventurer.ROTD.objects.items.gemset.armorset;

import com.TheRPGAdventurer.ROTD.DragonMounts;
import com.TheRPGAdventurer.ROTD.api.IArmorEffect;
import com.TheRPGAdventurer.ROTD.api.IArmorEffectSource;
import com.TheRPGAdventurer.ROTD.capability.IArmorEffectManager;
import com.TheRPGAdventurer.ROTD.inits.ModArmour;
import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;
import com.TheRPGAdventurer.ROTD.registry.CooldownCategory;
import com.TheRPGAdventurer.ROTD.util.CooldownOverlayCompat;
import com.TheRPGAdventurer.ROTD.util.DMUtils;
import com.TheRPGAdventurer.ROTD.util.IHasModel;
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

import static com.TheRPGAdventurer.ROTD.DragonMounts.makeId;

public abstract class DragonArmourBase extends ItemArmor implements IHasModel, IArmorEffectSource {
	private final EnumItemBreedTypes type;
	public final IArmorEffect effect;

	public DragonArmourBase(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String unlocalizedName, EnumItemBreedTypes type, IArmorEffect effect) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
		this.effect = effect;
		setTranslationKey("dragonscale_" + equipmentSlotIn.toString().toLowerCase());
		setRegistryName(makeId(unlocalizedName));
		this.type = type;
		ModArmour.ARMOR.add(this);
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
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(type.color + DMUtils.translateToLocal(type.translationKey));
		stack.setStackDisplayName(type.color + stack.getDisplayName());
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
		for (CreativeTabs tab : this.getCreativeTabs())
			if (tab == targetTab) return true;
		return targetTab == CreativeTabs.SEARCH;
	}
}
