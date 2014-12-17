package dimensionguard.nei;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.recipe.ShapedRecipeHandler;
import dimensionguard.items.DisabledRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Iterator;

public class DisabledShapedRecipeHandler extends ShapedRecipeHandler
{

    public void loadCraftingRecipes(String outputId, Object... results) {
        if(outputId.equals("crafting")) {
            for (DisabledRecipe disabledRecipe: NEIDimensionGuardConfig.disabledRecipes)
            {
                IRecipe irecipe = disabledRecipe.getRecipe();
                ShapedRecipeHandler.CachedShapedRecipe recipe = null;
                if(irecipe instanceof ShapedRecipes) {
                    recipe = new ShapedRecipeHandler.CachedShapedRecipe((ShapedRecipes)irecipe);
                } else if(irecipe instanceof ShapedOreRecipe) {
                    recipe = this.forgeShapedRecipe((ShapedOreRecipe)irecipe);
                }

                if(recipe != null) {
                    recipe.computeVisuals();
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
                CachedShapedRecipe recipe = null;
                IRecipe irecipe = disabledRecipe.getRecipe();
                if (irecipe instanceof ShapedRecipes)
                    recipe = new CachedShapedRecipe((ShapedRecipes) irecipe);
                else if (irecipe instanceof ShapedOreRecipe)
                    recipe = forgeShapedRecipe((ShapedOreRecipe) irecipe);

                if (recipe == null)
                    continue;

                recipe.computeVisuals();
                arecipes.add(recipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (DisabledRecipe disabledRecipe: NEIDimensionGuardConfig.disabledRecipes) {
            IRecipe irecipe = disabledRecipe.getRecipe();
            CachedShapedRecipe recipe = null;
            if (irecipe instanceof ShapedRecipes)
                recipe = new CachedShapedRecipe((ShapedRecipes) irecipe);
            else if (irecipe instanceof ShapedOreRecipe)
                recipe = forgeShapedRecipe((ShapedOreRecipe) irecipe);

            if (recipe == null || !recipe.contains(recipe.ingredients, ingredient.getItem()))
                continue;

            recipe.computeVisuals();
            if (recipe.contains(recipe.ingredients, ingredient)) {
                recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                arecipes.add(recipe);
            }
        }
    }
}
