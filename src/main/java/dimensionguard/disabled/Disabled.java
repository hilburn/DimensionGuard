package dimensionguard.disabled;

import net.minecraft.item.Item;

import java.util.ArrayList;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class Disabled
{
	protected Item item;
	protected int damage;
	protected ArrayList<Integer> dimensions = new ArrayList<Integer>();
	protected boolean blacklist;


	public Disabled(Item disable, String damage, String[] dim, boolean isBlacklist){
		item=disable;
		this.damage =safeParseInt(damage);
		if (this.damage <-1) this.damage =0;
		blacklist=isBlacklist;
		getDimensions(dim);
	}
	
	public Disabled(String[] dim, boolean isBlacklist){
		blacklist=isBlacklist;
		getDimensions(dim);
	}
	
	public Disabled(String metadata, String[] dim, boolean isBlacklist){
		damage =safeParseInt(metadata);
		if (damage <-1) damage =0;
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
	
	public boolean dimensionMatch(int dim){
		for (int i=0;i<dimensions.size();i+=2){
			if (dimensions.get(i)<=dim && dim<=dimensions.get(i+1)) return !(true^blacklist);
		}
		return !(false^blacklist);
	}

	public boolean isDisabled(int damage, int dim){
		return damageMatch(damage) && dimensionMatch(dim);
	}

	public boolean damageMatch(int damage){
		if (this.damage ==-1|| this.damage ==damage)return true;
		return false;
	}
	
	public Item getItem(){return item;}
	public int getDamage(){return damage;}
	public boolean isEmpty(){return dimensions.isEmpty();}
}
