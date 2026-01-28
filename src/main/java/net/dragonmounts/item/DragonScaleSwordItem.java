package net.dragonmounts.item;

import com.google.common.collect.Multimap;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.registry.DragonType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;
import static net.dragonmounts.util.ItemUtil.isInCreativeInventory;

public class DragonScaleSwordItem extends ItemSword {
    public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "dragon_scale_sword";
    public final DragonType type;

    public DragonScaleSwordItem(ToolMaterial material, DragonType type) {
        super(material);
        this.type = type;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add(this.type.getDisplayName());
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers(slot);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            String name = SharedMonsterAttributes.ATTACK_SPEED.getName();
            modifiers.removeAll(name);
            modifiers.put(name, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.0, 0));
        }
        return modifiers;
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
