from Core.Util import ResourceLocation, ItemStack, makeId, cast2Ingredient
from Core.Criterion import InventoryChangedCriterion as has
from Core.RecipeBuilder import ShapedRecipeBuilder as shaped, ShaplessRecipeBuilder as shapeless
from Core.Output import Output
from ItemBreedType import ItemBreedType
from CarriageType import CarriageType

def generateRecipes(output: Output):
  ingot = {
    'iron': cast2Ingredient('iron_ingot'),
    'gold': cast2Ingredient('gold_ingot'),
    'emerald': cast2Ingredient('emerald'),
    'diamond': cast2Ingredient('diamond')
  }
  stick = cast2Ingredient('stick')
  stringItem = cast2Ingredient('string')
  cobblestone = cast2Ingredient('cobblestone')
  enderPearl = cast2Ingredient('ender_pearl')
  redstone = cast2Ingredient('redstone')
  leather = cast2Ingredient('leather')
  hasLeather = has('leather')
  hasDiamond = has('diamond')
  hasEnderPearl = has('ender_pearl')
  dragonScaleBows = []
  for breed in ItemBreedType:
    if (breed is ItemBreedType.SKELETON or breed is ItemBreedType.WITHER): continue
    base = breed.value
    dragonScales = base.withSuffix('_dragonscales')
    unlock = has(dragonScales)
    shaped(base.withSuffix('_dragon_axe'))\
      .define('X', dragonScales)\
      .define('#', stick)\
      .pattern('XX')\
      .pattern('X#')\
      .pattern(' #')\
      .groupBy('dragon_scale_axe')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'combat')
    shaped(base.withSuffix('_dragon_hoe'))\
      .define('X', dragonScales)\
      .define('#', stick)\
      .pattern('XX')\
      .pattern(' #')\
      .pattern(' #')\
      .groupBy('dragon_scale_hoe')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'tools')
    shaped(base.withSuffix('_dragon_pickaxe'))\
      .define('X', dragonScales)\
      .define('#', stick)\
      .pattern('XXX')\
      .pattern(' # ')\
      .pattern(' # ')\
      .groupBy('dragon_scale_pickaxe')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'tools')
    shaped(base.withSuffix('_dragon_shovel'))\
      .define('X', dragonScales)\
      .define('#', stick)\
      .pattern('X')\
      .pattern('#')\
      .pattern('#')\
      .groupBy('dragon_scale_shovel')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'tools')
    shaped(base.withSuffix('_dragon_sword'))\
      .define('X', dragonScales)\
      .define('#', stick)\
      .pattern('X')\
      .pattern('X')\
      .pattern('#')\
      .groupBy('dragon_scale_sword')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'combat')
    shaped(base.withSuffix('_dragonscale_boots'))\
      .define('#', dragonScales)\
      .pattern('# #')\
      .pattern('# #')\
      .groupBy('dragon_scale_boots')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'combat')
    shaped(base.withSuffix('_dragonscale_cap'))\
      .define('#', dragonScales)\
      .pattern('###')\
      .pattern('# #')\
      .groupBy('dragon_scale_helmet')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'combat')
    shaped(base.withSuffix('_dragonscale_leggings'))\
      .define('#', dragonScales)\
      .pattern('###')\
      .pattern('# #')\
      .pattern('# #')\
      .groupBy('dragon_scale_leggings')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'combat')
    shaped(base.withSuffix('_dragonscale_tunic'))\
      .define('#', dragonScales)\
      .pattern('###')\
      .pattern('# #')\
      .pattern('# #')\
      .groupBy('dragon_scale_chestplate')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'combat')
    if (breed is ItemBreedType.ENDER):
      base = makeId('end')
    bow = base.withPrefix('dragon_bow_')
    dragonScaleBows.append(bow)
    shaped(bow)\
      .define('#', dragonScales)\
      .define('X', stringItem)\
      .pattern(' #X')\
      .pattern('# X')\
      .pattern(' #X')\
      .groupBy('dragon_scale_bow')\
      .unlockedBy('has_scales', unlock)\
      .save(output, 'combat')
    if (breed is ItemBreedType.NETHER2 or breed is ItemBreedType.STORM2):
      name = 'dragon_shield_' + breed.value.path[:-1]
      shaped(makeId(name))\
        .define('W', dragonScales)\
        .define('o', ingot['iron'])\
        .pattern('WoW')\
        .pattern('WWW')\
        .pattern(' W ')\
        .groupBy('dragon_scale_shield')\
        .unlockedBy('has_scales', unlock)\
        .save(output, 'combat', name + '2')
    else:
      shaped(base.withPrefix('dragon_shield_'))\
        .define('W', dragonScales)\
        .define('o', ingot['iron'])\
        .pattern('WoW')\
        .pattern('WWW')\
        .pattern(' W ')\
        .groupBy('dragon_scale_shield')\
        .unlockedBy('has_scales', unlock)\
        .save(output, 'combat')
  for carriageType in CarriageType:
    shaped(makeId('carriage_' + carriageType.name))\
      .define('X', leather)\
      .define('#', ItemStack('planks', 1, carriageType.value))\
      .pattern('X X')\
      .pattern('###')\
      .groupBy('carriage')\
      .unlockedBy('has_leather', hasLeather)\
      .save(output, 'transportation')
  shaped(makeId('dragon_amulet'))\
    .define('Y', cobblestone)\
    .define('#', stringItem)\
    .define('X', enderPearl)\
    .pattern(' Y ')\
    .pattern('#X#')\
    .pattern(' # ')\
    .unlockedBy('has_pearl', hasEnderPearl)\
    .save(output, 'misc')
  shaped(makeId('dragon_whistle'))\
    .define('P', enderPearl)\
    .define('#', stick)\
    .define('X', stringItem)\
    .pattern('P#')\
    .pattern('#X')\
    .unlockedBy('has_pearl', hasEnderPearl)\
    .save(output, 'misc')
  shaped(makeId('diamond_shears'))\
    .define('X', ingot['diamond'])\
    .pattern(' X')\
    .pattern('X ')\
    .unlockedBy('has_diamond', hasDiamond)\
    .save(output, 'combat')
  shaped(makeId('dragon_gender'))\
    .define('W', 'emerald')\
    .define('o', 'gold_ingot')\
    .define('z', ingot['diamond'])\
    .define('r', redstone)\
    .pattern('roW')\
    .pattern('zrW')\
    .pattern(' z ')\
    .unlockedBy('has_diamond', hasDiamond)\
    .save(output, 'misc')
  shaped('dispenser')\
    .define('R', redstone)\
    .define('#', cobblestone)\
    .define('X', dragonScaleBows)\
    .pattern('###')\
    .pattern('#X#')\
    .pattern('#R#')\
    .unlockedBy('has_bow', has(dragonScaleBows))\
    .save(output, 'redstone')
  for material in ['diamond', 'emerald', 'gold', 'iron']:
    block = ResourceLocation(material + '_block')
    shaped(makeId('dragonarmor_' + material))\
      .groupBy('dragon_armor')\
      .define('#', ingot[material])\
      .define('X', block)\
      .pattern('X #')\
      .pattern(' XX')\
      .pattern('## ')\
      .unlockedBy('has_block', has(block))\
      .save(output, 'combat')
  shaped('saddle')\
    .define('#', ingot['iron'])\
    .define('X', leather)\
    .pattern('XXX')\
    .pattern('X#X')\
    .save(output, 'redstone', 'easter_egg')
  shaped(makeId('pileofsticks'))\
    .define('#', stick)\
    .pattern('###')\
    .pattern('###')\
    .pattern('###')\
    .unlockedBy('has_stick', has('stick'))\
    .save(output, 'building_blocks')