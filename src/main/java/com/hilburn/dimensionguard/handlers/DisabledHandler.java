package com.hilburn.dimensionguard.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import net.minecraft.item.Item;

import com.hilburn.dimensionguard.DimensionGuard;
import com.hilburn.dimensionguard.Logger;
import com.hilburn.dimensionguard.disabled.DisabledBlock;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class DisabledHandler {
	public static ArrayList<DisabledBlock> disabledBlocks;
	private static ArrayList<String> registeredBlocks;
	private static ArrayList<String> registeredItems;
	
	public static void init(){
		ConfigHandler.init(DimensionGuard.config);
		disabledBlocks = new ArrayList<DisabledBlock>();
		registeredBlocks=new ArrayList<String>();
		registeredItems=new ArrayList<String>();
		
		for (Object key:GameData.getBlockRegistry().getKeys()){
			registeredBlocks.add((String) key);
		}
		for (Object key:GameData.getItemRegistry().getKeys()){
			registeredItems.add((String) key);
		}
		addDisabledBlocks(true);
		addDisabledBlocks(false);
	}
	
	private static void addDisabledBlocks(boolean blacklist){
		ArrayList<String> strings = blacklist?ConfigHandler.blacklistBlock:ConfigHandler.whitelistBlock;
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
				blockID.replaceAll("/*", "/./*");
				Pattern blockPattern = Pattern.compile(blockID,Pattern.CASE_INSENSITIVE);
				for (String block:registeredBlocks){
					if (blockPattern.matcher(block).find()) wildcardMatch.add(block);
				}
			}else{
				if (registeredBlocks.contains(blockID))wildcardMatch.add(blockID);
				else Logger.log("Block "+blockID+" is not registered");
			}

			for (String match:wildcardMatch){
				String[] blockData = match.split(":");
				DisabledBlock newDisabled = new DisabledBlock(Item.getItemFromBlock(GameRegistry.findBlock(blockData[0], blockData[1])),metadata,dimensions,blacklist);//blacklist?new BlacklistBlock(Item.getItemFromBlock(GameRegistry.findBlock(blockInfo[0], blockInfo[1])),metadata,dimensions):new WhitelistBlock(Item.getItemFromBlock(GameRegistry.findBlock(blockInfo[0], blockInfo[1])),metadata,dimensions);
				if (newDisabled.isEmpty()){
					Logger.log(match+" has no valid dimensions");
					continue;
				}else if (newDisabled.getItem()==null){
					Logger.log(match+" is not registered");
					continue;
				}
				disabledBlocks.add(newDisabled);
			}
		}
	}
	
	public static boolean isDisabledBlock(Item item, int meta, int dim){
		for (DisabledBlock current:disabledBlocks){
			if (current.getItem()==item && (current.getMeta()==-1||current.getMeta()==meta)&&current.isDisabled(dim)) return true;
		}
		return false;
	}
	
//TODO: Future feature - Disabled Items
}
