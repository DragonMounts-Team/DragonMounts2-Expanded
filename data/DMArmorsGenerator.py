from DragonType import DragonType
from enum import Enum

class EquipmentSlot(Enum):
  HEAD = ['HELMET', 'cap']
  CHEST = ['CHESTPLATE', 'tunic']
  LEGS = ['LEGGINGS', 'leggings']
  FEET = ['BOOTS', 'boots']

def makeArmor(breed: DragonType, slot: EquipmentSlot):
  prefix = breed.name + '_DRAGON_SCALE_'
  print('public static final DragonScaleArmorItem ' + prefix + slot.value[0] + ' = new DragonScaleArmorItem(' + prefix + 'MATERIAL, ' + (
    '2, EntityEquipmentSlot.' if slot is EquipmentSlot.LEGS else '1, EntityEquipmentSlot.'
  ) + slot.name + ', "' + breed.value.path + '_dragonscale_' + slot.value[1] + '", EnumDragonTypes.' + breed.name + ', DMArmorEffects.' + breed.name + '_EFFECT);')

def makeArmors():
  for breed in DragonType:
    makeArmor(breed, EquipmentSlot.HEAD)
    makeArmor(breed, EquipmentSlot.CHEST)
    makeArmor(breed, EquipmentSlot.LEGS)
    makeArmor(breed, EquipmentSlot.FEET)
    print()

def makeMaterial(breed: DragonType, factor, enchantment, toughness):
  print(breed.name + '_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("'
    + breed.name + '_DRAGON_SCALE", prefix + ":' + breed.value.path + '", ' + str(factor) + ', defence, ' + str(enchantment) + ', sound, ' + str(toughness)
    + 'F);'
  )

def makeMaterials():
  low = [
    DragonType.TERRA,
    DragonType.MOONLIGHT,
    DragonType.ZOMBIE
  ]
  special = [
    DragonType.ENCHANTED,
    DragonType.NETHER,
    DragonType.ENDER,
    DragonType.SKELETON,
    DragonType.WITHER
  ]
  for breed in DragonType: print('public static final ArmorMaterial ' + breed.name + '_DRAGON_SCALE_MATERIAL;')
  print('\nstatic {')
  print('int[] defence = new int[]{3, 7, 8, 3};')
  print('String prefix = DragonMountsTags.MOD_ID;')
  print('SoundEvent sound = SoundEvents.ITEM_ARMOR_EQUIP_GOLD;')
  for breed in low: makeMaterial(breed, 50, 11, 7.0)
  print('defence = new int[]{4, 7, 8, 4};')
  for breed in DragonType:
    if breed in special or breed in low: continue
    makeMaterial(breed, 50, 11, 7.0)
  makeMaterial(DragonType.ENCHANTED, 50, 30, 7.0)
  print('defence = new int[]{4, 7, 9, 4};')
  makeMaterial(DragonType.NETHER, 55, 11, 8.0)
  makeMaterial(DragonType.ENDER, 70, 11, 9.0)
  print('}')