package com.hilburn.dimensionguard.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.hilburn.dimensionguard.Logger;
import com.hilburn.dimensionguard.items.DisableItem;
import com.hilburn.dimensionguard.items.ModItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class DGEventHandler {
	
	@SubscribeEvent
	public void PlayerInteract(PlayerInteractEvent event){
		if (event.action==Action.RIGHT_CLICK_BLOCK){
			ItemStack heldItem=event.entityPlayer.getCurrentEquippedItem();
			if (heldItem!=null){
				if (event.entityPlayer.isSneaking()||!event.world.getBlock(event.x, event.y, event.z)
						.onBlockActivated(event.world, event.x, event.y, event.z, event.entityPlayer, event.face, 0.5F, 0.5F, 0.5F)){//(event.world.getTileEntity(event.x, event.y, event.z)==null)){
					if (DisabledHandler.isDisabledBlock(heldItem.getItem(),heldItem.getItemDamage(),event.entityPlayer.dimension)) {
						if (event.world.isRemote)Logger.chatLog(event.entityPlayer,"[DimensionGuard] Placing "+heldItem.getDisplayName()+" in Dimension "+ event.entityPlayer.dimension+ " has been disabled.");
						event.setCanceled(true);
						//Logger.log(event.entityPlayer.getLookVec().xCoord+","+event.entityPlayer.getLookVec().yCoord+","+event.entityPlayer.getLookVec().zCoord);
					}
				}
			}
		}else if (event.action==Action.RIGHT_CLICK_AIR && event.entityPlayer.isSneaking()){
			ItemStack heldItem=event.entityPlayer.getCurrentEquippedItem();
			if (heldItem!=null&&heldItem.getItem()!=ModItems.disable){
				ItemStack newStack = DisableItem.storeItem(new ItemStack(ModItems.disable,1), heldItem);
				event.entityPlayer.inventory.decrStackSize(event.entityPlayer.inventory.currentItem, 64);
				event.entityPlayer.inventory.addItemStackToInventory(newStack);
			}
		}
	}
	
	@SubscribeEvent
	public void JoinWorld(EntityJoinWorldEvent event){
		if (event.entity instanceof EntityPlayer){
			Logger.chatLog((EntityPlayer)event.entity, "Joining new world");
		}
	}
	
	
	
	
}
