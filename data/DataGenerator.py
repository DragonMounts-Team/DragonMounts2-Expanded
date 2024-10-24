from RecipeProvider import RecipeProvider
from Core.Output import Output

output = Output('../src/generated/assets/', 'dragonmounts')
RecipeProvider().buildRecipes(output)