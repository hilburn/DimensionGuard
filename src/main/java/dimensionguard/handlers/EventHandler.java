package dimensionguard.handlers;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;


/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class EventHandler
{

	@SubscribeEvent
	public void checkSpawnEvent(LivingSpawnEvent.CheckSpawn event){
		if (DisabledHandler.isDisabledEntity(event.entityLiving.getClass(), event.entityLiving.dimension)) event.setResult(Result.DENY);
	}

	@SubscribeEvent
	public void hitEntity(AttackEntityEvent event)
	{
		if (DisabledHandler.isDisabledStack(event.entityPlayer)) event.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (DisabledHandler.isDisabledStack(event.entityPlayer)) event.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void onPlayerLoad(PlayerEvent.LoadFromFile event)
	{
		CommonEventHandler.checkInventory(event.entityPlayer, event.entity.worldObj.provider.dimensionId, Integer.MIN_VALUE);
	}

}
