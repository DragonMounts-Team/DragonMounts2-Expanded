from Core.Util import JsonSerializable, ItemStack, TagKey, cast2Stack

class ItemPredicate(JsonSerializable):
  def __init__(self, item: ItemStack | TagKey | str):
    self.item = item if isinstance(item, TagKey) else cast2Stack(item)

  def toJson(self):
    return {
      'type': 'forge:ore_dict',
      'ore': self.item
    } if isinstance(self.item, TagKey) else self.item

class Criterion(JsonSerializable):
  pass

class InventoryChangedCriterion(Criterion):
  def __init__(self, *args):
    predicates = []
    for arg in args:
      if (isinstance(arg, list)):
        for item in arg:
          predicates.append(ItemPredicate(item))
      else:
        predicates.append(ItemPredicate(arg))
    self.predicates = predicates

  def toJson(self):
    return { 'trigger': 'minecraft:inventory_changed', 'conditions': { 'items': self.predicates } }

class RecipeCriterion(Criterion):
  pass