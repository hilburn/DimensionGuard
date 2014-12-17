package dimensionguard.handlers;

import java.util.*;
import java.util.regex.Pattern;

import cpw.mods.fml.relauncher.ReflectionHelper;
import dimensionguard.items.DisabledRecipe;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import dimensionguard.DimensionGuard;
import dimensionguard.utils.Logger;
import dimensionguard.disabled.Disabled;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/

public class DisabledHandler {
	public static Map<Item,ArrayList<Disabled>> disabledItemHash = new HashMap<Item, ArrayList<Disabled>>();
	public static Map<Class,ArrayList<Disabled>> disabledEntityHash = new HashMap<Class, ArrayList<Disabled>>();
	
	public static void init(){
		ConfigHandler.init(DimensionGuard.config);

		addDisabledItems(true);
		addDisabledItems(false);
		addDisabledEntity(true);
		addDisabledEntity(false);
		disableRecipes();
	}

	private static void disableRecipes()
	{
		ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
		for (Iterator<IRecipe> itr = CraftingManager.getInstance().getRecipeList().iterator(); itr.hasNext();)
		{
			IRecipe recipe = itr.next();
			if (!(recipe instanceof DisabledRecipe))
			{
				ItemStack output = recipe.getRecipeOutput();
				if (output == null || output.getItem() == null) continue;
				if (isDisabledStack(output))
				{
					recipes.add(new DisabledRecipe(recipe));
				}
				else recipes.add(recipe);
			}
		}
		ReflectionHelper.setPrivateValue(CraftingManager.class, CraftingManager.getInstance(),recipes,"recipes");
	}

	private static void addDisabledItems(boolean blacklist){
		ArrayList<String> strings = blacklist?ConfigHandler.blackList:ConfigHandler.whiteList;
		String[] splitString;
		String[] dimensions;
		for (String disableItem: strings){
			splitString = disableItem.split(",");
			if (splitString.length<2){
				Logger.log("Insufficient data for meaningful action: "+disableItem);
				continue;
			}
			dimensions = Arrays.copyOfRange(splitString, 1, splitString.length);
			String[] itemInfo = splitString[0].split(":");
			if (itemInfo.length<2){
				Logger.log("Invalid block: "+splitString[0]);
				continue;
			}
			String itemID=itemInfo[0]+":"+itemInfo[1];
			String damage=itemInfo.length==3?itemInfo[2]:"0"; 	//Defaults to 0 if no damage is declared
			if (damage.contains("*")) damage="-1";				//If wildcard is present set to -1 (any)

			ArrayList<Item> matches = new ArrayList<Item>();
			if (itemID.contains("*"))itemID=itemID.replaceAll("\\*", ".*");
			Pattern blockPattern = Pattern.compile(itemID,Pattern.CASE_INSENSITIVE);
			for (Object key:GameData.getItemRegistry().getKeys()){
				if (blockPattern.matcher(key.toString()).matches())
				{
					matches.add(GameData.getItemRegistry().getObject(key.toString()));
				}
			}
			if (matches.isEmpty())Logger.log(itemID+" has no registered matches");
			Disabled newDisabled = new Disabled(damage,dimensions,blacklist);
			for (Item match:matches){
				ArrayList<Disabled> temp = new ArrayList<Disabled>(Arrays.asList(newDisabled));
				if (disabledItemHash.get(match)!=null)temp.addAll(disabledItemHash.get(match));
				disabledItemHash.put(match, temp);
			}
		}
	}

	private static void addDisabledEntity(boolean blacklist){
		ArrayList<String> strings = blacklist?ConfigHandler.entityBlacklist:ConfigHandler.entityWhitelist;
		String[] splitString;
		String[] dimensions;
		for (String disableEntity: strings){
			splitString = disableEntity.split(",");
			if (splitString.length<2){
				Logger.log("Insufficient data for meaningful action: "+disableEntity);
				continue;
			}
			dimensions = Arrays.copyOfRange(splitString, 1, splitString.length);
			String entityID=splitString[0];
			ArrayList<String> wildcardMatch=new ArrayList<String>();
			if (entityID.contains("*"))entityID=entityID.replaceAll("\\*", ".*");
			Pattern entityPattern = Pattern.compile(entityID,Pattern.CASE_INSENSITIVE);
			for (Object entity:EntityList.stringToClassMapping.keySet()){
				if (entityPattern.matcher((String)entity).matches()) wildcardMatch.add((String)entity);
			}
			Disabled newDisabled = new Disabled(dimensions,blacklist);
			for (String match:wildcardMatch){
				ArrayList<Disabled> temp = new ArrayList<Disabled>(Arrays.asList(newDisabled));
				if (disabledEntityHash.get(match)!=null)temp.addAll(disabledEntityHash.get(match));
				disabledEntityHash.put((Class)EntityList.stringToClassMapping.get(match), temp);
			}
		}
	}

	public static boolean isDisabledStack(ItemStack stack)
	{
		ArrayList<Disabled> disabled = disabledItemHash.get(stack.getItem());
		if (disabled==null) return false;
		for (Disabled check: disabled)
		{
			if (check.damageMatch(stack.getItemDamage())) return true;
		}
		return false;
	}

	public static boolean isDisabledStack(ItemStack stack, int dim)
	{
		ArrayList<Disabled> disabled = disabledItemHash.get(stack.getItem());
		if (disabled==null) return false;
		for (Disabled check: disabled)
		{
			if (check.isDisabled(stack.getItemDamage(),dim)) return true;
		}
		return false;
	}


	public static boolean isDisabledEntity(Class entityClass, int dim){
		ArrayList<Disabled> disabled = disabledEntityHash.get(entityClass);
		if (disabled!=null){
			for (Disabled set:disabled)if (set.dimensionMatch(dim))return true;
		}
		return false;
	}
}
