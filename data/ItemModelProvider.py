from Core.Util import ResourceLocation, makeId
from Core.Output import Output
from Core.ModelBuilder import ItemModelBuilder as model
from DragonType import DragonType
from DragonVariant import DragonVariant
from DragonArmorMaterial import DragonArmorMaterial
from CarriageType import CarriageType

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
  identifier = type.value.withSuffix('_dragon_scales')
  basicItem(output, identifier, makeId('items/scales/' + identifier.path))

def dragonScaleToolItem(output: Output, type: DragonType, name: str):
  handheldItem(
    output,
    type.value.withSuffix('_dragon_scale_' + name),
    ResourceLocation(type.value.namespace, 'items/' + name + '/' + type.value.path + '_dragon_' + name)
  )

def dragonScaleArmorItem(output: Output, type: DragonType, name: str):
  identifier = type.value.withSuffix('_dragon_scale_' + name)
  basicItem(
    output,
    identifier,
    ResourceLocation(type.value.namespace, 'items/armor/' + type.value.path + '/' + identifier.path)
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
  dragonHeadModel = ResourceLocation('item/skull_dragon')
  basicItem(output, makeId('variation_orb'))
  basicItem(output, makeId('raw_dragon_meat'))
  basicItem(output, makeId('cooked_dragon_meat'))
  handheldItem(output, makeId('diamond_shears'))
  for type in CarriageType:
    basicItem(
      output,
      makeId(type.name + '_carriage'),
      makeId('items/carriage/carriage_' + type.name)
    )
  for material in DragonArmorMaterial:
    basicItem(
      output,
      material.value[0],
      makeId('items/armor/dragon/' + material.name.lower())
    )
  for variant in DragonVariant:
    model(dragonHeadModel).save(output, variant.value.path + '_dragon_head')
  for type in DragonType:
    name = type.value.path
    dragonAmuletItem(output, name)
    essence = type.value.withSuffix('_dragon_essence')
    basicItem(output, essence, essence.withPrefix('items/essence/'))
    model(spawnEggModel).save(output, name + '_dragon_spawn_egg')
    if (type is not DragonType.ENDER):
      model(ResourceLocation(type.value.namespace, 'block/' + name + '_dragon_egg'))\
        .save(output, name + '_dragon_egg')
    if (type is DragonType.SKELETON or type is DragonType.WITHER): continue
    model(ResourceLocation(type.value.namespace, 'block/' + name + '_dragon_scale_block'))\
      .save(output, name + '_dragon_scale_block')
    dragonScalesItem(output, type)
    dragonScaleBowItem(output, type)
    dragonScaleToolItem(output, type, 'axe')
    dragonScaleToolItem(output, type, 'pickaxe')
    dragonScaleToolItem(output, type, 'hoe')
    dragonScaleToolItem(output, type, 'shovel')
    dragonScaleToolItem(output, type, 'sword')
    dragonScaleArmorItem(output, type, 'helmet')
    dragonScaleArmorItem(output, type, 'chestplate')
    dragonScaleArmorItem(output, type, 'leggings')
    dragonScaleArmorItem(output, type, 'boots')
    root = name + '_dragon_scale_shield'
    texture = makeId('entities/dragon_scale_shield/' + name)
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