package com.hilburn.dimensionguard.items;

import cpw.mods.fml.common.registry.GameRegistry;
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
		GameRegistry.registerItem(disable=new DisableItem(),"disable");
	}
}
