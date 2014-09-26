
package com.hilburn.dimensionguard;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;

import com.hilburn.dimensionguard.handlers.ConfigHandler;
import com.hilburn.dimensionguard.handlers.PlaceHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = ModInformation.MODID, name = ModInformation.NAME, version = ModInformation.VERSION)
/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class DimensionGuard {
	PlaceHandler blockEvent = new PlaceHandler();
	File config;
	@Instance(ModInformation.MODID)
	public static DimensionGuard instance = new DimensionGuard();
	
	@EventHandler 
	public void preInit(FMLPreInitializationEvent event){
		config=event.getSuggestedConfigurationFile();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		ConfigHandler.init(config);
		MinecraftForge.EVENT_BUS.register(blockEvent);
	}
	
}