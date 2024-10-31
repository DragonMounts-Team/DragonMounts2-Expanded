package com.TheRPGAdventurer.ROTD.api;

import com.TheRPGAdventurer.ROTD.capability.ArmorEffectManager;
import com.TheRPGAdventurer.ROTD.capability.IArmorEffectManager;
import com.TheRPGAdventurer.ROTD.registry.CooldownCategory;
import com.TheRPGAdventurer.ROTD.util.DMUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface IDescribableArmorEffect extends IArmorEffect {
    @SideOnly(Side.CLIENT)
    default void appendTriggerInfo(ItemStack stack, List<String> tooltips) {
        tooltips.add(DMUtils.translateToLocal("tooltip.dragonmounts.armor_effect_piece_4"));
    }

    @SideOnly(Side.CLIENT)
    default void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {}

    class Advanced extends CooldownCategory implements IDescribableArmorEffect {
        public final int cooldown;
        public final String description;

        public Advanced(ResourceLocation identifier, int cooldown) {
            this.cooldown = cooldown;
            this.description = "tooltip.armor_effect." + identifier.getNamespace() + "." + identifier.getPath().replace('/', '.');
            this.setRegistryName(identifier);
        }

        public final void applyCooldown(IArmorEffectManager manager) {
            manager.setCooldown(this, this.cooldown);
        }

        @SideOnly(Side.CLIENT)
        public final void appendCooldownInfo(List<String> tooltips) {
            int value = ArmorEffectManager.getLocalCooldown(this);
            if (value > 0) {
                tooltips.add(DMUtils.quickFormatAsFloat("tooltip.dragonmounts.armor_effect_remaining_cooldown", value));
            } else if (this.cooldown > 0) {
                tooltips.add(DMUtils.quickFormatAsFloat("tooltip.dragonmounts.armor_effect_cooldown", this.cooldown));
            }
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {
            tooltips.add("");
            this.appendTriggerInfo(stack, tooltips);
            tooltips.add(DMUtils.translateToLocal(this.description));
            this.appendCooldownInfo(tooltips);
        }

        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            return level > 3;
        }

    }
}
