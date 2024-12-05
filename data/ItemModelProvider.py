from Core.Util import ResourceLocation, makeId
from Core.Output import Output
from Core.ModelBuilder import ModelBuilder as model
from DragonType import DragonType
from DragonArmorMaterial import DragonArmorMaterial

generatedModel = ResourceLocation('item/generated')
handheldModel = ResourceLocation('item/handheld')
bowModel = ResourceLocation('item/bow')
blockingPredicate = ResourceLocation('blocking')
pullingPredicate = ResourceLocation('pulling')
pullPredicate = ResourceLocation('pull')
amuletModel = makeId('item/amulet')

def basicItem(output: Output, identifier: ResourceLocation, texture: ResourceLocation = None):
  model(generatedModel)\
    .texture('layer0', identifier.withPrefix('items/') if texture is None else texture)\
    .save(output, identifier.path)

def handheldItem(output: Output, identifier: ResourceLocation, texture: ResourceLocation = None):
  model(handheldModel)\
    .texture('layer0', identifier.withPrefix('items/') if texture is None else texture)\
    .save(output, identifier.path)

def dragonScalesItem(output: Output, type: DragonType):
  identifier = type.value
  basicItem(output, identifier.withSuffix('_dragon_scales'), makeId('items/scales/' + identifier.path + '_dragon_scales'))

def dragonScaleToolItem(output: Output, type: DragonType, name: str):
  handheldItem(
    output,
    type.value.withSuffix('_dragon_scale_' + name),
    ResourceLocation(type.value.namespace, 'items/tools/' + name + '/' + type.value.path + '_dragon_' + name)
  )

def dragonScaleArmorItem(output: Output, type: DragonType, name: str, nick: str):
  basicItem(
    output,
    type.value.withSuffix('_dragon_scale_' + name),
    ResourceLocation(type.value.namespace, 'items/armor/' + type.value.path + '/' + type.value.path + '_dragonscale_' + nick)
  )

def dragonScaleBowItem(output: Output, type: DragonType):
  textureBase = type.value.withPrefix('items/bow/dragon_bow_')
  pathBase = type.value.withSuffix('_dragon_scale_bow')
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

def dragonAmuletItem(output, type: str):
  model(amuletModel)\
    .texture('layer0', makeId('items/amulet/' + type + '_dragon_amulet'))\
    .save(output, type + '_dragon_amulet')

def generateItemModels(output: Output):
  shieldModel = makeId('item/shield/shield')
  blockingShieldModel = makeId('item/shield/shield_blocking')
  spawnEggModel = ResourceLocation('item/spawn_egg')
  basicItem(output, makeId('variant_switcher'))
  for material in DragonArmorMaterial:
    name = material.name.lower()
    basicItem(
      output,
      makeId(name + '_dragon_armor'),
      makeId('items/armor/dragon/dragonarmor_' + ('gold' if name == 'golden' else name))
    )
  for type in DragonType:
    name = type.value.path
    dragonAmuletItem(output, name)
    essence = type.value.withSuffix('_dragon_essence')
    basicItem(output, essence, essence.withPrefix('items/essence/'))
    model(spawnEggModel).save(output, name + '_dragon_spawn_egg')
    model(ResourceLocation(type.value.namespace, 'block/' + name + '_dragon_egg')).save(output, name + '_dragon_egg')
    if (type is DragonType.SKELETON or type is DragonType.WITHER): continue
    dragonScalesItem(output, type)
    dragonScaleBowItem(output, type)
    dragonScaleToolItem(output, type, 'axe')
    dragonScaleToolItem(output, type, 'pickaxe')
    dragonScaleToolItem(output, type, 'hoe')
    dragonScaleToolItem(output, type, 'shovel')
    dragonScaleToolItem(output, type, 'sword')
    dragonScaleArmorItem(output, type, 'helmet', 'cap')
    dragonScaleArmorItem(output, type, 'chestplate', 'tunic')
    dragonScaleArmorItem(output, type, 'leggings', 'leggings')
    dragonScaleArmorItem(output, type, 'boots', 'boots')
    root = name + '_dragon_scale_shield'
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
  output.log('item model(s)')