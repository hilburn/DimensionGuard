package com.hilburn.dimensionguard.handlers;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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
	private int triggerTick=50;
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (tickCount++==triggerTick){
			for (int i=0;i<event.player.inventory.getSizeInventory();i++){
				if (event.player.inventory.getStackInSlot(i)!=null){
					//event.player.inventory.setInventorySlotContents(i, new ItemStack(Blocks.sand));
				}
			}
			tickCount=0;
		}
	}
}
