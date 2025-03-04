from DragonType import DragonType
from Core.Output import Output
from Core.Util import ResourceLocation
from Core.ModelBuilder import BlockModelBuilder as model

cubeAllModel = ResourceLocation('block/cube_all')

def generateBlockModels(output: Output):
  for type in DragonType:
    if (type is DragonType.SKELETON or type is DragonType.WITHER): continue
    model(cubeAllModel)\
      .texture('all', 'dragonmounts:blocks/' + type.value.path + '_dragon_scale_block')\
      .save(output, type.value.path + '_dragon_scale_block')
  output.log('block model(s)')