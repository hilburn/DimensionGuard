package com.hilburn.dimensionguard.disabled;

import java.util.ArrayList;

import net.minecraft.item.Item;

import com.hilburn.dimensionguard.Logger;

import cpw.mods.fml.common.registry.GameRegistry;
/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class DisabledBlock {
	protected Item item;
	protected ArrayList<Integer> dimensions = new ArrayList<Integer>();
	public DisabledBlock(String init){
		String[] splitString = init.split(",");
		if (splitString.length<2) {
			Logger.log("Insufficient data for meaningful action: "+init);
			return;
		}
		String modName=splitString[0].substring(0, splitString[0].indexOf(':'));
		String blockName=splitString[0].substring(splitString[0].indexOf(':')+1);
		item = Item.getItemFromBlock(GameRegistry.findBlock(modName,blockName));
		if (item==null){
			Logger.log("Block " + splitString[0]+" not found, skipping.");
			return;
		}
		getDimensions(splitString);
	}
	
	private void getDimensions(String[] splitString){
		int lower;
		int upper;
		for (int i=1;i<splitString.length;i++){
			String dim = splitString[i];
			if (dim.contains(":")){
				lower=safeParseInt(dim.substring(0, dim.indexOf(':')));
				upper=safeParseInt(dim.substring(dim.indexOf(':')+1));
				if (lower>Integer.MIN_VALUE&&upper>Integer.MIN_VALUE){
					if (lower<=upper)addRange(lower,upper);
					else addRange(upper,lower);
				}
			}else if (dim.contains("++")){
				lower=safeParseInt(dim.substring(0, dim.indexOf("++")));
				if (lower>Integer.MIN_VALUE)addRange(lower,Integer.MAX_VALUE);
			}else if (dim.contains("--")){
				upper=safeParseInt(dim.substring(0, dim.indexOf("--")));
				if (upper>Integer.MIN_VALUE)addRange(Integer.MIN_VALUE,upper);
			}else{
				lower=safeParseInt(dim);
				if (lower>Integer.MIN_VALUE)addRange(lower,lower);
			}
		}
	}
	
	private int safeParseInt(String s){
		try{
			return Integer.parseInt(s.trim());
		}catch(NumberFormatException nfe) {};
		return Integer.MIN_VALUE;
	}
	
	private void addRange(int a, int b){
		dimensions.add(a);
		dimensions.add(b);
	}
	
	public Item getItem(){return item;}
	
	public boolean isDisabled(int dim){
		for (int i=0;i<dimensions.size();i+=2){
			if (dimensions.get(i)<=dim && dim<=dimensions.get(i+1)) return true;
		}
		return false;
	}
}
