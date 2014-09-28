package com.hilburn.dimensionguard.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import com.hilburn.dimensionguard.ModInformation;

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
		//setTextureName("dimensionguard:lock");
	}

	public static ItemStack storeItem(ItemStack thisStack, ItemStack storeStack){
		thisStack.stackTagCompound = new NBTTagCompound();
		thisStack.stackSize=storeStack.stackSize;
		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		storeStack.stackSize=1;
		storeStack.stackTagCompound.getCompoundTag("DimensionGuard").removeTag("LastDimChecked");
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
	
	@Override
	public IIcon getIconFromDamage(int p_77617_1_) {
		return this.lock;
	}
	
    @Override
	public void registerIcons(IIconRegister reg) {
		this.lock=reg.registerIcon(ModInformation.MODID + ":lock");
	}
    
	@SuppressWarnings({"unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player,List list, boolean par4) {
		list.clear();
		list.add(itemstack.getDisplayName());
	}
}
