from enum import Enum
from Core.Util import makeId, TagKey
class DragonArmorMaterial(Enum):
  COPPER = [
    makeId("copper_dragon_armor"),
    TagKey('ingotCopper'),
    TagKey('blockCopper')
  ]
  IRON = [
    makeId("iron_dragon_armor"),
    TagKey('ingotIron'),
    TagKey('blockIron')
  ]
  GOLD = [
    makeId("golden_dragon_armor"),
    TagKey('ingotGold'),
    TagKey('blockGold')
  ]
  EMERALD = [
    makeId("emerald_dragon_armor"),
    TagKey('gemEmerald'),
    TagKey('blockEmerald')
  ]
  DIAMOND = [
    makeId("diamond_dragon_armor"),
    TagKey('gemDiamond'),
    TagKey('blockDiamond')
  ]