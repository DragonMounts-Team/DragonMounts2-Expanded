from abc import ABC, abstractmethod
from Core.Output import Output
from Core.Util import ItemStack, cast2Id, cast2Ingredient
from Core.Criterion import Criterion
class CraftingRecipeBuilder(ABC):
  def makeAdvancement(self, recipe: str) -> dict:
    requirements = [ 'has_the_recipe' ]
    criteria = {
      'has_the_recipe': {
        'trigger': 'minecraft:recipe_unlocked',
        'conditions': {
          'recipe': recipe
        }
      }
    }
    for name, criterion in self._criteria.items():
      requirements.append(name)
      criteria[name] = criterion
    return {
      'parent': 'minecraft:recipes/root',
      'rewards': {
        'recipes': [ recipe ]
      },
      'criteria': criteria,
      'requirements': [
        requirements
      ]
    }
  @abstractmethod
  def makeRecipe(self) -> dict:
    pass
  def __init__(self, result, count = 1, data = 32767):
    self._criteria = {}
    self._group = {}
    if (isinstance(result, ItemStack)):
      self.result = result
    else:
      self.result = ItemStack(cast2Id(result), count, data)

  def save(self, output: Output, category: str = '', path: str = ''):
    if (len(category) and not category.endswith('/')):
      category = category + '/'
    if path == '':
      path = self.result.item.path
    output.accept('recipes/' + path, self.makeRecipe())
    if (len(self._criteria) != 0):
      output.accept(
        'advancements/recipes/' + category + path,
        self.makeAdvancement(output.namespace + ':' + path)
      )

  def unlockedBy(self, name: str, criteria: Criterion):
    self._criteria[name] = criteria
    return self

  def groupBy(self, group: str):
    if (group is None and 'group' in self._group): 
      del self._group['group']
    else:
     self._group['group'] = group
    return self

class ShapedRecipeBuilder(CraftingRecipeBuilder):
  _width = 0
  def __init__(self, result, count = 1, data = 32767):
    super().__init__(result, count, data)
    self._keys = {}
    self._patterns = []
  def define(self, symbol: str, ingredient):
    assert len(symbol) == 1
    if (symbol in self._keys):
      self._keys[symbol].append(cast2Ingredient(ingredient))
    else:
      self._keys[symbol] = [cast2Ingredient(ingredient)]
    return self

  def pattern(self, pattern: str):
    width = len(pattern)
    assert 0 < width <= 3
    if self._width == 0:
      self._width = width
    else:
      assert self._width == width
    assert len(self._patterns) < 3
    self._patterns.append(pattern)
    return self

  def makeRecipe(self) -> dict:
    for pattern in self._patterns:
      for char in pattern:
        assert char == ' ' or char in self._keys
    return {
      'type': 'minecraft:crafting_shaped',
      **self._group,
      'pattern': self._patterns,
      'key': {
        symbol: ingredients[0] if len(ingredients) == 1 else ingredients
        for symbol, ingredients in self._keys.items()
      },
      'result': self.result
    }

class ShapelessRecipeBuilder(CraftingRecipeBuilder):
  def __init__(self, result, count = 1, data = 32767):
    super().__init__(result, count, data)
    self._ingredients = []

  def requires(self, ingredient, count = 1):
    ingredient = cast2Ingredient(ingredient)
    for _ in range(count):
      self._ingredients.append(ingredient)
    return self

  def makeRecipe(self) -> dict:
    return {
      'type': 'minecraft:crafting_shapeless',
      **self._group,
      'ingredients': self._ingredients,
      'result': self.result
    }