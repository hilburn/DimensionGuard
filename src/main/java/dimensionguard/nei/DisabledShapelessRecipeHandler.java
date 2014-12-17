package dimensionguard.nei;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.recipe.ShapelessRecipeHandler;
import dimensionguard.items.DisabledRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class DisabledShapelessRecipeHandler extends ShapelessRecipeHandler
{
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if(outputId.equals("crafting")) {
            for (DisabledRecipe disabledRecipe: NEIDimensionGuardConfig.disabledRecipes)
            {
                IRecipe irecipe = disabledRecipe.getRecipe();
                ShapelessRecipeHandler.CachedShapelessRecipe recipe = null;
                if(irecipe instanceof ShapelessRecipes) {
                    recipe = this.shapelessRecipe((ShapelessRecipes)irecipe);
                } else if(irecipe instanceof ShapelessOreRecipe) {
                    recipe = this.forgeShapelessRecipe((ShapelessOreRecipe)irecipe);
                }

                if(recipe != null) {
                    this.arecipes.add(recipe);
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }

    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (DisabledRecipe disabledRecipe: NEIDimensionGuardConfig.disabledRecipes) {
            if (NEIServerUtils.areStacksSameTypeCrafting(disabledRecipe.getRecipeOutput(), result)) {
                IRecipe irecipe = disabledRecipe.getRecipe();
                CachedShapelessRecipe recipe = null;
                if (irecipe instanceof ShapelessRecipes)
                    recipe = shapelessRecipe((ShapelessRecipes) irecipe);
                else if (irecipe instanceof ShapelessOreRecipe)
                    recipe = forgeShapelessRecipe((ShapelessOreRecipe) irecipe);

                if (recipe == null)
                    continue;

                arecipes.add(recipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (DisabledRecipe disabledRecipe: NEIDimensionGuardConfig.disabledRecipes) {
            IRecipe irecipe = disabledRecipe.getRecipe();
            CachedShapelessRecipe recipe = null;
            if (irecipe instanceof ShapelessRecipes)
                recipe = shapelessRecipe((ShapelessRecipes) irecipe);
            else if (irecipe instanceof ShapelessOreRecipe)
                recipe = forgeShapelessRecipe((ShapelessOreRecipe) irecipe);

            if (recipe == null)
                continue;

            if (recipe.contains(recipe.ingredients, ingredient)) {
                recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                arecipes.add(recipe);
            }
        }
    }

    private ShapelessRecipeHandler.CachedShapelessRecipe shapelessRecipe(ShapelessRecipes recipe) {
        return recipe.recipeItems == null?null:new ShapelessRecipeHandler.CachedShapelessRecipe(recipe.recipeItems, recipe.getRecipeOutput());
    }
}
