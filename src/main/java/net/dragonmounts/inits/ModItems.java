package net.dragonmounts.inits;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.item.CraftableBlockItem;
import net.dragonmounts.item.DragonEggCompatItem;
import net.dragonmounts.items.ItemDragonOrb;
import net.dragonmounts.items.ItemTestRunner;
import net.dragonmounts.objects.entity.entitycarriage.EntityCarriage;
import net.dragonmounts.objects.items.ItemCarriage;
import net.dragonmounts.objects.items.ItemDragonWand;
import net.dragonmounts.objects.items.ItemDragonWhistle;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class ModItems {
    public static final ReferenceOpenHashSet<Item> DRAGON_INTERACTABLE = new ReferenceOpenHashSet<>();
    public static final ReferenceOpenHashSet<Item> HARDCODED_AQUATIC_FOOD = new ReferenceOpenHashSet<>();
    public static final List<Item> ITEMS = DMItems.ITEMS;

    public static final Item DRAGON_NEST = new CraftableBlockItem(DMBlocks.DRAGON_NEST, DMItemGroups.MAIN);
    public static final Item DRAGON_CORE = new ItemBlock(DMBlocks.DRAGON_CORE).setRegistryName(DMBlocks.DRAGON_CORE.getRegistryName());
    public static final Item DRAGON_EGG = new DragonEggCompatItem().setRegistryName("dragon_egg");

    //Other Start
    public static final Item dragon_wand = new ItemDragonWand("dragon_wand");
    public static final Item dragon_whistle = new ItemDragonWhistle();
    public static final ItemDragonOrb dragon_orb = new ItemDragonOrb();
    public static final ItemTestRunner test_runner = new ItemTestRunner();
    //Other End

    //Carriages Start
    public static final Item carriage_oak = new ItemCarriage("carriage_", EntityCarriage.Type.OAK);
    public static final Item carriage_spruce = new ItemCarriage("carriage_", EntityCarriage.Type.SPRUCE);
    public static final Item carriage_birch = new ItemCarriage("carriage_", EntityCarriage.Type.BIRCH);
    public static final Item carriage_darkoak = new ItemCarriage("carriage_", EntityCarriage.Type.DARK_OAK);
    public static final Item carriage_jungle = new ItemCarriage("carriage_", EntityCarriage.Type.JUNGLE);
    public static final Item carriage_acacia = new ItemCarriage("carriage_", EntityCarriage.Type.ACACIA);
    //Carriages end

    static {
        DRAGON_INTERACTABLE.add(DMItems.diamond_shears);
        DRAGON_INTERACTABLE.add(ModItems.dragon_wand);
        DRAGON_INTERACTABLE.add(ModItems.dragon_whistle);
        DRAGON_INTERACTABLE.add(DMItems.AMULET);
        DRAGON_INTERACTABLE.add(Items.BONE);
        DRAGON_INTERACTABLE.add(Items.STICK);
        DRAGON_INTERACTABLE.add(DMItems.IRON_DRAGON_ARMOR);
        DRAGON_INTERACTABLE.add(DMItems.GOLDEN_DRAGON_ARMOR);
        DRAGON_INTERACTABLE.add(DMItems.EMERALD_DRAGON_ARMOR);
        DRAGON_INTERACTABLE.add(DMItems.DIAMOND_DRAGON_ARMOR);
        HARDCODED_AQUATIC_FOOD.add(Items.FISH);
        HARDCODED_AQUATIC_FOOD.add(Items.COOKED_FISH);
        ITEMS.add(DRAGON_CORE);
        ITEMS.add(DRAGON_NEST);
        ITEMS.add(DRAGON_EGG);
    }

    public static boolean isAquaticFood(ItemStack stack) {
        return HARDCODED_AQUATIC_FOOD.contains(stack.getItem()) || ArrayUtils.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID("listAllfishraw"));
    }
}
