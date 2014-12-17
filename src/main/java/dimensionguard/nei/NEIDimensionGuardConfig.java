package dimensionguard.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.TemplateRecipeHandler;
import dimensionguard.items.DisabledRecipe;
import dimensionguard.reference.Reference;

import java.util.ArrayList;

public class NEIDimensionGuardConfig implements IConfigureNEI
{
    public static ArrayList<DisabledRecipe> disabledRecipes = new ArrayList<DisabledRecipe>();

    @Override
    public void loadConfig()
    {
        TemplateRecipeHandler handler = new DisabledShapedRecipeHandler();
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);

        handler = new DisabledShapelessRecipeHandler();
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
    }

    @Override
    public String getName()
    {
        return Reference.NAME;
    }

    @Override
    public String getVersion()
    {
        return "v0.1";
    }
}
