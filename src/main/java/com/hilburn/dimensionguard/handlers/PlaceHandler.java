package com.hilburn.dimensionguard.handlers;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.hilburn.dimensionguard.disabled.DisabledBlock;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlaceHandler {
	
	public static ArrayList<DisabledBlock> disabled = new ArrayList<DisabledBlock>();
	
	@SubscribeEvent
	public void PlayerInteract(PlayerInteractEvent event){
		
		if (event.action==Action.RIGHT_CLICK_BLOCK){
			ItemStack heldItem=event.entityPlayer.getCurrentEquippedItem();
			if (heldItem!=null){
				if (event.entityPlayer.isSneaking()||(event.world.getTileEntity(event.x, event.y, event.z)==null)){
					if (isDisabled(heldItem.getItem(),event.entityPlayer.dimension)) event.setCanceled(true);
				}
			}
		}
	}
	
	private boolean isDisabled(Item item, int dim){
		for (DisabledBlock current:disabled){
			if (current.getItem()==item && current.isDisabled(dim)) return true;
		}
		return false;
	}
	
	
}
