from Core.Output import Output
from Core.Util import ResourceLocation
from DragonVariant import DragonVariant
from DragonType import  DragonType

def makeSimpleBlock(output: Output, block: ResourceLocation):
  output.accept('blockstates/' + block.path, { 'variants': { 'normal': { 'model': block } } })

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
  for type in DragonType:
    if (type is DragonType.SKELETON or type is DragonType.WITHER): continue
    makeSimpleBlock(output, type.value.withSuffix('_dragon_scale_block'))
  output.log('block state(s)')

