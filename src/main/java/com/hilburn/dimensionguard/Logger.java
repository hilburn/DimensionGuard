package com.hilburn.dimensionguard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class Logger {

	public static void log(String text)
	{
		System.out.println("[DimensionGuard] " + text);
	}
	
	public static void chatLog(EntityPlayer player, String text){
		player.addChatComponentMessage(new ChatComponentText(text));
	}

}