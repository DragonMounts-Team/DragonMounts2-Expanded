package net.dragonmounts.objects.items;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dragonmounts.block.BlockDragonBreedEgg;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import net.dragonmounts.util.IHasModel;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class ItemDragonBreedEgg extends ItemBlock implements IHasModel {
    private static final Int2ObjectOpenHashMap<String> BREED_CACHE = new Int2ObjectOpenHashMap<>();
    
    public ItemDragonBreedEgg() {
        super(BlockDragonBreedEgg.DRAGON_BREED_EGG);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int metadata) {
        return metadata;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int meta = stack.getMetadata();
        String breed = BREED_CACHE.get(meta);
        if (breed == null) {
            breed = "entity.DragonMount." + EnumDragonBreed.byMeta(meta).getName() + ".name";
            BREED_CACHE.put(meta, breed);
        }
        return I18n.translateToLocalFormatted("item.dragonEgg.name", I18n.translateToLocal(breed));
    }

    @Override
    public void registerModel() {
        ClientUtil.registerDragonEggModel(this);
    }
}
