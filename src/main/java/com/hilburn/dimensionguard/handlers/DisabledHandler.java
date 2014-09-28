package com.hilburn.dimensionguard.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
public class DisabledHandler {
	public static ArrayList<Disabled> disabledBlocks;
	public static Map<String,ArrayList<Disabled>> disabledHash = new Hashtable<String,ArrayList<Disabled>>();
	private static ArrayList<String> registeredBlocks;
	private static ArrayList<String> registeredItems;
	
	public static void init(){
		//Logger.log(EntityList.classToStringMapping.toString());
		ConfigHandler.init(DimensionGuard.config);
		disabledBlocks = new ArrayList<Disabled>();
		registeredBlocks=new ArrayList<String>();
		registeredItems=new ArrayList<String>();
		
		for (Object key:GameData.getBlockRegistry().getKeys()){
			registeredBlocks.add((String) key);
		}
		for (Object key:GameData.getItemRegistry().getKeys()){
			registeredItems.add((String) key);
			//Logger.log((String)key);
		}
		addDisabledBlocks(true);
		addDisabledBlocks(false);
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
			if (blockID.contains("*")){
				blockID=blockID.replaceAll("\\*", ".*");
				Pattern blockPattern = Pattern.compile(blockID,Pattern.CASE_INSENSITIVE);
				for (String block:registeredItems){
					if (blockPattern.matcher(block).find()) wildcardMatch.add(block);
				}
			}else{
				if (registeredBlocks.contains(blockID))wildcardMatch.add(blockID);
				else Logger.log("Block "+blockID+" is not registered");
			}
			Disabled newDisabled = new Disabled(metadata,dimensions,blacklist);
			for (String match:wildcardMatch){
//				String[] blockData = match.split(":");
//				Disabled newDisabled = new Disabled(Item.getItemFromBlock(GameRegistry.findBlock(blockData[0], blockData[1])),metadata,dimensions,blacklist);//blacklist?new BlacklistBlock(Item.getItemFromBlock(GameRegistry.findBlock(blockInfo[0], blockInfo[1])),metadata,dimensions):new WhitelistBlock(Item.getItemFromBlock(GameRegistry.findBlock(blockInfo[0], blockInfo[1])),metadata,dimensions);
//				if (newDisabled.isEmpty()){
//					Logger.log(match+" has no valid dimensions");
//					continue;
//				}else if (newDisabled.getItem()==null){
//					Logger.log(match+" is not registered");
//					continue;
//				}
				ArrayList<Disabled> temp = new ArrayList<Disabled>(Arrays.asList(newDisabled));
				if (disabledHash.get(match)!=null)temp.addAll(disabledHash.get(match));
				disabledHash.put(match, temp);
				//Logger.log(match);
				//disabledBlocks.add(newDisabled);
			}
		}
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
	
	public static boolean isDisabledBlock(Item item, int meta, int dim){
		for (Disabled current:disabledBlocks){
			if (current.getItem()==item && (current.getMeta()==-1||current.getMeta()==meta)&&current.isDisabled(dim)) return true;
		}
		return false;
	}
	
	public static void scanInventory(EntityPlayer player){
		for (int i=0;i<player.inventory.getSizeInventory();i++){
			ItemStack thisStack=player.inventory.getStackInSlot(i);
			if (thisStack!=null){
				if(thisStack.stackTagCompound==null){
					thisStack.stackTagCompound=new NBTTagCompound();
				}
				if(!thisStack.stackTagCompound.hasKey("DimensionGuard"))
					thisStack.stackTagCompound.setTag("DimensionGuard", new NBTTagCompound());
				if(!thisStack.stackTagCompound.getCompoundTag("DimensionGuard").hasKey("CanBeDisabled"))
					thisStack.stackTagCompound.getCompoundTag("DimensionGuard").setBoolean("CanBeDisabled", 
							DisabledHandler.canBeDisabled(GameRegistry.findUniqueIdentifierFor(thisStack.getItem()).toString(), thisStack.getItemDamage()));
				if(thisStack.stackTagCompound.getCompoundTag("DimensionGuard").getBoolean("CanBeDisabled")||thisStack.getItem()==ModItems.disable){
					if(!thisStack.stackTagCompound.getCompoundTag("DimensionGuard").hasKey("LastDimChecked"))
						thisStack.stackTagCompound.getCompoundTag("DimensionGuard").setInteger("LastDimChecked",Integer.MIN_VALUE);
					if(thisStack.stackTagCompound.getCompoundTag("DimensionGuard").getInteger("LastDimChecked")!=player.dimension){
						if (thisStack.getItem()==ModItems.disable){
							ItemStack storeStack = DisableItem.recoverItemStack(thisStack);
							//Logger.log(storeStack.getDisplayName());
							if (!DisabledHandler.isDisabled(GameRegistry.findUniqueIdentifierFor(storeStack.getItem()).toString(), storeStack.getItemDamage(),player.dimension)){
								player.inventory.setInventorySlotContents(i, storeStack);
							}
						}else{
							if (DisabledHandler.isDisabled(GameRegistry.findUniqueIdentifierFor(thisStack.getItem()).toString(), thisStack.getItemDamage(),player.dimension)){
								player.inventory.setInventorySlotContents(i, DisableItem.storeItem(new ItemStack(ModItems.disable,1), thisStack));
							}else{
								thisStack.stackTagCompound.getCompoundTag("DimensionGuard").setInteger("LastDimChecked",player.dimension);
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isDisabledEntity(Class entityClass, int dim){
		return false;
	}
}
