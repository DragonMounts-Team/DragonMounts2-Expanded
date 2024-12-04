from enum import Enum
from Core.Util import cast2Ingredient
class DragonArmorMaterial(Enum):
  IRON = cast2Ingredient('iron_ingot')
  GOLDEN = cast2Ingredient('gold_ingot')
  EMERALD = cast2Ingredient('emerald')
  DIAMOND = cast2Ingredient('diamond')