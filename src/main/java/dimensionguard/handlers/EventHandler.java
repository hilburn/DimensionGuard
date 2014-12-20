package dimensionguard.handlers;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dimensionguard.handlers.DisabledHandler;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

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

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void loadTextures(TextureStitchEvent.Pre evt) {
		if (evt.map.getTextureType() == 1) {
			DisabledHandler.lockedIcon=evt.map.registerIcon("dimensionguard:lock");
		}
	}

}
