from abc import ABC, abstractmethod
from Core.Util import ResourceLocation, JsonSerializable
from Core.Output import Output

class OverrideBuilder(JsonSerializable):
  _model = None
  def __init__(self, parent):
    self._parent = parent
    self._predicates = {}

  def predicate(self, name: ResourceLocation, value: float): 
    if (value == 0): return self
    self._predicates[str(name)] = value
    return self

  def model(self, model: ResourceLocation): 
    self._model = model
    return self

  def end(self):
    return self._parent
  
  def toJson(self):
    assert self._model is not None
    return {
      'predicate': self._predicates,
      'model': self._model
    }

class ModelBuilder(ABC):
  def __init__(self, parent: ResourceLocation):
    self.parent = parent
    self._textures = {}
  
  def texture(self, slot: str, texture: ResourceLocation | str):
    self._textures[slot] = texture
    return self
  
  def redirect(self, slot: str, target: str):
    self._textures[slot] = '#' + target
    return self

  @abstractmethod
  def save(self, output: Output, path: str):
    pass

class ItemModelBuilder(ModelBuilder):
  def __init__(self, parent: ResourceLocation):
    super().__init__(parent)
    self._overrides = []

  def override(self):
    builder = OverrideBuilder(self)
    self._overrides.append(builder)
    return builder

  def save(self, output: Output, path: str):
    json = {}
    if (self.parent is not None):
      json['parent'] = self.parent
    if (len(self._textures)):
      json['textures'] = self._textures
    if (len(self._overrides)):
      json['overrides'] = self._overrides
    output.accept('models/item/' + path, json)

class BlockModelBuilder(ModelBuilder):
  def __init__(self, parent: ResourceLocation):
    super().__init__(parent)

  def save(self, output: Output, path: str):
    json = {}
    if (self.parent is not None):
      json['parent'] = self.parent
    if (len(self._textures)):
      json['textures'] = self._textures
    output.accept('models/block/' + path, json)