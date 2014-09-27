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
	public static ArrayList<String> blacklistBlock;
	public static ArrayList<String> whitelistBlock;
	public static ArrayList<String> blacklistItem;
	public static ArrayList<String> whitelistItem;
	//@SuppressWarnings("unchecked")
	public static void init(File file)
	{
		Configuration config = new Configuration(file);
		blacklistBlock = new ArrayList<String>();
		whitelistBlock = new ArrayList<String>();
		blacklistItem = new ArrayList<String>();
		whitelistItem = new ArrayList<String>();
		config.load();
		
		ConfigCategory instructions = config.getCategory("Formatting Instructions");
		instructions.setComment("Format is 'modid:block/item(:meta/damage optional)' followed by a comma delimited list of dimensions\n"
							+	"A blank metadata/damage field defaults to 0, alternatively * can be used as a wildcard in modid or \n"
							+ 	"the object name to select multiple blocks, and in the meta/damage to select all values\n"
							+ 	"eg '*:*wool:*' selects every block with 'wool' in it's unlocalized name from any mod in every colour\n"
							+ 	"Dimensions can be defined as single dimensions (0), ranges (0:5), and more than or less than (0++/1--)");
		
		ConfigCategory blacklist = config.getCategory("blacklist");
		blacklist.setComment("Set blacklists here (can only not place in certain dimensions)");

		Property blacklistBlockP = config.get("blacklist", "block", new String[] {});
		blacklistBlockP.comment = "Block ids and dimension(s) blacklisted - add each new block on a separate line.\n"
							+ "Format is 'modid:blockname(:metadata optional),dim1...'";
		//for (String i : blacklistP.getStringList()) PlaceHandler.disabled.add(new BlacklistBlock(i));
		blacklistBlock.addAll(Arrays.asList(blacklistBlockP.getStringList()));
		
		Property blacklistItemP = config.get("blacklist", "item", new String[] {});
		blacklistItemP.comment = "Item ids and dimension(s) blacklisted - add each new item on a separate line.\n"
							+ "Format is 'modid:itemname(:damage optional),dim1,dim2...' etc.";
		//for (String i : blacklistP.getStringList()) PlaceHandler.disabled.add(new BlacklistBlock(i));
		blacklistItem.addAll(Arrays.asList(blacklistItemP.getStringList()));
		
		ConfigCategory whitelist = config.getCategory("whitelist");
		whitelist.setComment("Set whitelists here (can only place in certain dimensions)");

		Property whitelistBlockP = config.get("whitelist", "block", new String[] {});
		whitelistBlockP.comment = "Block ids and dimension(s) whitelisted - add each new block on a separate line\n"
						+ "Format is 'modid:blockname(:damage optional),dim1...'";
		whitelistBlock.addAll(Arrays.asList(whitelistBlockP.getStringList()));
		
		Property whitelistItemP = config.get("whitelist", "item", new String[] {});
		whitelistItemP.comment = "Item ids and dimension(s) whitelisted - add each new item on a separate line.\n"
							+ "Format is 'modid:itemname(:damage optional),dim1,dim2...' etc.";
		whitelistItem.addAll(Arrays.asList(whitelistItemP.getStringList()));
		
		config.save();
	}
}
