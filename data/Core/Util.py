from abc import ABC

def makeId(name: str):
    return ResourceLocation('dragonmounts', name.lower())

def cast2Id(obj):
  if (isinstance(obj, ResourceLocation)): return obj
  return ResourceLocation(str(obj))

def cast2Stack(obj):
  if (isinstance(obj, ItemStack)): return obj
  return ItemStack(cast2Id(obj))

def cast2Ingredient(obj):
  if (isinstance(obj, list)):
    tmp = []
    for item in obj:
      tmp.append(cast2Ingredient(item))
    return tmp
  if (isinstance(obj, Ingredient)): return obj
  return StackIngredient(cast2Stack(obj))

class JsonSerializable:
  def toJson(self):
    return str(self)

class ResourceLocation(JsonSerializable):
  namespace: str = 'minecraft'
  path: str = 'root'
  def __init__(self, location: str, path: str = ''):
    if path != '':
      self.namespace = location
      self.path = path
    else:
      index = location.find(':')
      if index == -1:
        self.path = location
      else:
        self.namespace = location[:index]
        self.path = location[index + 1:]

  def __str__(self):
    return self.namespace + ':' + self.path

  def withPrefix(self, perfix: str):
    return ResourceLocation(self.namespace, perfix + self.path)

  def withSuffix(self, suffix: str):
    return ResourceLocation(self.namespace, self.path + suffix)

class ItemStack(JsonSerializable):
  def __init__(self, item, count = 1, data = 32767):
    self.item = cast2Id(item)
    self.count = count
    self.data = data

  def toJson(self):
    result = { 'item': str(self.item) }
    if (self.count != 1):
      result['count'] = self.count
    if (self.data != 32767):
      result['data'] = self.data
    return result

class Ingredient(ABC, JsonSerializable):
  def toJson(self):
    raise NotImplementedError()

class StackIngredient(Ingredient):
  def __init__(self, stack):
    self.stack = cast2Stack(stack)

  def toJson(self):
    return self.stack.toJson()

class NBTIngredient(Ingredient):
  def __init__(self):
    raise NotImplementedError()

class OreIngredient(Ingredient):
  def __init__(self, ore: str):
    self.ore = ore

  def toJson(self):
    return {
      'type': 'forge:ore_dict',
      'ore': self.ore
    }