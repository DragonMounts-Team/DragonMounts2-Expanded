from Core.Util import JsonSerializable
import os
import json
class Encoder(json.JSONEncoder):
    def default(self, obj):
        if (isinstance(obj, JsonSerializable)): return obj.toJson()
        return super().default(obj)

class Output:
  def __init__(self, root: str, namespace: str):
    if (root.endswith('/')):
      self.root = root + namespace + '/'
    else:
      self.root = root + '/' + namespace + '/'
    self.namespace = namespace

  def accept(self, name: str, content: dict):
    path = os.path.join(self.root, name + '.json')
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as file:
      json.dump(content, file, cls = Encoder, indent = 2)

class ConsoleOutput(Output):
  def __init__(self, root: str, namespace: str):
    super().__init__(root, namespace)

  def accept(self, name: str, content: dict):
    print('save to', self.root + name + '.json', '\n', json.dumps(content, cls = Encoder, indent = 2))