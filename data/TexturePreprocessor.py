from PIL import Image
from pathlib import Path
input_folder = '../src/main/resources/assets/dragonmounts/textures/entities/dragon/'
output_folder = '../run/textures/'
mask = Image.open(input_folder + 'dissolve.png').convert('RGBA')
mask_pixels = mask.load()
def process(paths: tuple[str, str, str]) -> None:
  base = Image.open(paths[0]).convert('RGBA')
  glow = Image.open(paths[1]).convert('RGBA')
  if mask.size != base.size or mask.size != glow.size: raise ValueError('size mismatched')
  base_pixels = base.load()
  glow_pixels = glow.load()
  result = Image.new('RGBA', mask.size, (0, 0, 0, 0))
  builder = result.load()
  width, height = mask.size
  unchanged = True
  for y in range(height):
    for x in range(width):
      if base_pixels[x, y][3] > 0 or glow_pixels[x, y][3] > 0: # type: ignore
        builder[x, y] = mask_pixels[x, y] # type: ignore
      elif mask_pixels[x, y][3] > 0: # type: ignore
        unchanged = False
  if unchanged:
    print('Ignore', paths[2])
    return
  out = Path(paths[2])
  out.parent.mkdir(parents=True, exist_ok=True)
  result.save(out)
  print('Generated', out)
def common(path: str) -> tuple[str, str, str]:
  return (
    input_folder + path + '/body.png',
    input_folder + path + '/glow.png',
    output_folder + path + '/dissolve.png'
  )
def forest(major: str, minor: str) -> tuple[str, str, str]:
  return (
    input_folder + 'forest/' + major + '/' + minor + '_body.png',
    input_folder + 'forest/glow.png',
    output_folder+ 'forest/' + major + '/' + minor + '_dissolve.png'
  )
for path in [
  common('aether/female'),
  common('aether/male'),
  common('dark/female'),
  common('dark/male'),
  common('enchanted/female'),
  common('enchanted/male'),
  common('ender/female'),
  common('ender/male'),
  common('ender/rare'),
  common('fire/female'),
  common('fire/male'),
  common('fire/rare'),
  forest('dry', 'female'),
  forest('dry', 'male'),
  forest('forest', 'female'),
  forest('forest', 'male'),
  forest('taiga', 'female'),
  forest('taiga', 'male'),
  common('ice/female'),
  common('ice/male'),
  common('moonlight/female'),
  common('moonlight/male'),
  common('nether/female'),
  common('nether/male'),
  common('nether/soul'),
  common('skeleton'),
  common('storm/female'),
  common('storm/male'),
  common('storm/rare'),
  common('sunlight/female'),
  common('sunlight/male'),
  common('terra/female'),
  common('terra/male'),
  common('water/female'),
  common('water/male'),
  common('wither'),
  common('zombie')
]: process(path)