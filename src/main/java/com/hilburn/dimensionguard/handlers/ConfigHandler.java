package com.hilburn.dimensionguard.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class ConfigHandler {
	public static ArrayList<String> blackList;
	public static ArrayList<String> whiteList;
	//@SuppressWarnings("unchecked")
	public static void init(File file)
	{
		Configuration config = new Configuration(file);
		blackList = new ArrayList<String>();
		whiteList = new ArrayList<String>();
		config.load();
		
		ConfigCategory instructions = config.getCategory("Formatting Instructions");
		instructions.setComment("Format is 'modid:block/item(:meta/damage optional)' followed by a comma delimited list of dimensions\n"
							+	"A blank metadata/damage field defaults to 0, alternatively * can be used as a wildcard in modid or \n"
							+ 	"the object name to select multiple blocks, and in the meta/damage to select all values\n"
							+ 	"eg '*:*wool:*' selects every block with 'wool' in it's unlocalized name from any mod in every colour\n"
							+ 	"Dimensions can be defined as single dimensions (0), ranges (0:5), and more than or less than (0++/1--)");
		
		ConfigCategory blacklist = config.getCategory("blacklist");
		blacklist.setComment("Set blacklists here (can only not place in certain dimensions)");

		Property blacklistP = config.get("blacklist", "blacklist", new String[] {});
		blacklistP.comment = "Block and item identifiers and dimension(s) blacklisted - add each new element on a separate line.\n"
							+ "Format is 'modid:name(:metadata optional),dim1...'";
		//for (String i : blacklistP.getStringList()) PlaceHandler.disabled.add(new BlacklistBlock(i));
		blackList.addAll(Arrays.asList(blacklistP.getStringList()));
		
//		Property blacklistItemP = config.get("blacklist", "item", new String[] {});
//		blacklistItemP.comment = "Item ids and dimension(s) blacklisted - add each new item on a separate line.\n"
//							+ "Format is 'modid:itemname(:damage optional),dim1,dim2...' etc.";
//		//for (String i : blacklistP.getStringList()) PlaceHandler.disabled.add(new BlacklistBlock(i));
//		blacklistItem.addAll(Arrays.asList(blacklistItemP.getStringList()));
		
		ConfigCategory whitelist = config.getCategory("whitelist");
		whitelist.setComment("Set whitelists here (can only place in certain dimensions)");

		Property whitelistP = config.get("whitelist", "whitelist", new String[] {});
		whitelistP.comment = "Block and item identifiers and dimension(s) blacklisted - add each new element on a separate line.\n"
							+ "Format is 'modid:blockname(:metadata optional),dim1...'";
		whiteList.addAll(Arrays.asList(whitelistP.getStringList()));
		
//		Property whitelistItemP = config.get("whitelist", "item", new String[] {});
//		whitelistItemP.comment = "Item ids and dimension(s) whitelisted - add each new item on a separate line.\n"
//							+ "Format is 'modid:itemname(:damage optional),dim1,dim2...' etc.";
//		whitelistItem.addAll(Arrays.asList(whitelistItemP.getStringList()));
		
		config.save();
	}
}
