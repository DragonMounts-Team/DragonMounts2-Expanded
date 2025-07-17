from enum import Enum
from Core.Util import makeId
class DragonType(Enum):
  AETHER = makeId('aether')
  WATER = makeId('water')
  ICE = makeId('ice')
  FIRE = makeId('fire')
  FOREST = makeId('forest')
  SKELETON = makeId('skeleton')
  WITHER = makeId('wither')
  NETHER = makeId('nether')
  ENDER = makeId('ender')
  ENCHANTED = makeId('enchanted')
  SUNLIGHT = makeId('sunlight')
  MOONLIGHT = makeId('moonlight')
  STORM = makeId('storm')
  TERRA = makeId('terra')
  ZOMBIE = makeId('zombie')
  DARK = makeId('dark')