package com.hilburn.dimensionguard.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.hilburn.dimensionguard.DimensionGuard;
import com.hilburn.dimensionguard.Logger;
import com.hilburn.dimensionguard.disabled.Disabled;
import com.hilburn.dimensionguard.items.DisableItem;
import com.hilburn.dimensionguard.items.ModItems;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
@SuppressWarnings("rawtypes")
public class DisabledHandler {
	public static Map<String,ArrayList<Disabled>> disabledHash = new Hashtable<String,ArrayList<Disabled>>();
	public static Map<Class,ArrayList<Disabled>> disabledEntityHash = new Hashtable<Class,ArrayList<Disabled>>();
	//private static ArrayList<String> registeredBlocks;
	private static ArrayList<String> registeredItems;
	
	public static void init(){
		ConfigHandler.init(DimensionGuard.config);
		registeredItems=new ArrayList<String>();
		
//		for (Object key:GameData.getBlockRegistry().getKeys()){
//			registeredBlocks.add((String) key);
//		}
		for (Object key:GameData.getItemRegistry().getKeys()){
			registeredItems.add((String) key);
			//Logger.log((String)key);
		}
		addDisabledBlocks(true);
		addDisabledBlocks(false);
		addDisabledEntity(true);
		addDisabledEntity(false);
	}
	
	private static void addDisabledBlocks(boolean blacklist){
		ArrayList<String> strings = blacklist?ConfigHandler.blackList:ConfigHandler.whiteList;
		String[] splitString;
		String[] dimensions;
		for (String disableBlock: strings){
			splitString = disableBlock.split(",");
			if (splitString.length<2){
				Logger.log("Insufficient data for meaningful action: "+disableBlock);
				continue;
			}
			dimensions = Arrays.copyOfRange(splitString, 1, splitString.length);
			String[] blockInfo = splitString[0].split(":");
			if (blockInfo.length<2){
				Logger.log("Invalid block: "+splitString[0]);
				continue;
			}
			String blockID=blockInfo[0]+":"+blockInfo[1];
			String metadata=blockInfo.length==3?blockInfo[2]:"0"; 	//Defaults to 0 if no metadata is declared
			if (metadata.contains("*")) metadata="-1";				//If wildcard is present set to -1 (any)
			
			ArrayList<String> wildcardMatch=new ArrayList<String>();
			if (blockID.contains("*"))blockID=blockID.replaceAll("\\*", ".*");
			Pattern blockPattern = Pattern.compile(blockID,Pattern.CASE_INSENSITIVE);
			for (String block:registeredItems){
				if (blockPattern.matcher(block).matches()) wildcardMatch.add(block);
			}
			if (wildcardMatch.isEmpty())Logger.log(blockID+" has no registered matches");
			Disabled newDisabled = new Disabled(metadata,dimensions,blacklist);
			for (String match:wildcardMatch){
				ArrayList<Disabled> temp = new ArrayList<Disabled>(Arrays.asList(newDisabled));
				if (disabledHash.get(match)!=null)temp.addAll(disabledHash.get(match));
				disabledHash.put(match, temp);
				//Logger.log(match);
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
			//Logger.log(wildcardMatch.toString());
			Disabled newDisabled = new Disabled(dimensions,blacklist);
			for (String match:wildcardMatch){
				ArrayList<Disabled> temp = new ArrayList<Disabled>(Arrays.asList(newDisabled));
				if (disabledEntityHash.get(match)!=null)temp.addAll(disabledHash.get(match));
				disabledEntityHash.put((Class)EntityList.stringToClassMapping.get(match), temp);
			}
		}
		//Logger.log(disabledEntityHash.keySet().toString());
	}
	
	public static boolean canBeDisabled(String UID, int meta){
		ArrayList<Disabled> disabled = disabledHash.get(UID);
		if (disabled!=null){
			for (Disabled data:disabled){
				if (data!=null&&data.metaMatch(meta))return true;
			}
		}
		return false;
	}
	
	public static boolean isDisabled(String UID, int meta, int dim){
		ArrayList<Disabled> disabled = disabledHash.get(UID);
		if (disabled!=null){
			for (Disabled data:disabled){
				if (data!=null&&data.metaMatch(meta)&&data.isDisabled(dim))return true;
			}
		}
		return false;
	}
	
	public static void scanInventory(EntityPlayer player, boolean setCanBeDisabled){
		for (int i=0;i<player.inventory.getSizeInventory();i++){
			ItemStack thisStack=player.inventory.getStackInSlot(i);
			if (thisStack!=null){
				if (i<36) player.inventory.setInventorySlotContents(i, scanStack(thisStack,player.dimension,setCanBeDisabled));
				else{
					ItemStack armourStack = scanStack(thisStack,player.dimension,setCanBeDisabled);
					if (armourStack.getItem()==ModItems.disable)
					{
						player.inventory.setInventorySlotContents(i, null);
						player.inventory.addItemStackToInventory(armourStack);
					}
					else player.inventory.setInventorySlotContents(i, armourStack);
				}
			}
		}
	}
	
	public static ItemStack scanStack(ItemStack thisStack, int dim, boolean setCanBeDisabled){
		if (thisStack!=null&&thisStack.getItem()!=null)
		{
			if(thisStack.stackTagCompound==null){
				thisStack.stackTagCompound=new NBTTagCompound();
			}
			if(GameRegistry.findUniqueIdentifierFor(thisStack.getItem())==null){
				//TODO: Handle this case
				return thisStack;
			}
			if(!thisStack.stackTagCompound.hasKey("DimensionGuard"))
				thisStack.stackTagCompound.setTag("DimensionGuard", new NBTTagCompound());
			if(!thisStack.stackTagCompound.getCompoundTag("DimensionGuard").hasKey("CanBeDisabled")||setCanBeDisabled)
				thisStack.stackTagCompound.getCompoundTag("DimensionGuard").setBoolean("CanBeDisabled", 
						DisabledHandler.canBeDisabled(GameRegistry.findUniqueIdentifierFor(thisStack.getItem()).toString(), thisStack.getItemDamage()));
			if(thisStack.stackTagCompound.getCompoundTag("DimensionGuard").getBoolean("CanBeDisabled")||thisStack.getItem()==ModItems.disable){
				if(!thisStack.stackTagCompound.getCompoundTag("DimensionGuard").hasKey("LastDimChecked"))
					thisStack.stackTagCompound.getCompoundTag("DimensionGuard").setInteger("LastDimChecked",Integer.MIN_VALUE);
				if(thisStack.stackTagCompound.getCompoundTag("DimensionGuard").getInteger("LastDimChecked")!=dim){
					if (thisStack.getItem()==ModItems.disable){
						ItemStack storeStack = DisableItem.recoverItemStack(thisStack);
						//Logger.log(storeStack.getDisplayName());
						if (!DisabledHandler.isDisabled(GameRegistry.findUniqueIdentifierFor(storeStack.getItem()).toString(), storeStack.getItemDamage(),dim)){
							return storeStack;
						}
					}else{
						if (DisabledHandler.isDisabled(GameRegistry.findUniqueIdentifierFor(thisStack.getItem()).toString(), thisStack.getItemDamage(),dim)){
							return DisableItem.storeItem(new ItemStack(ModItems.disable,1), thisStack);
						}else{
							thisStack.stackTagCompound.getCompoundTag("DimensionGuard").setInteger("LastDimChecked",dim);
						}
					}
				}
			}
		}
		return thisStack;
	}
	
	public static boolean isDisabledEntity(Class entityClass, int dim){
		ArrayList<Disabled> disabled = disabledEntityHash.get(entityClass);
		if (disabled!=null){
			for (Disabled set:disabled)if (set.isDisabled(dim))return true;
		}
		return false;
	}
}
