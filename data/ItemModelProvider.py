from Core.Util import ResourceLocation, makeId
from Core.Output import Output
from Core.ModelBuilder import ModelBuilder as model
from ItemBreedType import ItemBreedType

def generateItemModels(output: Output):
  shieldModel = makeId('item/shield/shield')
  blockingShieldModel = makeId('item/shield/shield_blocking')
  blockingPredicate = ResourceLocation('blocking')
  for breed in ItemBreedType:
    if (
      breed is ItemBreedType.NETHER2 or
      breed is ItemBreedType.STORM2 or
      breed is ItemBreedType.SKELETON or
      breed is ItemBreedType.WITHER
    ): continue
    if (breed is ItemBreedType.ENDER):
      root = 'dragon_shield_end'
      texture = makeId('entities/dragon_shield/end')
    else:
      root = 'dragon_shield_' + breed.value.path
      texture = makeId('entities/dragon_shield/' + breed.value.path)
    blocking = root + '_blocking'
    model(blockingShieldModel)\
      .texture('base', texture)\
      .save(output, blocking)
    model(shieldModel)\
      .texture('base', texture)\
      .override()\
        .predicate(blockingPredicate, 1.0)\
        .model(makeId('item/' + blocking))\
      .end()\
      .save(output, root)
  output.log('model(s)')