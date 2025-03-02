from Core.Output import Output
from DragonVariant import DragonVariant
from DragonType import  DragonType

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

  for types in DragonType:
    scales_block = {
      "variants": {
        "normal": {
          "model": "dragonmounts:" + types.value.path + "_dragon_scales_block"
        }
      }
    }
    scales_base = "blockstates/" + types.value.path+ "_dragon_scales_block"
    output.accept(scales_base, scales_block)

  output.log('block state(s)')

