import json
input = '../run/lang.json'
output = '../run/lang/'
locales = [
  'en_us',
  'zh_cn',
  'ja_jp',
  'fr_fr'
]
with open(input, 'r', encoding='utf-8') as file:
  data = json.load(file)
  if 'translations' in data:
    data = data['translations']

def dump(locale: str):
  with open(output + locale + '.lang', 'w', encoding='utf-8') as lang:
    for key, info in data.items():
      if locale in info:
        lang.write(key + '=' + info[locale] + '\n')

for locale in locales: dump(locale)