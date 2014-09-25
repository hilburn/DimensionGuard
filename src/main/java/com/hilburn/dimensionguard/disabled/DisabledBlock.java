package com.hilburn.dimensionguard.disabled;

import com.hilburn.dimensionguard.Logger;

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class DisabledBlock {
	protected Item item;
	public DisabledBlock(String init){
		String modName=init.substring(0, init.indexOf(':'));
		String blockName=init.substring(init.indexOf(':')+1, init.indexOf(','));
		item = Item.getItemFromBlock(GameRegistry.findBlock(modName,blockName));
		if (item==null)Logger.log("Block " + modName +":"+blockName+" not found, skipping.");
	}
	
	public Item getItem(){return item;}
	
	public boolean isDisabled(int dim){
		return false;
	}
}
