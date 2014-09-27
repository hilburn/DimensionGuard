package com.hilburn.dimensionguard.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class TickHandler {
	private int tickCount=0;
	private int triggerTick=20;
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (tickCount++==triggerTick){
			DisabledHandler.scanInventory(event.player);
			tickCount=0;
		}
	}
}
