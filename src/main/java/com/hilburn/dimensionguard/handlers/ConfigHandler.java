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
	//@SuppressWarnings("unchecked")
	public static void init(File file)
	{
		Configuration config = new Configuration(file);
		blacklistBlock = new ArrayList<String>();
		whitelistBlock = new ArrayList<String>();
		config.load();
		
		ConfigCategory blacklist = config.getCategory("blacklist");
		blacklist.setComment("Set blacklisted blocks here (can only not place in certain dimensions)");

		Property blacklistP = config.get("blacklist", "blacklist", new String[] {});
		blacklistP.comment = "Block ids and dimension(s) blacklisted in (comma delimited) - add each new block on a separate line.\n"
							+ "Format is 'modid:blockname(:metadata optional),dim1...'\n"
							+ "eg 'minecraft:wool:1,0' disables placement of Orange Wool in the overworld\n"
							+ "* can be used at any point as a wildcard to allow multiblock or metadata independent disabling\n"
							+ "Dimensions can be defined as single dimensions (0), ranges (0:5), and more than or less than (0++/1--)";
		//for (String i : blacklistP.getStringList()) PlaceHandler.disabled.add(new BlacklistBlock(i));
		blacklistBlock.addAll(Arrays.asList(blacklistP.getStringList()));
		
		ConfigCategory whitelist = config.getCategory("whitelist");
		whitelist.setComment("Set whitelisted blocks here (can only place in certain dimensions)");

		Property whitelistP = config.get("whitelist", "whitelist", new String[] {});
		whitelistP.comment = "Block ids and dimension(s) whitelisted in (comma delimited) - add each new block on a separate line\nFormat as above: 'modid:blockname(:metadata),dim1,dim2 etc'";
		//for (String i : whitelistP.getStringList()) PlaceHandler.disabled.add(new WhitelistBlock(i));
		whitelistBlock.addAll(Arrays.asList(whitelistP.getStringList()));
		
		config.save();
	}
}
