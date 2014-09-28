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
	public static ArrayList<String> entityBlacklist;
	public static ArrayList<String> entityWhitelist;
	//@SuppressWarnings("unchecked")
	public static void init(File file)
	{
		Configuration config = new Configuration(file);
		blackList = new ArrayList<String>();
		whiteList = new ArrayList<String>();
		entityBlacklist = new ArrayList<String>();
		entityWhitelist = new ArrayList<String>();
		config.load();
		
		ConfigCategory blocksAndItems = config.getCategory("blocks and items");
		blocksAndItems.setComment("Format is 'modid:block/item(:meta/damage optional)' followed by a comma delimited list of dimensions\n"
							+	"A blank metadata/damage field defaults to 0, alternatively * can be used as a wildcard in modid or \n"
							+ 	"the object name to select multiple blocks, and in the meta/damage to select all values\n"
							+ 	"eg '*:*wool:*' selects every block with 'wool' in it's unlocalized name from any mod in every colour\n"
							+ 	"Dimensions can be defined as single dimensions (0), ranges (0:5), and more than or less than (0++/1--)");

		Property blacklistP = config.get("Blocks and Items", "blacklist", new String[] {});
		blacklistP.comment = "Block and item identifiers and dimension(s) blacklisted - add each new element on a separate line.\n"
							+ "Format is 'modid:name(:metadata optional),dim1...'";
		//for (String i : blacklistP.getStringList()) PlaceHandler.disabled.add(new BlacklistBlock(i));
		blackList.addAll(Arrays.asList(blacklistP.getStringList()));
		
		Property whitelistP = config.get("Blocks and Items", "whitelist", new String[] {});
		whitelistP.comment = "Block and item identifiers and dimension(s) whitelisted - add each new element on a separate line.\n"
							+ "Format is 'modid:blockname(:metadata optional),dim1...'";
		whiteList.addAll(Arrays.asList(whitelistP.getStringList()));

		ConfigCategory entity = config.getCategory("Entity");
		entity.setComment("Format is 'entity name, dim1, dim2...'\n"
							+	"* can be used as a wildcard in entity name to select multiples\n"
							+ 	"eg '*spider*' selects every form of spider - capitalisation is not important\n"
							+ 	"Dimensions can be defined as single dimensions (0), ranges (0:5), and more than or less than (0++/1--)");

		Property blackEntityP = config.get("Entity", "blacklist", new String[] {});
		blackEntityP.comment = "Entity name and dimension(s) blacklisted - add each new element on a separate line.\n"
							+ "Format is 'entity name,dim1...'";
		entityBlacklist.addAll(Arrays.asList(blackEntityP.getStringList()));
		
		Property whiteEntityP = config.get("Entity", "whitelist", new String[] {});
		whiteEntityP.comment = "Entity name and dimension(s) whitelisted - add each new element on a separate line.\n"
							+ "Format is 'entity name,dim1...'";
		entityWhitelist.addAll(Arrays.asList(whiteEntityP.getStringList()));
		

		
		config.save();
	}
}
