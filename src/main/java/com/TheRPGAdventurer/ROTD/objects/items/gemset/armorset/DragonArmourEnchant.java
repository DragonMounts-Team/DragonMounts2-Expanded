package com.TheRPGAdventurer.ROTD.objects.items.gemset.armorset;

import java.util.Random;

import com.TheRPGAdventurer.ROTD.inits.ModArmour;
import com.TheRPGAdventurer.ROTD.objects.items.EnumItemBreedTypes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DragonArmourEnchant extends DragonArmourBase {
	
	private Random rand = new Random();
	/**Used in {@link com.TheRPGAdventurer.ROTD.objects.items.getset.armorset.DragonArmourEnchant#ArmourXPBonus ArmourXPBonus}*/
	private static boolean check = false;
	
	public DragonArmourEnchant(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String unlocalizedName) {
		super(materialIn, renderIndexIn, equipmentSlotIn, unlocalizedName, EnumItemBreedTypes.ENCHANT);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		super.onArmorTick(world, player, itemStack);
		if (head == ModArmour.enchantDragonScaleCap || chest == ModArmour.enchantDragonScaleTunic || legs == ModArmour.enchantDragonScaleLeggings || feet == ModArmour.enchantDragonScaleBoots) {
	        for (int i = -2; i <= 2; ++i)
	            for (int j = -2; j <= 2; ++j) {
	                if (i > -2 && i < 2 && j == -1) j = 2;
	                if (rand.nextInt(30) == 0)
	                    for (int k = 0; k <= 1; ++k)
	                        world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, player.posX, player.posY + rand.nextDouble() + 1.5, player.posZ, (double)((float)i + rand.nextFloat()) - 0.5D, (double)((float)k - rand.nextFloat() - 1.0F), (double)((float)j + rand.nextFloat()) - 0.5D);
	            }
		}
		if (head == ModArmour.enchantDragonScaleCap && chest == ModArmour.enchantDragonScaleTunic && legs == ModArmour.enchantDragonScaleLeggings && feet == ModArmour.enchantDragonScaleBoots)
			check = true;
	}
	
	public static class ArmourXPBonus {
		@SubscribeEvent
		public static void handleXPDrops(LivingDeathEvent event) {
			if (!check) return;
			EntityLivingBase target = event.getEntityLiving();
			if (target.world.isRemote) return;
			Entity source = event.getSource().getTrueSource();
			if (!(source instanceof EntityPlayer)) return;
			target.world.spawnEntity(new EntityXPOrb(target.world, target.posX, target.posY, target.posZ, Math.round(target.getExperiencePoints((EntityPlayer) source) * ((float) Math.log10(3 + 1) * 2))));
		}
	}
}
