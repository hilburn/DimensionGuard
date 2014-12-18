package dimensionguard.handlers;

import cpw.mods.fml.common.eventhandler.EventPriority;
import dimensionguard.reference.Names;
import dimensionguard.utils.StackUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

import java.util.ArrayList;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class EventHandler
{

	@SubscribeEvent
	public void itemPickupEvent(EntityItemPickupEvent event){
		ItemStack stack = event.item.getEntityItem();
		if (DisabledHandler.isDisabledStack(stack,event.item.dimension))
		{
			StackUtils.addDisabledStack(event.entityPlayer, stack);
			event.item.setDead();
			event.item.worldObj.removeEntity(event.item);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent 
	public void checkSpawnEvent(LivingSpawnEvent.CheckSpawn event){
		if (DisabledHandler.isDisabledEntity(event.entityLiving.getClass(), event.entityLiving.dimension)) event.setResult(Result.DENY);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDropsEvent event)
	{
		if (ConfigHandler.dropDisabledItems)
		{
			EntityPlayer player = event.entityPlayer;
			NBTTagCompound persisted = player.getEntityData().getCompoundTag(player.PERSISTED_NBT_TAG);
			NBTTagList disabledItems = persisted.getTagList(Names.NBTTag, 10);
			for (int i = 0; i<disabledItems.tagCount(); i++)
			{
				NBTTagCompound stackCompound = disabledItems.getCompoundTagAt(i);
				ItemStack stack = ItemStack.loadItemStackFromNBT(stackCompound);

				EntityItem entityitem = new EntityItem(player.worldObj, player.posX, player.posY - 0.30000001192092896D + (double)player.getEyeHeight(), player.posZ, stack);
				entityitem.delayBeforeCanPickup = 40;
				entityitem.func_145799_b(player.getCommandSenderName());
				float f = 0.3F;
				entityitem.motionX = (double)(-MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI) * f);
				entityitem.motionZ = (double)(MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI) * f);
				entityitem.motionY = (double)(-MathHelper.sin(player.rotationPitch / 180.0F * (float)Math.PI) * f + 0.1F);
				f = 0.02F;
				float f1 = player.worldObj.rand.nextFloat() * (float)Math.PI * 2.0F;
				f *= player.worldObj.rand.nextFloat();
				entityitem.motionX += Math.cos((double)f1) * (double)f;
				entityitem.motionY += (double)((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.1F);
				entityitem.motionZ += Math.sin((double)f1) * (double)f;

				event.drops.add(entityitem);
			}
			persisted.setTag(Names.NBTTag, new NBTTagList());
			player.getEntityData().setTag(player.PERSISTED_NBT_TAG,persisted);
		}
	}
}
