import json
from os import path
source = input('path: ')
if not path.exists(source): exit()
lines = []
with open(source, 'r', encoding='utf-8') as file:
  for (key, text) in json.load(file).items():
    lines.append(key + '=' + text +'\n')
dot = source.rfind('.')
with open((source if dot == -1 else source[0:dot]) + '.lang', 'w', encoding='utf-8') as lang:
  lang.writelines(lines)