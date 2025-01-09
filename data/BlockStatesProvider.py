from Core.Output import Output
from DragonVariant import DragonVariant

def generateBlockStates(output: Output):
  model = {
    'model': 'dragonmounts:dragon_head'
  }
  rotation = {
    'variants': {
      'rotation=' + str(i): model for i in range(16)
    }
  }
  facing = {
    'variants': {
      'facing=south': model,
      'facing=west': model,
      'facing=north': model,
      'facing=east': model
    }
  }
  for variant in DragonVariant:
    base = "blockstates/" + variant.value.path + "_dragon_head"
    output.accept(base, rotation)
    output.accept(base + '_wall', facing)
  output.log('block state(s)')