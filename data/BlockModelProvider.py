from DragonType import DragonType
from Core.Output import Output
from Core.Model import ModelTemplate, TextureSlot, TextureMapping

TEXTURE_SLOT_ALL = TextureSlot('all')
TEMPLATE_CUBE_ALL = ModelTemplate('block/cube_all', TEXTURE_SLOT_ALL)

def generateBlockModels(output: Output):
  for type in DragonType:
    if (type is DragonType.SKELETON or type is DragonType.WITHER): continue
    TEMPLATE_CUBE_ALL.create(
      output,
      'models/block/' + type.value.path + '_dragon_scale_block',
      TextureMapping()\
        .put(TEXTURE_SLOT_ALL, 'dragonmounts:blocks/' + type.value.path + '_dragon_scale_block')
    )
  output.log('block model(s)')