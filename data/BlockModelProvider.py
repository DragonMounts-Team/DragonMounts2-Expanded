from DragonType import DragonType
from Core.Util import ResourceLocation, makeId
from Core.Output import Output
from Core.ModelBuilder import ModelBuilder as model

generatedModel = ResourceLocation('item/generated')


def dragonScalesBlock(output: Output):
    for types in DragonType:
        scales_block = {
            "parent": "block/cube_all",
            "textures": {
                "all": "dragonmounts:blocks/" + types.value.path + "_dragon_scales_block"
            }
        }
        scales_item = {
            "parent": "dragonmounts:block/" + types.value.path + "_dragon_scales_block"
        }
        scales_block_base = "models/block/" + types.value.path + "_dragon_scales_block"
        scales_item_base = "models/item/" + types.value.path + "_dragon_scales_block"
        output.accept(scales_block_base, scales_block)
        output.accept(scales_item_base, scales_item)


def generateBlockModels(output: Output):
    dragonScalesBlock(output)
