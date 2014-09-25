
package com.hilburn.dimensionguard;

import net.minecraftforge.common.MinecraftForge;

import com.hilburn.dimensionguard.handlers.PlaceHandler;
import com.hilburn.dimensionguard.handlers.ConfigHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
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
	@Instance(ModInformation.MODID)
	public static DimensionGuard instance = new DimensionGuard();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(blockEvent);
	}
	
}