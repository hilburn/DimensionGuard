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

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class DisableItem extends Item{
	
	private IIcon lock;
	
	public DisableItem(){
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
		thisStack.stackTagCompound.setString("Store",storeStack.getDisplayName());
		thisStack.stackTagCompound.setInteger("MaxStackSize",storeStack.getMaxStackSize());
		thisStack.stackTagCompound.setTag("ItemStack", nbttagcompound1);
		return thisStack;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack thisStack, World world,EntityPlayer player) {
		if (!world.isRemote){
			int stackSize=thisStack.stackSize;
			thisStack=ItemStack.loadItemStackFromNBT((NBTTagCompound) thisStack.stackTagCompound.getTag("ItemStack"));
			thisStack.stackSize=stackSize;
		//player.inventory.decrStackSize(player.inventory.currentItem, 64);
		}
		return thisStack;
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return stack.stackTagCompound.getInteger("MaxStackSize");
	}
	/*
	@Override
	@SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return false;
    }
	
//	/**
//     * Return the correct icon for rendering based on the supplied ItemStack and render pass.
//     *
//     * Defers to {@link #getIconFromDamageForRenderPass(int, int)}
//     * @param stack to render for
//     * @param pass the multi-render pass
//     * @return the icon
//     */
//	@Override
//    public IIcon getIcon(ItemStack stack, int pass)
//    {
//        /**
//         * Gets an icon index based on an item's damage value and the given render pass
//         */
//    	switch (pass){
//    	//case 0:
//    		//return getStoredIcon(stack);
//    	default:
//    		return this.lock;
//    	}
//        
//    }
/*	@Override
	public IIcon getIconFromDamage(int p_77617_1_) {
		return this.lock;
	}

    private IIcon getStoredIcon(ItemStack stack){
    	if (stack.stackTagCompound!=null){
    	ItemStack thisStack=ItemStack.loadItemStackFromNBT((NBTTagCompound) stack.stackTagCompound.getTag("ItemStack"));
    	return thisStack.getItem().getIcon(thisStack, 0);
    	}else return null;
    }
    
    public int getRenderPasses(int metadata)
    {
        return requiresMultipleRenderPasses() ? 3 : 1;
    }*/
	
//    @Override
//	public void registerIcons(IIconRegister reg) {
//		this.lock=reg.registerIcon(ModInformation.MODID + ":lock");
//	}
    
	@SuppressWarnings({"unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player,List list, boolean par4) {
		if (itemstack.stackTagCompound!=null){
			list.clear();
			list.add("Stored Item: "+itemstack.stackTagCompound.getString("Store"));
		}
	}
}
