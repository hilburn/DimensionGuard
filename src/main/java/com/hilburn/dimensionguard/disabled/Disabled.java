package com.hilburn.dimensionguard.disabled;

import java.util.ArrayList;

import net.minecraft.item.Item;
/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class Disabled {
	protected Item item;
	protected int meta;
	protected ArrayList<Integer> dimensions = new ArrayList<Integer>();
	protected boolean blacklist;
	public Disabled(Item disable, String metadata, String[] dim, boolean isBlacklist){
		item=disable;
		meta=safeParseInt(metadata);
		if (meta<-1)meta=0;
		blacklist=isBlacklist;
		getDimensions(dim);
	}
	
	private void getDimensions(String[] splitString){
		int lower;
		int upper;
		for (String dim:splitString){
			if (dim.contains(":")){
				lower=safeParseInt(dim.split(":")[0]);
				upper=safeParseInt(dim.split(":")[1]);
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
	
	public boolean isDisabled(int dim){
		for (int i=0;i<dimensions.size();i+=2){
			if (dimensions.get(i)<=dim && dim<=dimensions.get(i+1)) return !(true^blacklist);
		}
		return !(false^blacklist);
	}
	
	public Item getItem(){return item;}
	public int getMeta(){return meta;}
	public boolean isEmpty(){return dimensions.isEmpty();}
}
