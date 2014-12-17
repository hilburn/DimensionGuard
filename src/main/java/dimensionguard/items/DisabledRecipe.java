package dimensionguard.items;

import dimensionguard.disabled.Disabled;
import dimensionguard.handlers.DisabledHandler;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class DisabledRecipe implements IRecipe
{
    private IRecipe recipe;

    public DisabledRecipe(IRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public boolean matches(InventoryCrafting crafting, World world)
    {
        if (world==null || DisabledHandler.isDisabledStack(getRecipeOutput(),world.provider.dimensionId)) return false;
        return recipe.matches(crafting, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting)
    {
        return recipe.getCraftingResult(crafting);
    }

    @Override
    public int getRecipeSize()
    {
        return recipe.getRecipeSize();
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return recipe.getRecipeOutput();
    }
}
