import json
from os import path
source = '../run/lang.json'
output = '../run/lang/'
locales = [
  'en_us',
  'zh_cn',
  'ja_jp',
  'fr_fr'
]
if not path.exists(source): exit()
with open(source, 'r', encoding='utf-8') as file:
  data = json.load(file)
  if 'translations' in data:
    data = data['translations']

def dump(locale: str):
  with open(output + locale + '.lang', 'w', encoding='utf-8') as lang:
    for key, info in data.items():
      if locale in info:
        lang.write(key + '=' + info[locale] + '\n')

for locale in locales: dump(locale)