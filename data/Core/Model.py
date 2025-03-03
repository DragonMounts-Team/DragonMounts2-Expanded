from Core.Util import ResourceLocation, cast2Id
from Core.Output import Output

class TextureSlot:
  def __init__(self, key: str, parent: 'TextureSlot' = None):
    self.key = key
    self.parent = parent

class TextureMapping:
  def __init__(self):
    self.textures = {}

  def put(self, slot: TextureSlot, texture: str | ResourceLocation):
    self.textures[slot.key] = texture
    return self

class ModelTemplate:
  def __init__(self, parent: str | ResourceLocation, *slots: TextureSlot):
    self._parent = { 'parent': cast2Id(parent) }
    self._slots = slots

  def create(self, output: Output, location: str, mapping: TextureMapping):
    mapping = mapping.textures
    textures = {}
    for slot in self._slots:
      key = slot.key
      if key in mapping:
        textures[key] = mapping[key]
      elif slot.parent is not None:
        textures[key] = '#' + slot.parent.key
    output.accept(location, {
      **self._parent,
      'textures': textures
    })