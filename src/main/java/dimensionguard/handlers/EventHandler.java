package dimensionguard.handlers;

import dimensionguard.utils.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
}
