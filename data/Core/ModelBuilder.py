from Core.Util import ResourceLocation, JsonSerializable
from Core.Output import Output
class OverrideBuilder(JsonSerializable):
  _model = None
  def __init__(self, parent):
    self._parent = parent
    self._predicates = {}

  def predicate(self, name: ResourceLocation, value: float): 
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

class ModelBuilder:

  def __init__(self, parent: ResourceLocation):
    self.parent = parent
    self._textures = {}
    self._overrides = []

  def override(self):
    builder = OverrideBuilder(self)
    self._overrides.append(builder)
    return builder
  
  def texture(self, key: str, texture: ResourceLocation):
    self._textures[key] = texture
    return self

  def save(self, output: Output, path: str):
    json = {
      'parent': self.parent,
      'textures': self._textures
    }
    if (len(self._overrides)):
      json['overrides'] = self._overrides
    output.accept('models/item/' + path, json)