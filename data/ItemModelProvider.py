from Core.Util import ResourceLocation, makeId
from Core.Output import Output
from Core.ModelBuilder import ModelBuilder as model
from ItemBreedType import ItemBreedType

generatedModel = ResourceLocation('item/generated')
handheldModel = ResourceLocation('item/handheld')
bowModel = ResourceLocation('item/bow')
blockingPredicate = ResourceLocation('blocking')
pullingPredicate = ResourceLocation('pulling')
pullPredicate = ResourceLocation('pull')

def basicItem(output: Output, identifier: ResourceLocation, texture: ResourceLocation = None):
  model(generatedModel)\
    .texture('layer0', identifier.withPrefix('item/') if texture is None else texture)\
    .save(output, identifier.path)

def handheldItem(output: Output, identifier: ResourceLocation, texture: str = None):
  model(handheldModel)\
    .texture('layer0', identifier.withPrefix('item/') if texture is None else texture)\
    .save(output, identifier.path)

def dragonScalesItem(output: Output, breed: ItemBreedType):
  identifier = breed.value
  basicItem(output, identifier.withSuffix('_dragonscales'), makeId('items/scales/' + identifier.path + '_dragon_scales'))

def dragonScaleArmorItem(output: Output, breedName: str, type: str):
  identifier = makeId(breedName + '_dragonscale_' + type)
  basicItem(output, identifier, identifier.withPrefix('items/armor/'+ breedName + '/'))

def dragonScaleToolItem(output: Output, breed: ItemBreedType, type: str):
  identifier = breed.value.withSuffix('_dragon_' + type)
  handheldItem(output, identifier, identifier.withPrefix('items/tools/' + type + '/'))

def dragonScaleBowItem(output: Output, breed: ItemBreedType):
  pathBase = breed.value.withPrefix('dragon_bow_')
  textureBase = pathBase.withPrefix('items/bow/')
  actualLoc = pathBase.withPrefix('item/')
  model(actualLoc).texture('layer0', textureBase.withSuffix('_0')).save(output, pathBase.path + '_0')
  model(actualLoc).texture('layer0', textureBase.withSuffix('_1')).save(output, pathBase.path + '_1')
  model(actualLoc).texture('layer0', textureBase.withSuffix('_2')).save(output, pathBase.path + '_2')
  model(bowModel).\
    texture('layer0', textureBase)\
    .override()\
      .predicate(pullingPredicate, 1.0)\
      .model(actualLoc.withSuffix('_0'))\
    .end()\
    .override()\
      .predicate(pullingPredicate, 1.0)\
      .predicate(pullPredicate, 0.65)\
      .model(actualLoc.withSuffix('_1'))\
    .end()\
    .override()\
      .predicate(pullingPredicate, 1.0)\
      .predicate(pullPredicate, 0.9)\
      .model(actualLoc.withSuffix('_2'))\
    .end()\
    .save(output, pathBase.path)

def generateItemModels(output: Output):
  shieldModel = makeId('item/shield/shield')
  blockingShieldModel = makeId('item/shield/shield_blocking')
  spawnEggModel = ResourceLocation('item/spawn_egg')
  for breed in ItemBreedType:
    if (breed is ItemBreedType.ENDER):
      name = 'end'
    else:
      name = breed.value.path
    if (breed is ItemBreedType.NETHER2 or breed is ItemBreedType.STORM2): continue
    if breed is ItemBreedType.MOONLIGHT_MALE:
      dragonScalesItem(output, breed)
      dragonScaleArmorItem(output, name, 'boots')
      dragonScaleArmorItem(output, name, 'cap')
      dragonScaleArmorItem(output, name, 'leggings')
      dragonScaleArmorItem(output, name, 'tunic')
      dragonScaleToolItem(output, breed, 'axe')
      dragonScaleToolItem(output, breed, 'hoe')
      dragonScaleToolItem(output, breed, 'pickaxe')
      dragonScaleToolItem(output, breed, 'shovel')
      dragonScaleToolItem(output, breed, 'sword')
      dragonScaleBowItem(output, breed)
    elif (breed is not ItemBreedType.FIRE2 and breed is not ItemBreedType.SUNLIGHT2 and breed is not ItemBreedType.TERRA2):
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