package net.dragonmounts.inits;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.item.CraftableBlockItem;
import net.dragonmounts.items.ItemDragonOrb;
import net.dragonmounts.items.ItemTestRunner;
import net.dragonmounts.objects.entity.entitycarriage.EntityCarriage;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import net.dragonmounts.objects.items.*;
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
    public static final Item DRAGON_EGG = new ItemDragonBreedEgg().setRegistryName("dragon_egg");

    //Essence Start
    public static final ItemDragonEssence EssenceForest = new ItemDragonEssence(EnumItemBreedTypes.FOREST, EnumDragonBreed.FOREST);
    public static final ItemDragonEssence EssenceAether = new ItemDragonEssence(EnumItemBreedTypes.AETHER, EnumDragonBreed.AETHER);
    public static final ItemDragonEssence EssenceFire = new ItemDragonEssence(EnumItemBreedTypes.FIRE, EnumDragonBreed.FIRE);
    public static final ItemDragonEssence EssenceIce = new ItemDragonEssence(EnumItemBreedTypes.ICE, EnumDragonBreed.ICE);
    public static final ItemDragonEssence EssenceWater = new ItemDragonEssence(EnumItemBreedTypes.WATER, EnumDragonBreed.SYLPHID);
    public static final ItemDragonEssence EssenceSkeleton = new ItemDragonEssence(EnumItemBreedTypes.SKELETON, EnumDragonBreed.SKELETON);
    public static final ItemDragonEssence EssenceWither = new ItemDragonEssence(EnumItemBreedTypes.WITHER, EnumDragonBreed.WITHER);
    public static final ItemDragonEssence EssenceEnd = new ItemDragonEssence(EnumItemBreedTypes.END, EnumDragonBreed.END);
    public static final ItemDragonEssence EssenceNether = new ItemDragonEssence(EnumItemBreedTypes.NETHER, EnumDragonBreed.NETHER);
    public static final ItemDragonEssence EssenceEnchant = new ItemDragonEssence(EnumItemBreedTypes.ENCHANT, EnumDragonBreed.ENCHANT);
    public static final ItemDragonEssence EssenceSunlight = new ItemDragonEssence(EnumItemBreedTypes.SUNLIGHT, EnumDragonBreed.SUNLIGHT);
    public static final ItemDragonEssence EssenceStorm = new ItemDragonEssence(EnumItemBreedTypes.STORM, EnumDragonBreed.STORM);
    public static final ItemDragonEssence EssenceZombie = new ItemDragonEssence(EnumItemBreedTypes.ZOMBIE, EnumDragonBreed.ZOMBIE);
    public static final ItemDragonEssence EssenceTerra = new ItemDragonEssence(EnumItemBreedTypes.TERRA, EnumDragonBreed.TERRA);
    public static final ItemDragonEssence EssenceMoonlight = new ItemDragonEssence(EnumItemBreedTypes.MOONLIGHT, EnumDragonBreed.MOONLIGHT);
    //public static final ItemDragonEssence Essencelight = new ItemDragonEssence(EnumItemBreedTypes.LIGHT, EnumDragonBreed.LIGHT);
    //public static final ItemDragonEssence Essencedark = new ItemDragonEssence(EnumItemBreedTypes.DARK, EnumDragonBreed.DARK);
    //public static final ItemDragonEssence Essencespecter = new ItemDragonEssence(EnumItemBreedTypes.SPECTER, EnumDragonBreed.SPECTER);
    //Essence End

    //Amulets Start
    public static final ItemDragonAmulet AmuletForest = new ItemDragonAmulet(EnumItemBreedTypes.FOREST, EnumDragonBreed.FOREST);
    public static final ItemDragonAmulet AmuletAether = new ItemDragonAmulet(EnumItemBreedTypes.AETHER, EnumDragonBreed.AETHER);
    public static final ItemDragonAmulet AmuletFire = new ItemDragonAmulet(EnumItemBreedTypes.FIRE, EnumDragonBreed.FIRE);
    public static final ItemDragonAmulet AmuletIce = new ItemDragonAmulet(EnumItemBreedTypes.ICE, EnumDragonBreed.ICE);
    public static final ItemDragonAmulet AmuletWater = new ItemDragonAmulet(EnumItemBreedTypes.WATER, EnumDragonBreed.SYLPHID);
    public static final ItemDragonAmulet AmuletSkeleton = new ItemDragonAmulet(EnumItemBreedTypes.SKELETON, EnumDragonBreed.SKELETON);
    public static final ItemDragonAmulet AmuletWither = new ItemDragonAmulet(EnumItemBreedTypes.WITHER, EnumDragonBreed.WITHER);
    public static final ItemDragonAmulet AmuletEnd = new ItemDragonAmulet(EnumItemBreedTypes.END, EnumDragonBreed.END);
    public static final ItemDragonAmulet AmuletNether = new ItemDragonAmulet(EnumItemBreedTypes.NETHER, EnumDragonBreed.NETHER);
    public static final ItemDragonAmulet AmuletEnchant = new ItemDragonAmulet(EnumItemBreedTypes.ENCHANT, EnumDragonBreed.ENCHANT);
    public static final ItemDragonAmulet AmuletSunlight = new ItemDragonAmulet(EnumItemBreedTypes.SUNLIGHT, EnumDragonBreed.SUNLIGHT);
    public static final ItemDragonAmulet AmuletStorm = new ItemDragonAmulet(EnumItemBreedTypes.STORM, EnumDragonBreed.STORM);
    public static final ItemDragonAmulet AmuletZombie = new ItemDragonAmulet(EnumItemBreedTypes.ZOMBIE, EnumDragonBreed.ZOMBIE);
    public static final ItemDragonAmulet AmuletTerra = new ItemDragonAmulet(EnumItemBreedTypes.TERRA, EnumDragonBreed.TERRA);
    public static final ItemDragonAmulet AmuletMoonlight = new ItemDragonAmulet(EnumItemBreedTypes.MOONLIGHT, EnumDragonBreed.MOONLIGHT);

    public static final ItemDragonAmuletNEW Amulet = new ItemDragonAmuletNEW();

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
        DRAGON_INTERACTABLE.add(ModItems.Amulet);
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
