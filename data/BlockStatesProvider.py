from Core.Output import Output
from DragonVariant import DragonVariant

def generateBlockStates(output: Output):
  rotation = {
    'forge_marker': 1,
    'defaults': {
      'model': 'dragonmounts:dragon_head',
    },
    'variants': {
      'rotation': {
        str(i): {} for i in range(16)
      }
    }
  }
  facing = {
    'forge_marker': 1,
    'defaults': {
      'model': 'dragonmounts:dragon_head',
    },
    'variants': {
      'facing': {
        'south': {},
        'west': {},
        'north': {},
        'east': {}
      }
    }
  }
  for variant in DragonVariant:
    base = "blockstates/" + variant.value.path + "_dragon_head"
    output.accept(base, rotation)
    output.accept(base + '_wall', facing)
  output.log('block state(s)')