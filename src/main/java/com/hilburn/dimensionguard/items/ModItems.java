package com.hilburn.dimensionguard.items;

import net.minecraft.item.Item;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class ModItems {
	public static Item disable;
	public static void init(){
		disable=new DisableItem();
	}
}
