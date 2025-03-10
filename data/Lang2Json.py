import json
input = '../src/main/resources/assets/dragonmounts/lang/'
output = '../run/lang.json'
locales = [
  'en_us',
  'zh_cn',
  'ja_jp',
  'fr_fr'
]
content = {}
missing = {
  locale: [] for locale in locales
}

def include(locale: str):
  with open(input + locale + '.lang', 'r', encoding='utf-8') as lang:
    for line in lang:
      separator = line.find('=')
      if (separator == -1): continue
      comment = line.find('#')
      if (comment == -1):
        comment = len(line)
      if (comment > separator):
        key = line[:separator].strip()
        if (key in content):
          content[key][locale] = line[separator + 1:comment].strip()
        else:
          content[key] = { locale: line[separator + 1:comment].strip() }

for locale in locales: include(locale)
for key in list(content):
  info = content[key]
  if 'en_us' not in info:
    del content[key]
    continue
  for locale in locales:
    if locale in info: continue
    missing[locale].append(key)

with open(output, 'w', encoding='utf-8') as file:
  json.dump({
    'missing': missing,
    'translations': content
  }, file, indent = 2, ensure_ascii=False)