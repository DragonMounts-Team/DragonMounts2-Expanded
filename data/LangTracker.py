import json
import os
import re

source = 'en_us'
langs = '../src/main/resources/assets/dragonmounts/lang/'
record = '../run/LangRecord.json'
output = '../run/LangOverview.json'
usage = r'^commands\..*?\.usage$'

# read lang files
locales = []
translations = {}
for file in os.listdir(langs):
  if not file.endswith('.lang'): continue
  location = os.path.join(langs, file)
  if not os.path.isfile(location): continue
  with open(location, 'r', encoding='utf-8') as lang:
    locale = location[-10:-5]
    locales.append(locale)
    for line in lang:
      separator = line.find('=')
      if (separator == -1): continue
      comment = line.find('#')
      if (comment == -1):
        comment = len(line)
      if (comment > separator):
        key = line[:separator].strip()
        if (key in translations):
          translations[key][locale] = line[separator + 1:comment].strip()
        else:
          translations[key] = { locale: line[separator + 1:comment].strip() }

# processing
locales.remove(source)
redundant = {
  locale: [] for locale in locales
}
untranslated = {
  locale: [] for locale in locales
}
modified = []
sample = {}
for key in list(translations):
  info = translations[key]
  if source not in info:
    for locale in locales:
      if locale in info:
        redundant[locale].append(key)
    del translations[key]
    continue
  raw = info[source]
  sample[key] = raw
  for locale in locales:
    if locale in info and (raw != info[locale] or re.match(usage, key)): continue
    untranslated[locale].append(key)

# compare with record
if os.path.isfile(record):
  with open(record, 'r', encoding='utf-8') as file:
    data = json.load(file)
  for key, text in sample.items():
    if key in data and text != data[key]:
      modified.append(key)

# simply output
for locale in list(redundant):
  if not redundant[locale]:
    del redundant[locale]
for locale in list(untranslated):
  if not untranslated[locale]:
    del untranslated[locale]

# save output
with open(output, 'w', encoding='utf-8') as file:
  json.dump({
    'modified': modified,
    'redundant': redundant,
    'untranslated': untranslated,
    'translations': translations
  }, file, indent = 2, ensure_ascii=False)

# open if needed
if hasattr(os, 'startfile') and (modified or redundant or untranslated):
  os.startfile(os.path.normpath(output))

# ask whether to save record
if modified:
  order = input('Save record?')
  if order != 'NO':
    with open(record, 'w', encoding='utf-8') as file:
      json.dump(sample, file, indent = 2, ensure_ascii=False)
