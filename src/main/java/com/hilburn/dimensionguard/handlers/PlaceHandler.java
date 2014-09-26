package com.hilburn.dimensionguard.handlers;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.hilburn.dimensionguard.Logger;
import com.hilburn.dimensionguard.disabled.DisabledBlock;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class PlaceHandler {
	
	public static ArrayList<DisabledBlock> disabled = new ArrayList<DisabledBlock>();
	
	@SubscribeEvent
	public void PlayerInteract(PlayerInteractEvent event){
		if (event.action==Action.RIGHT_CLICK_BLOCK){
			ItemStack heldItem=event.entityPlayer.getCurrentEquippedItem();
			if (heldItem!=null){
				if (event.entityPlayer.isSneaking()||!event.world.getBlock(event.x, event.y, event.z)
						.onBlockActivated(event.world, event.x, event.y, event.z, event.entityPlayer, event.face, 0.5F, 0.5F, 0.5F)){//(event.world.getTileEntity(event.x, event.y, event.z)==null)){
					if (isDisabled(heldItem.getItem(),event.entityPlayer.dimension)) {
						if (event.world.isRemote)Logger.chatLog(event.entityPlayer,"[DimensionGuard] Placing "+heldItem.getDisplayName()+" in Dimension "+ event.entityPlayer.dimension+ " has been disabled.");
						event.setCanceled(true);
						//Logger.log(event.entityPlayer.getLookVec().xCoord+","+event.entityPlayer.getLookVec().yCoord+","+event.entityPlayer.getLookVec().zCoord);
					}
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
