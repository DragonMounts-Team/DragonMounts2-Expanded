package com.TheRPGAdventurer.ROTD.objects.items;

import com.TheRPGAdventurer.ROTD.DragonMounts;
import com.TheRPGAdventurer.ROTD.inits.DMItemGroups;
import com.TheRPGAdventurer.ROTD.util.DMUtils;
import com.TheRPGAdventurer.ROTD.util.IHasModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

public class ItemDragonScales extends Item implements IHasModel {
    private static final EnumMap<EnumItemBreedTypes, ItemDragonScales> INSTANCES = new EnumMap<>(EnumItemBreedTypes.class);
    public EnumItemBreedTypes type;

    public static ItemDragonScales byBreed(EnumItemBreedTypes type) {
        return INSTANCES.get(type);
    }

    public static Collection<ItemDragonScales> getInstances() {
        return INSTANCES.values();
    }

    public ItemDragonScales(String name, EnumItemBreedTypes type) {
        if (INSTANCES.containsKey(type)) {
            throw new IllegalArgumentException("Duplicate breed type: " + type);
        }
        this.setTranslationKey("dragonscales");
        this.setRegistryName(name);
        this.setCreativeTab(DMItemGroups.MAIN);
        this.setMaxStackSize(64);
        this.type=type;
        INSTANCES.put(type, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(type.color + DMUtils.translateToLocal(type.translationKey));
    }

    @Override
    public void RegisterModels() {
        DragonMounts.proxy.registerItemRenderer(this, 0, "inventory");
    }
}
