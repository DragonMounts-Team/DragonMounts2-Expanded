from enum import Enum
from Core.Util import makeId, cast2Ingredient, cast2Stack
class DragonArmorMaterial(Enum):
  IRON = [
    makeId("iron_dragon_armor"),
    cast2Ingredient('iron_ingot'),
    cast2Stack('iron_block')
  ]
  GOLD = [
    makeId("golden_dragon_armor"),
    cast2Ingredient('gold_ingot'),
    cast2Stack('gold_block')
  ]
  EMERALD = [
    makeId("emerald_dragon_armor"),
    cast2Ingredient('emerald'),
    cast2Stack('emerald_block')
  ]
  DIAMOND = [
    makeId("diamond_dragon_armor"),
    cast2Ingredient('diamond'),
    cast2Stack('diamond_block')
  ]