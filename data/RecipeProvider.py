from Core.Util import ResourceLocation, ItemStack, TagKey, makeId, cast2Ingredient
from Core.Criterion import InventoryChangedCriterion as has
from Core.RecipeBuilder import ShapedRecipeBuilder as shaped, ShapelessRecipeBuilder as shapeless
from Core.Output import Output
from DragonType import DragonType
from CarriageType import CarriageType
from DragonArmorMaterial import DragonArmorMaterial

def storageBlock(
  output: Output,
  item: ResourceLocation | ItemStack,
  block: ResourceLocation | ItemStack,
  item_group: str,
  block_group: str
):
  shaped(block) \
    .define('#', item) \
    .pattern('###') \
    .pattern('###') \
    .pattern('###') \
    .unlockedBy('has_item', has(item)) \
    .groupBy(block_group) \
    .save(output, 'building_blocks')
  if (isinstance(item, ItemStack)):
    item = ItemStack(item.item, 9, item.data)
  shapeless(item, 9) \
    .requires(block) \
    .unlockedBy('has_block', has(block)) \
    .groupBy(item_group) \
    .save(output, 'misc')

def generateRecipes(output: Output):
  ingot = {
    'iron': TagKey('ingotIron'),
    'gold': TagKey('ingotGold'),
    'emerald': TagKey('gemEmerald'),
    'diamond': TagKey('gemDiamond')
  }
  stick = TagKey('stickWood')
  stringItem = TagKey('string')
  cobblestone = TagKey('cobblestone')
  enderPearl = TagKey('enderpearl')
  redstone = TagKey('dustRedstone')
  leather = TagKey('leather')
  hasLeather = has(leather)
  hasDiamond = has(TagKey('gemDiamond'))
  hasEnderPearl = has(enderPearl)
  dragonScaleBows = []
  for type in DragonType:
    if type is DragonType.SKELETON or type is DragonType.WITHER: continue
    base = type.value
    dragonScales = base.withSuffix('_dragon_scales')
    unlock = has(dragonScales)
    storageBlock(
      output,
      base.withSuffix('_dragon_scales'),
      base.withSuffix('_dragon_scale_block'),
      'dragon_scales',
      'dragon_scale_block'
    )
    shaped(base.withSuffix('_dragon_scale_axe')) \
      .define('X', dragonScales) \
      .define('#', stick) \
      .pattern('XX') \
      .pattern('X#') \
      .pattern(' #') \
      .groupBy('dragon_scale_axe') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'combat')
    shaped(base.withSuffix('_dragon_scale_hoe')) \
      .define('X', dragonScales) \
      .define('#', stick) \
      .pattern('XX') \
      .pattern(' #') \
      .pattern(' #') \
      .groupBy('dragon_scale_hoe') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'tools')
    shaped(base.withSuffix('_dragon_scale_pickaxe')) \
      .define('X', dragonScales) \
      .define('#', stick) \
      .pattern('XXX') \
      .pattern(' # ') \
      .pattern(' # ') \
      .groupBy('dragon_scale_pickaxe') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'tools')
    shaped(base.withSuffix('_dragon_scale_shovel')) \
      .define('X', dragonScales) \
      .define('#', stick) \
      .pattern('X') \
      .pattern('#') \
      .pattern('#') \
      .groupBy('dragon_scale_shovel') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'tools')
    shaped(base.withSuffix('_dragon_scale_sword')) \
      .define('X', dragonScales) \
      .define('#', stick) \
      .pattern('X') \
      .pattern('X') \
      .pattern('#') \
      .groupBy('dragon_scale_sword') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'combat')
    shaped(base.withSuffix('_dragon_scale_boots')) \
      .define('#', dragonScales) \
      .pattern('# #') \
      .pattern('# #') \
      .groupBy('dragon_scale_boots') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'combat')
    shaped(base.withSuffix('_dragon_scale_helmet')) \
      .define('#', dragonScales) \
      .pattern('###') \
      .pattern('# #') \
      .groupBy('dragon_scale_helmet') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'combat')
    shaped(base.withSuffix('_dragon_scale_leggings')) \
      .define('#', dragonScales) \
      .pattern('###') \
      .pattern('# #') \
      .pattern('# #') \
      .groupBy('dragon_scale_leggings') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'combat')
    shaped(base.withSuffix('_dragon_scale_chestplate')) \
      .define('#', dragonScales) \
      .pattern('# #') \
      .pattern('###') \
      .pattern('###') \
      .groupBy('dragon_scale_chestplate') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'combat')
    bow = base.withSuffix('_dragon_scale_bow')
    dragonScaleBows.append(bow)
    shaped(bow) \
      .define('#', dragonScales) \
      .define('X', stringItem) \
      .pattern(' #X') \
      .pattern('# X') \
      .pattern(' #X') \
      .groupBy('dragon_scale_bow') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'combat')
    shaped(base.withSuffix('_dragon_scale_shield')) \
      .define('W', dragonScales) \
      .define('o', ingot['iron']) \
      .pattern('WoW') \
      .pattern('WWW') \
      .pattern(' W ') \
      .groupBy('dragon_scale_shield') \
      .unlockedBy('has_scales', unlock) \
      .save(output, 'combat')
  for carriageType in CarriageType:
    shaped(makeId(carriageType.name + '_carriage')) \
      .define('X', leather) \
      .define('#', ItemStack('planks', 1, carriageType.value)) \
      .pattern('X X') \
      .pattern('###') \
      .groupBy('carriage') \
      .unlockedBy('has_leather', hasLeather) \
      .save(output, 'transportation')
  shaped(makeId('amulet')) \
    .define('Y', cobblestone) \
    .define('#', stringItem) \
    .define('X', enderPearl) \
    .pattern(' Y ') \
    .pattern('#X#') \
    .pattern(' # ') \
    .unlockedBy('has_pearl', hasEnderPearl) \
    .save(output, 'misc')
  shaped(makeId('flute')) \
    .define('P', enderPearl) \
    .define('#', stick) \
    .define('X', stringItem) \
    .pattern('P#') \
    .pattern('#X') \
    .unlockedBy('has_pearl', hasEnderPearl) \
    .save(output, 'misc')
  shaped(makeId('diamond_shears')) \
    .define('X', ingot['diamond']) \
    .pattern(' X') \
    .pattern('X ') \
    .unlockedBy('has_diamond', hasDiamond) \
    .save(output, 'combat')
  shaped(makeId('variation_orb')) \
    .define('W', ingot['emerald']) \
    .define('o', ingot['gold']) \
    .define('z', ingot['diamond']) \
    .define('r', redstone) \
    .pattern('roW') \
    .pattern('zrW') \
    .pattern(' z ') \
    .unlockedBy('has_diamond', hasDiamond) \
    .save(output, 'misc')
  shaped('dispenser') \
    .define('R', redstone) \
    .define('#', cobblestone) \
    .define('X', dragonScaleBows) \
    .pattern('###') \
    .pattern('#X#') \
    .pattern('#R#') \
    .unlockedBy('has_bow', has(dragonScaleBows)) \
    .save(output, 'redstone')
  for material in DragonArmorMaterial:
    items = material.value
    shaped(items[0]) \
      .groupBy('dragon_armor') \
      .define('#', items[1]) \
      .define('X', items[2]) \
      .pattern('X #') \
      .pattern(' XX') \
      .pattern('## ') \
      .unlockedBy('has_block', has(items[2])) \
      .save(output, 'combat')
  shaped('saddle') \
    .define('#', ingot['iron']) \
    .define('X', leather) \
    .pattern(' X ') \
    .pattern('X#X') \
    .unlockedBy('has_block', hasLeather) \
    .save(output, 'tools', 'saddle')
  shaped(makeId('dragon_nest')) \
    .define('#', stick) \
    .pattern('###') \
    .pattern('###') \
    .pattern('###') \
    .unlockedBy('has_stick', has('stick')) \
    .save(output, 'building_blocks')
  output.log('recipe(s)')
