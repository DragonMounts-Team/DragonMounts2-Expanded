from enum import Enum
from Core.Util import makeId, TagKey, cast2Stack
class DragonArmorMaterial(Enum):
  IRON = [
    makeId("iron_dragon_armor"),
    TagKey('ingotIron'),
    cast2Stack('iron_block')
  ]
  GOLD = [
    makeId("golden_dragon_armor"),
    TagKey('gold_ingot'),
    cast2Stack('gold_block')
  ]
  EMERALD = [
    makeId("emerald_dragon_armor"),
    TagKey('ingotGold'),
    cast2Stack('emerald_block')
  ]
  DIAMOND = [
    makeId("diamond_dragon_armor"),
    TagKey('gemDiamond'),
    cast2Stack('diamond_block')
  ]