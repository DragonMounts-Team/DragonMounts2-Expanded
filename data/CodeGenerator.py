from ItemBreedType import ItemBreedType
from enum import Enum

class EquipmentSlot(Enum):
  HEAD = ['HELMET', 'cap']
  CHEST = ['CHESTPLATE', 'tunic']
  LEGS = ['LEGGINGS', 'leggings']
  FEET = ['BOOTS', 'boots']

def makeArmor(breed: ItemBreedType, slot: EquipmentSlot):
  prefix = breed.name + '_DRAGON_SCALE_'
  print('public static final DragonScaleArmorItem ' + prefix + slot.value[0] + ' = new DragonScaleArmorItem(' + prefix + 'MATERIAL, ' + (
    '2, EntityEquipmentSlot.' if slot is EquipmentSlot.LEGS else '1, EntityEquipmentSlot.'
  ) + slot.name + ', "' + breed.value.path + '_dragonscale_' + slot.value[1] + '", EnumItemBreedTypes.' + breed.name + ', DMArmorEffects.' + breed.name + '_EFFECT);')

def makeArmors():
  for breed in ItemBreedType:
    makeArmor(breed, EquipmentSlot.HEAD)
    makeArmor(breed, EquipmentSlot.CHEST)
    makeArmor(breed, EquipmentSlot.LEGS)
    makeArmor(breed, EquipmentSlot.FEET)
    print()

def makeMaterial(breed: ItemBreedType, factor, enchantment, toughness):
  print(breed.name + '_DRAGON_SCALE_MATERIAL = EnumHelper.addArmorMaterial("'
    + breed.name + '_DRAGON_SCALE", prefix + ":' + breed.value.path + '", ' + str(factor) + ', defence, ' + str(enchantment) + ', sound, ' + str(toughness)
    + 'F);'
  )

def makeMaterials():
  low = [
    ItemBreedType.TERRA,
    ItemBreedType.TERRA2,
    ItemBreedType.MOONLIGHT,
    ItemBreedType.MOONLIGHT_FEMALE,
    ItemBreedType.ZOMBIE
  ]
  special = [
    ItemBreedType.ENCHANT,
    ItemBreedType.NETHER,
    ItemBreedType.NETHER2,
    ItemBreedType.ENDER,
    ItemBreedType.SKELETON,
    ItemBreedType.WITHER
  ]
  for breed in ItemBreedType: print('public static final ArmorMaterial ' + breed.name + '_DRAGON_SCALE_MATERIAL;')
  print('\nstatic {')
  print('int[] defence = new int[]{3, 7, 8, 3};')
  print('String prefix = DragonMountsTags.MOD_ID;')
  print('SoundEvent sound = SoundEvents.ITEM_ARMOR_EQUIP_GOLD;')
  for breed in low: makeMaterial(breed, 50, 11, 7.0)
  print('defence = new int[]{4, 7, 8, 4};')
  for breed in ItemBreedType:
    if breed in special or breed in low: continue
    makeMaterial(breed, 50, 11, 7.0)
  makeMaterial(ItemBreedType.ENCHANT, 50, 30, 7.0)
  print('defence = new int[]{4, 7, 9, 4};')
  makeMaterial(ItemBreedType.NETHER, 55, 11, 8.0)
  makeMaterial(ItemBreedType.NETHER2, 55, 11, 8.0)
  makeMaterial(ItemBreedType.ENDER, 70, 11, 9.0)
  print('}')