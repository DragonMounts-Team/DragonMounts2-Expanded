from Core.Util import JsonSerializable, cast2Stack

class Criterion(JsonSerializable):
  pass

class InventoryChangedCriterion(Criterion):
  def __init__(self, *args):
    stacks = []
    for arg in args:
      if (isinstance(arg, list)):
        for item in arg:
          stacks.append(cast2Stack(item))
      else:
        stacks.append(cast2Stack(arg))
    self.stacks = stacks

  def toJson(self):
    return { 'trigger': 'minecraft:inventory_changed', 'conditions': { 'items': self.stacks } }

class RecipeCriterion(Criterion):
  pass