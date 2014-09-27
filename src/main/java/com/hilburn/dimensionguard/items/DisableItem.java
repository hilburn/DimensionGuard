package com.hilburn.dimensionguard.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.hilburn.dimensionguard.ModInformation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class DisableItem extends Item{
	
	private IIcon lock;
	
	public DisableItem(){
		super();
		setUnlocalizedName("disable");
		setCreativeTab(null);
		setTextureName("dimensionguard:lock");
	}

	public static ItemStack storeItem(ItemStack thisStack, ItemStack storeStack){
		thisStack.stackTagCompound = new NBTTagCompound();
		thisStack.stackSize=storeStack.stackSize;
		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		storeStack.stackSize=1;
		storeStack.writeToNBT(nbttagcompound1);
		//thisStack.stackTagCompound.setString("Store",storeStack.getDisplayName());
		thisStack.stackTagCompound.setInteger("MaxStackSize",storeStack.getMaxStackSize());
		thisStack.stackTagCompound.setTag("ItemStack", nbttagcompound1);
		thisStack.setStackDisplayName("Disabled: "+storeStack.getDisplayName());
		return thisStack;
	}
	
	public static ItemStack recoverItemStack(ItemStack stack){
		ItemStack result=ItemStack.loadItemStackFromNBT((NBTTagCompound) stack.stackTagCompound.getTag("ItemStack"));
		result.stackSize=stack.stackSize;
		return result;
	}
	
//	@Override
//	public ItemStack onItemRightClick(ItemStack thisStack, World world,EntityPlayer player) {
//		if (!world.isRemote){
//			thisStack=recoverItemStack(thisStack);
//		//player.inventory.decrStackSize(player.inventory.currentItem, 64);
//		}
//		return thisStack;
//	}
    
	@SuppressWarnings({"unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player,List list, boolean par4) {
		list.clear();
		list.add(itemstack.getDisplayName());
	}
}
