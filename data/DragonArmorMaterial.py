from enum import Enum
from Core.Util import cast2Ingredient, cast2Stack
class DragonArmorMaterial(Enum):
  IRON = [cast2Ingredient('iron_ingot'), cast2Stack('iron_block')]
  GOLDEN = [cast2Ingredient('gold_ingot'), cast2Stack('gold_block')]
  EMERALD = [cast2Ingredient('emerald'), cast2Stack('emerald_block')]
  DIAMOND = [cast2Ingredient('diamond'), cast2Stack('diamond_block')]