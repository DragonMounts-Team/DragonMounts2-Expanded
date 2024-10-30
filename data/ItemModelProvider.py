from Core.Util import ResourceLocation, makeId
from Core.Output import Output
from Core.ModelBuilder import ModelBuilder as model
from ItemBreedType import ItemBreedType

def generateItemModels(output: Output):
  shieldModel = makeId('item/shield/shield')
  blockingShieldModel = makeId('item/shield/shield_blocking')
  spawnEggModel = ResourceLocation('item/spawn_egg')
  blockingPredicate = ResourceLocation('blocking')
  for breed in ItemBreedType:
    if (breed is ItemBreedType.ENDER):
      name = 'end'
    else:
      name = breed.value.path
    if (breed is ItemBreedType.NETHER2 or breed is ItemBreedType.STORM2): continue
    if (breed is not ItemBreedType.FIRE2 and breed is not ItemBreedType.SUNLIGHT2 and breed is not ItemBreedType.TERRA2):
      model(spawnEggModel).save(output, 'summon_' + name)
    if (breed is ItemBreedType.SKELETON or breed is ItemBreedType.WITHER): continue
    root = 'dragon_shield_' + name
    texture = makeId('entities/dragon_shield/' + name)
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