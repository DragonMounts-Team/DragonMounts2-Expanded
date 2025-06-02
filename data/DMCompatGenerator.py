from DragonType import DragonType

def updateMappings(old: str, neo):
  if (neo == 'SKELETON' or neo == 'WITHER'): return
  print('ITEM_MAPPINGS.put("' + old + '_dragonscales", DMItems.' + neo +'_DRAGON_SCALES);')
  print('ITEM_MAPPINGS.put("dragon_bow_' + old + '", DMItems.' + neo +'_DRAGON_SCALE_BOW);')
  print('ITEM_MAPPINGS.put("dragon_shield_' + old + '", DMItems.' + neo +'_DRAGON_SCALE_SHIELD);')
  print('ITEM_MAPPINGS.put("' + old + '_dragon_sword", DMItems.' + neo +'_DRAGON_SCALE_SWORD);')
  print('ITEM_MAPPINGS.put("' + old + '_dragon_axe", DMItems.' + neo +'_DRAGON_SCALE_AXE);')
  print('ITEM_MAPPINGS.put("' + old + '_dragon_pickaxe", DMItems.' + neo +'_DRAGON_SCALE_PICKAXE);')
  print('ITEM_MAPPINGS.put("' + old + '_dragon_hoe", DMItems.' + neo +'_DRAGON_SCALE_HOE);')
  print('ITEM_MAPPINGS.put("' + old + '_dragon_shovel", DMItems.' + neo +'_DRAGON_SCALE_SHOVEL);')
  print('suit = DMItems.' + neo + '_DRAGON_SCALE_ARMORS;')
  print('ITEM_MAPPINGS.put("' + old + '_dragonscale_cap", suit.helmet);')
  print('ITEM_MAPPINGS.put("' + old + '_dragonscale_tunic", suit.chestplate);')
  print('ITEM_MAPPINGS.put("' + old + '_dragonscale_leggings", suit.leggings);')
  print('ITEM_MAPPINGS.put("' + old + '_dragonscale_boots", suit.boots);')


for type in DragonType:
  old = type.value.path
  neo = type.name
  updateMappings(old, neo)
  if (
    type is DragonType.FIRE or
    type is DragonType.MOONLIGHT or
    type is DragonType.TERRA or
    type is DragonType.NETHER or
    type is DragonType.SUNLIGHT
  ): updateMappings(old + '2', neo)